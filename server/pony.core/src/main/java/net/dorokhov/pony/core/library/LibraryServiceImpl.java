package net.dorokhov.pony.core.library;

import net.dorokhov.pony.core.audio.SongDataService;
import net.dorokhov.pony.core.audio.data.SongDataReadable;
import net.dorokhov.pony.core.audio.data.SongDataWritable;
import net.dorokhov.pony.core.common.PageProcessor;
import net.dorokhov.pony.core.common.Partition;
import net.dorokhov.pony.core.dao.AlbumDao;
import net.dorokhov.pony.core.dao.ArtistDao;
import net.dorokhov.pony.core.dao.GenreDao;
import net.dorokhov.pony.core.dao.SongDao;
import net.dorokhov.pony.core.domain.*;
import net.dorokhov.pony.core.image.ThumbnailService;
import net.dorokhov.pony.core.library.file.LibraryImage;
import net.dorokhov.pony.core.library.file.LibrarySong;
import net.dorokhov.pony.core.logging.LogService;
import net.dorokhov.pony.core.storage.StoreFileCommand;
import net.dorokhov.pony.core.storage.StoredFileService;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.mutable.MutableInt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.ObjectUtils;

import java.io.File;
import java.util.*;

@Service
public class LibraryServiceImpl implements LibraryService {

	private static final int CLEANING_BUFFER_SIZE = 300;

	private final Logger log = LoggerFactory.getLogger(getClass());

	private final Object lock = new Object();

	private TransactionTemplate newTransactionTemplate;
	private TransactionTemplate readOnlyTransactionTemplate;

	private LogService logService;

	private SongDao songDao;

	private AlbumDao albumDao;

	private ArtistDao artistDao;

	private GenreDao genreDao;

	private StoredFileService storedFileService;

	private SongDataService songDataService;

	private ThumbnailService thumbnailService;

	private ArtworkDiscoveryService artworkDiscoveryService;

	@Autowired
	public void setTransactionManager(PlatformTransactionManager aTransactionManager) {

		newTransactionTemplate = new TransactionTemplate(aTransactionManager, new DefaultTransactionDefinition(TransactionDefinition.PROPAGATION_REQUIRES_NEW));

		DefaultTransactionDefinition readOnlyDefinition = new DefaultTransactionDefinition();

		readOnlyDefinition.setReadOnly(true);

		readOnlyTransactionTemplate = new TransactionTemplate(aTransactionManager, readOnlyDefinition);
	}

	@Autowired
	public void setLogService(LogService aLogService) {
		logService = aLogService;
	}

	@Autowired
	public void setSongDao(SongDao aSongDao) {
		songDao = aSongDao;
	}

	@Autowired
	public void setAlbumDao(AlbumDao aAlbumDao) {
		albumDao = aAlbumDao;
	}

	@Autowired
	public void setArtistDao(ArtistDao aArtistDao) {
		artistDao = aArtistDao;
	}

	@Autowired
	public void setGenreDao(GenreDao aGenreDao) {
		genreDao = aGenreDao;
	}

	@Autowired
	public void setStoredFileService(StoredFileService aStoredFileService) {
		storedFileService = aStoredFileService;
	}

	@Autowired
	public void setSongDataService(SongDataService aSongDataService) {
		songDataService = aSongDataService;
	}

	@Autowired
	public void setThumbnailService(ThumbnailService aThumbnailService) {
		thumbnailService = aThumbnailService;
	}

	@Autowired
	public void setArtworkDiscoveryService(ArtworkDiscoveryService aArtworkDiscoveryService) {
		artworkDiscoveryService = aArtworkDiscoveryService;
	}

	@Override
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public void cleanSongs(List<LibrarySong> aSongFiles, final ProgressDelegate aDelegate) {

		if (aDelegate != null) {
			aDelegate.onProgress(-1.0);
		}

		final Set<String> songPaths = new HashSet<>();
		for (LibrarySong songFile : aSongFiles) {
			songPaths.add(songFile.getFile().getAbsolutePath());
		}

		final List<Long> songsToDelete = new ArrayList<>();

		readOnlyTransactionTemplate.execute(new TransactionCallbackWithoutResult() {
			@Override
			protected void doInTransactionWithoutResult(TransactionStatus status) {
				PageProcessor.Handler<Song> handler = new PageProcessor.Handler<Song>() {
					@Override
					public void process(Song aSong, Page<Song> aPage, int aIndexInPage, long aIndexInAll) {
						if (!songPaths.contains(aSong.getPath())) {
							songsToDelete.add(aSong.getId());
						}
					}

					@Override
					public Page<Song> getPage(Pageable aPageable) {
						return songDao.findAll(aPageable);
					}
				};
				new PageProcessor<>(CLEANING_BUFFER_SIZE, new Sort("id"), handler).run();
			}
		});

		if (aDelegate != null) {
			aDelegate.onProgress(0.0);
		}

		final MutableInt i = new MutableInt();

		for (final List<Long> chunk : Partition.partition(songsToDelete, CLEANING_BUFFER_SIZE)) {
			newTransactionTemplate.execute(new TransactionCallbackWithoutResult() {
				@Override
				protected void doInTransactionWithoutResult(TransactionStatus status) {
					for (Long id : chunk) {

						Song song = songDao.findOne(id);

						if (song != null) {

							logService.debug(log, "libraryService.deletingNotFoundSong",
									"Deleting song [" + song + "], song file not found [" + song.getPath() + "].",
									Arrays.asList(song.toString(), song.getPath()));

							songDao.delete(song);

							deleteGenreIfNotUsed(song.getGenre());
							deleteAlbumIfNotUsed(song.getAlbum());
							deleteArtistIfNotUsed(song.getAlbum().getArtist());
							deleteArtworkIfNotUsed(song.getArtwork());
						}

						if (aDelegate != null) {
							aDelegate.onProgress((i.getValue() + 1) / (double) songsToDelete.size());
						}

						i.increment();
					}
				}
			});
		}
	}

	@Override
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public void cleanArtworks(List<LibraryImage> aImageFiles, final ProgressDelegate aDelegate) {

		if (aDelegate != null) {
			aDelegate.onProgress(-1.0);
		}

		final Set<String> imagePaths = new HashSet<>();
		for (LibraryImage imageFile : aImageFiles) {
			imagePaths.add(imageFile.getFile().getAbsolutePath());
		}

		final List<ExternalArtworkDeletionTask> artworksToDelete = new ArrayList<>();

		readOnlyTransactionTemplate.execute(new TransactionCallbackWithoutResult() {
			@Override
			protected void doInTransactionWithoutResult(TransactionStatus status) {
				PageProcessor.Handler<StoredFile> handler = new PageProcessor.Handler<StoredFile>() {
					@Override
					public void process(StoredFile aStoredFile, Page<StoredFile> aPage, int aIndexInPage, long aIndexInAll) {

						String externalFilePath = aStoredFile.getUserData();

						if (!imagePaths.contains(externalFilePath)) {

							artworksToDelete.add(new ExternalArtworkDeletionTask(aStoredFile.getId(), externalFilePath, ExternalArtworkDeletionReason.NOT_FOUND));

						} else {

							File externalFile = new File(externalFilePath);

							if (aStoredFile.getDate().getTime() < externalFile.lastModified()) {
								artworksToDelete.add(new ExternalArtworkDeletionTask(aStoredFile.getId(), externalFilePath, ExternalArtworkDeletionReason.MODIFIED));
							}
						}
					}

					@Override
					public Page<StoredFile> getPage(Pageable aPageable) {
						return storedFileService.getByTag(StoredFile.TAG_ARTWORK_FILE, aPageable);
					}
				};
				new PageProcessor<>(CLEANING_BUFFER_SIZE, new Sort("id"), handler).run();
			}
		});

		if (aDelegate != null) {
			aDelegate.onProgress(0.0);
		}

		final MutableInt i = new MutableInt();

		for (final List<ExternalArtworkDeletionTask> chunk : Partition.partition(artworksToDelete, CLEANING_BUFFER_SIZE)) {
			newTransactionTemplate.execute(new TransactionCallbackWithoutResult() {
				@Override
				protected void doInTransactionWithoutResult(TransactionStatus status) {
					for (ExternalArtworkDeletionTask task : chunk) {

						StoredFile storedFile = storedFileService.getById(task.getId());

						switch (task.getReason()) {
							case NOT_FOUND:
								logService.debug(log, "libraryService.deletingNotFoundStoredFile",
										"Deleting file artwork [" + storedFile + "], artwork file not found [" + task.getPath() + "].",
										Arrays.asList(storedFile.toString(), task.getPath()));
								break;
							case MODIFIED:
								logService.debug(log, "libraryService.deletingModifiedStoredFile",
										"Deleting file artwork [" + storedFile + "], artwork file modified [" + task.getPath() + "].",
										Arrays.asList(storedFile.toString(), task.getPath()));
								break;
						}

						songDao.clearArtworkByArtworkId(task.getId());
						albumDao.clearArtworkByArtworkId(task.getId());
						artistDao.clearArtworkByArtworkId(task.getId());
						genreDao.clearArtworkByArtworkId(task.getId());

						storedFileService.delete(task.getId());

						if (aDelegate != null) {
							aDelegate.onProgress((i.getValue() + 1) / (double) artworksToDelete.size());
						}

						i.increment();
					}
				}
			});
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void normalize(final ProgressDelegate aDelegate) {

		final long genreCount = genreDao.countByArtworkId(null);
		final long artistCount = artistDao.countByArtworkId(null);

		final long entityCount = genreCount + artistCount;

		PageProcessor.Handler<Genre> genreHandler = new PageProcessor.Handler<Genre>() {

			@Override
			public void process(Genre aGenre, Page<Genre> aPage, int aIndexInPage, long aIndexInAll) {

				long genreSongCount = songDao.countByGenreIdAndArtworkNotNull(aGenre.getId());

				if (genreSongCount > 0) {

					Page<Song> songPage = songDao.findByGenreIdAndArtworkNotNull(aGenre.getId(),
							new PageRequest((int) Math.floor(genreSongCount / 2.0), 1, new Sort(Sort.Direction.ASC, "year")));

					if (songPage.hasContent()) {

						StoredFile artwork = songPage.getContent().get(0).getArtwork();

						logService.debug(log, "libraryService.settingGenreArtwork", "Setting genre artwork " + aGenre + " with " + artwork,
								Arrays.asList(aGenre.toString(), artwork.toString()));

						aGenre.setArtwork(artwork);

						genreDao.save(aGenre);
					}
				}

				if (aDelegate != null) {
					aDelegate.onProgress((aIndexInAll + 1) / (double) entityCount);
				}
			}

			@Override
			public Page<Genre> getPage(Pageable aPageable) {
				return genreDao.findByArtworkId(null, aPageable);
			}
		};
		new PageProcessor<>(CLEANING_BUFFER_SIZE, new Sort("id"), genreHandler).run();

		PageProcessor.Handler<Artist> artistHandler = new PageProcessor.Handler<Artist>() {

			@Override
			public void process(Artist aArtist, Page<Artist> aPage, int aIndexInPage, long aIndexInAll) {

				long artistAlbumCount = albumDao.countByArtistIdAndArtworkNotNull(aArtist.getId());

				if (artistAlbumCount > 0) {

					Page<Album> albumPage = albumDao.findByArtistIdAndArtworkNotNull(aArtist.getId(),
							new PageRequest((int) Math.floor(artistAlbumCount / 2.0), 1, Sort.Direction.ASC, "year"));

					if (albumPage.hasContent()) {

						StoredFile artwork = albumPage.getContent().get(0).getArtwork();

						logService.debug(log, "libraryService.settingArtistArtwork", "Setting artist artwork " + aArtist + " with " + artwork,
								Arrays.asList(aArtist.toString(), artwork.toString()));

						aArtist.setArtwork(artwork);

						artistDao.save(aArtist);
					}
				}

				if (aDelegate != null) {
					aDelegate.onProgress((genreCount + aIndexInAll + 1) / (double) entityCount);
				}
			}

			@Override
			public Page<Artist> getPage(Pageable aPageable) {
				return artistDao.findByArtworkId(null, aPageable);
			}
		};
		new PageProcessor<>(CLEANING_BUFFER_SIZE, new Sort("id"), artistHandler).run();
	}

	@Override
	@Transactional(readOnly = true)
	public Song importSong(final LibrarySong aSongFile) {

		Song song = songDao.findByPath(aSongFile.getFile().getAbsolutePath());

		boolean shouldImport = (song == null);

		if (!shouldImport) {
			if (song.getUpdateDate() != null) {
				shouldImport = (song.getUpdateDate().getTime() < aSongFile.getFile().lastModified());
			} else {
				shouldImport = (song.getCreationDate().getTime() < aSongFile.getFile().lastModified());
			}
		}

		if (shouldImport) {

			final SongDataReadable songData;

			try {
				songData = songDataService.read(aSongFile.getFile());
			} catch (Exception e) {
				throw new RuntimeException("Could not read song data from [" + aSongFile.getFile().getAbsolutePath() + "]", e);
			}

			synchronized (lock) {
				song = newTransactionTemplate.execute(new TransactionCallback<Song>() {
					@Override
					public Song doInTransaction(TransactionStatus status) {
						return importSong(aSongFile, songData);
					}
				});
			}

		} else {
			if (song.getArtwork() == null) {
				synchronized (lock) {
					song = newTransactionTemplate.execute(new TransactionCallback<Song>() {
						@Override
						public Song doInTransaction(TransactionStatus status) {

							Song song = songDao.findByPath(aSongFile.getFile().getAbsolutePath());

							if (song != null && song.getArtwork() == null) {
								song = discoverSongArtwork(song, aSongFile);
							}

							return song;
						}
					});
				}
			}
		}

		return song;
	}

	@Override
	@Transactional(readOnly = true)
	public Song writeAndImportSong(final LibrarySong aSongFile, final SongDataWritable aSongData) {

		Song song = songDao.findByPath(aSongFile.getFile().getAbsolutePath());

		if (song != null) {

			final SongDataReadable updatedSongData;

			try {
				updatedSongData = songDataService.write(new File(song.getPath()), aSongData);
			} catch (Exception e) {
				throw new RuntimeException("Could not write data " + aSongData + " to song [" + song.getPath() + "]");
			}

			synchronized (lock) {
				song = newTransactionTemplate.execute(new TransactionCallback<Song>() {
					@Override
					public Song doInTransaction(TransactionStatus status) {
						return importSong(aSongFile, updatedSongData);
					}
				});
			}
		}

		return song;
	}

	private Song importSong(LibrarySong aSongFile, SongDataReadable aSongData) {
		return importSong(aSongFile, aSongData, importAlbum(aSongData, importArtist(aSongData)), importGenre(aSongData));
	}

	private Song importSong(LibrarySong aSongFile, SongDataReadable aSongData, Album aAlbum, Genre aGenre) {

		boolean shouldSave = false;

		Song song = songDao.findByPath(aSongData.getPath());

		Album overriddenAlbum = null;
		Genre overriddenGenre = null;
		StoredFile overriddenArtwork = null;

		if (song == null) {

			song = new Song();
			song.setPath(aSongData.getPath());

			shouldSave = true;
		}

		StoredFile artwork = discoverEmbeddedArtwork(aSongData);
		if (artwork == null) {
			artwork = discoverFileArtwork(null, aSongData, aSongFile);
		}

		if (song.getId() != null) {
			if (!ObjectUtils.nullSafeEquals(song.getFormat(), aSongData.getFormat()) ||
					!ObjectUtils.nullSafeEquals(song.getMimeType(), aSongData.getMimeType()) ||
					!ObjectUtils.nullSafeEquals(song.getSize(), aSongData.getSize()) ||
					!ObjectUtils.nullSafeEquals(song.getDuration(), aSongData.getDuration()) ||
					!ObjectUtils.nullSafeEquals(song.getBitRate(), aSongData.getBitRate()) ||

					!ObjectUtils.nullSafeEquals(song.getDiscNumber(), aSongData.getDiscNumber()) ||
					!ObjectUtils.nullSafeEquals(song.getDiscCount(), aSongData.getDiscCount()) ||

					!ObjectUtils.nullSafeEquals(song.getTrackNumber(), aSongData.getTrackNumber()) ||
					!ObjectUtils.nullSafeEquals(song.getTrackCount(), aSongData.getTrackCount()) ||

					!ObjectUtils.nullSafeEquals(song.getName(), aSongData.getTitle()) ||
					!ObjectUtils.nullSafeEquals(song.getArtistName(), aSongData.getArtist()) ||
					!ObjectUtils.nullSafeEquals(song.getAlbumArtistName(), aSongData.getAlbumArtist()) ||
					!ObjectUtils.nullSafeEquals(song.getAlbumName(), aSongData.getAlbum()) ||
					!ObjectUtils.nullSafeEquals(song.getYear(), aSongData.getYear())) {

				shouldSave = true;
			}
			if (!ObjectUtils.nullSafeEquals(song.getAlbum(), aAlbum)) {

				overriddenAlbum = song.getAlbum();

				shouldSave = true;
			}
			if (!ObjectUtils.nullSafeEquals(song.getGenre(), aGenre)) {

				overriddenGenre = song.getGenre();

				shouldSave = true;
			}
			if (!ObjectUtils.nullSafeEquals(song.getArtwork(), artwork)) {

				overriddenArtwork = song.getArtwork();

				shouldSave = true;
			}
		}

		if (shouldSave) {

			song.setFormat(aSongData.getFormat());
			song.setMimeType(aSongData.getMimeType());
			song.setSize(aSongData.getSize());
			song.setDuration(aSongData.getDuration());
			song.setBitRate(aSongData.getBitRate());

			song.setDiscNumber(aSongData.getDiscNumber());
			song.setDiscCount(aSongData.getDiscCount());

			song.setTrackNumber(aSongData.getTrackNumber());
			song.setTrackCount(aSongData.getTrackCount());

			song.setName(aSongData.getTitle());
			song.setGenreName(aSongData.getGenre());
			song.setArtistName(aSongData.getArtist());
			song.setAlbumArtistName(aSongData.getAlbumArtist());
			song.setAlbumName(aSongData.getAlbum());
			song.setYear(aSongData.getYear());

			song.setAlbum(aAlbum);
			song.setGenre(aGenre);
			song.setArtwork(artwork);

			boolean newSong = (song.getId() == null);

			song = songDao.save(song);

			if (newSong) {
				logService.debug(log, "libraryService.creatingSong", "Creating song " + song + ".",
						Arrays.asList(song.toString()));
			} else {
				logService.debug(log, "libraryService.updatingSong", "Updating song " + song + ".",
						Arrays.asList(song.toString()));
			}

			deleteAlbumIfNotUsed(overriddenAlbum);
			deleteArtistIfNotUsed(overriddenAlbum != null ? overriddenAlbum.getArtist() : null);
			deleteGenreIfNotUsed(overriddenGenre);
			deleteArtworkIfNotUsed(overriddenArtwork);

			song.setAlbum(albumDao.findOne(song.getAlbum().getId()));

			if (artwork != null && song.getAlbum().getArtwork() == null) {

				logService.debug(log, "libraryService.settingAlbumArtwork", "Setting album artwork " + song.getAlbum() + " with " + artwork,
						Arrays.asList(song.getAlbum().toString(), artwork.toString()));

				song.getAlbum().setArtwork(artwork);

				albumDao.save(song.getAlbum());
			}
		}

		return song;
	}

	private Genre importGenre(SongDataReadable aSongData) {

		String genreName = StringUtils.defaultIfEmpty(StringUtils.normalizeSpace(aSongData.getGenre()), null);

		Genre genre = genreDao.findByName(genreName);

		boolean shouldSave = false;

		if (genre == null) {

			genre = new Genre();

			shouldSave = true;
		}

		if (genre.getId() != null) {
			if (!ObjectUtils.nullSafeEquals(genre.getName(), genreName)) {
				shouldSave = true;
			}
		}

		if (shouldSave) {

			genre.setName(genreName);

			boolean newGenre = (genre.getId() == null);

			genre = genreDao.save(genre);

			if (newGenre) {
				logService.debug(log, "libraryService.creatingGenre", "Creating genre " + genre + ".",
						Arrays.asList(genre.toString()));
			} else {
				logService.debug(log, "libraryService.updatingGenre", "Updating genre " + genre + ".",
						Arrays.asList(genre.toString()));
			}
		}

		return genre;
	}

	private Artist importArtist(SongDataReadable aSongData) {

		String artistName = aSongData.getAlbumArtist();
		if (artistName == null) {
			artistName = aSongData.getArtist();
		}
		artistName = StringUtils.defaultIfEmpty(StringUtils.normalizeSpace(artistName), null);

		Artist artist = artistDao.findByName(artistName);

		boolean shouldSave = false;

		if (artist == null) {

			artist = new Artist();

			shouldSave = true;
		}

		if (artist.getId() != null) {
			if (!ObjectUtils.nullSafeEquals(artist.getName(), artistName)) {
				shouldSave = true;
			}
		}

		if (shouldSave) {

			artist.setName(artistName);

			boolean newArtist = (artist.getId() == null);

			artist = artistDao.save(artist);

			if (newArtist) {
				logService.debug(log, "libraryService.creatingArtist", "Creating artist " + artist + ".",
						Arrays.asList(artist.toString()));
			} else {
				logService.debug(log, "libraryService.updatingArtist", "Updating artist " + artist + ".",
						Arrays.asList(artist.toString()));
			}
		}

		return artist;
	}

	private Album importAlbum(SongDataReadable aSongData, Artist aArtist) {

		String albumName = StringUtils.defaultIfEmpty(StringUtils.normalizeSpace(aSongData.getAlbum()), null);

		Album album = albumDao.findByArtistIdAndName(aArtist.getId(), albumName);

		boolean shouldSave = false;

		if (album == null) {

			album = new Album();
			album.setArtist(aArtist);

			shouldSave = true;
		}

		if (album.getId() != null) {
			if (!ObjectUtils.nullSafeEquals(album.getName(), albumName) ||
					!ObjectUtils.nullSafeEquals(album.getYear(), aSongData.getYear())) {

				shouldSave = true;
			}
		}

		if (shouldSave) {

			album.setName(albumName);
			album.setYear(aSongData.getYear());

			boolean newAlbum = (album.getId() == null);

			album = albumDao.save(album);

			if (newAlbum) {
				logService.debug(log, "libraryService.creatingAlbum", "Creating album " + album + ".",
						Arrays.asList(album.toString()));
			} else {
				logService.debug(log, "libraryService.updatingAlbum", "Updating album " + album + ".",
						Arrays.asList(album.toString()));
			}
		}

		return album;
	}

	private Song discoverSongArtwork(Song aSong, LibrarySong aSongFile) {

		StoredFile artwork = discoverFileArtwork(aSong, null, aSongFile);

		if (artwork != null) {

			aSong.setArtwork(artwork);

			aSong = songDao.save(aSong);

			if (aSong.getAlbum().getArtwork() == null) {

				logService.debug(log, "libraryService.settingAlbumArtwork", "Setting album artwork " + aSong.getAlbum() + " with " + artwork,
						Arrays.asList(aSong.getAlbum().toString(), artwork.toString()));

				aSong.getAlbum().setArtwork(artwork);

				albumDao.save(aSong.getAlbum());
			}
		}

		return aSong;
	}

	private StoredFile discoverEmbeddedArtwork(SongDataReadable aSongData) {

		StoredFile artwork = null;

		if (aSongData.getArtwork() != null && aSongData.getArtwork().getChecksum() != null) {

			artwork = storedFileService.getByTagAndChecksum(StoredFile.TAG_ARTWORK_EMBEDDED, aSongData.getArtwork().getChecksum());

			if (artwork == null) {

				StoreFileCommand command = null;

				try {
					command = buildEmbeddedArtworkStoreCommand(aSongData);
				} catch (Exception e) {
					logService.warn(log, "libraryService.couldNotStoreEmbeddedArtwork", "Could not store embedded artwork of " + aSongData.toString() + ".",
							e, Arrays.asList(aSongData.toString()));
				}

				if (command != null) {

					artwork = storedFileService.save(command);

					logService.debug(log, "libraryService.storingEmbeddedArtwork", "Storing embedded artwork " + artwork + ".",
							Arrays.asList(artwork.toString()));
				}
			}
		}

		return artwork;
	}

	private StoredFile discoverFileArtwork(Song aSong, SongDataReadable aSongData, LibrarySong aSongFile) {

		StoredFile artwork = null;

		LibraryImage artworkImage = artworkDiscoveryService.discoverArtwork(aSongFile);

		if (artworkImage != null) {

			String mimeType = artworkImage.getMimeType();

			if (mimeType != null) {

				String checksum = null;

				try {
					checksum = artworkImage.getChecksum();
				} catch (Exception e) {
					logService.warn(log, "libraryService.couldNotStoreFileArtwork", "Could not store file artwork", e);
				}

				if (checksum != null) {

					artwork = storedFileService.getByTagAndChecksum(StoredFile.TAG_ARTWORK_FILE, checksum);

					if (artwork == null) {

						StoreFileCommand command = null;

						try {
							command = buildFileArtworkStoreCommand(aSong, aSongData, artworkImage, mimeType, checksum);
						} catch (Exception e) {
							logService.warn(log, "libraryService.couldNotStoreFileArtwork", "Could not store file artwork", e);
						}

						if (command != null) {

							artwork = storedFileService.save(command);

							logService.debug(log, "libraryService.storingFileArtwork", "Storing file artwork " + artwork + ".",
									Arrays.asList(artwork.toString()));
						}
					}
				}
			}
		}

		return artwork;
	}

	private StoreFileCommand buildEmbeddedArtworkStoreCommand(SongDataReadable aSongData) throws Exception {

		File file = new File(FileUtils.getTempDirectory(), "pony." + StoredFile.TAG_ARTWORK_EMBEDDED + "." + UUID.randomUUID() + ".tmp");

		thumbnailService.makeThumbnail(aSongData.getArtwork().getBinaryData(), file);

		StoreFileCommand command = new StoreFileCommand(StoreFileCommand.Type.MOVE, file);

		command.setName(aSongData.getArtist() + " " + aSongData.getAlbum() + " " + aSongData.getTitle());
		command.setMimeType(aSongData.getArtwork().getMimeType());
		command.setChecksum(aSongData.getArtwork().getChecksum());
		command.setTag(StoredFile.TAG_ARTWORK_EMBEDDED);

		return command;
	}

	private StoreFileCommand buildFileArtworkStoreCommand(Song aSong, SongDataReadable aSongData, LibraryImage aArtwork, String aMimeType, String aChecksum) throws Exception {

		File file = new File(FileUtils.getTempDirectory(), "pony." + StoredFile.TAG_ARTWORK_FILE + "." + UUID.randomUUID() + ".tmp");

		thumbnailService.makeThumbnail(aArtwork.getFile(), file);

		String artist = null;
		if (aSong != null) {
			artist = aSong.getArtistName();
		} else if (aSongData != null) {
			artist = aSongData.getArtist();
		}

		String album = null;
		if (aSong != null) {
			album = aSong.getAlbumName();
		} else if (aSongData != null) {
			album = aSongData.getAlbum();
		}

		String title = null;
		if (aSong != null) {
			title = aSong.getName();
		} else if (aSongData != null) {
			title = aSongData.getTitle();
		}

		StoreFileCommand command = new StoreFileCommand(StoreFileCommand.Type.MOVE, file);

		command.setName(artist + " " + album + " " + title);
		command.setMimeType(aMimeType);
		command.setChecksum(aChecksum);
		command.setTag(StoredFile.TAG_ARTWORK_FILE);
		command.setUserData(aArtwork.getFile().getAbsolutePath());

		return command;
	}

	private void deleteAlbumIfNotUsed(Album aAlbum) {
		if (aAlbum != null && songDao.countByAlbumId(aAlbum.getId()) == 0) {

			albumDao.delete(aAlbum);

			logService.debug(log, "libraryService.deletingAlbum", "Deleting album without songs " + aAlbum + ".",
					Arrays.asList(aAlbum.toString()));
		}
	}

	private void deleteArtistIfNotUsed(Artist aArtist) {
		if (aArtist != null && albumDao.countByArtistId(aArtist.getId()) == 0) {

			artistDao.delete(aArtist);

			logService.debug(log, "libraryService.deletingArtist", "Deleting artist without albums " + aArtist + ".",
					Arrays.asList(aArtist.toString()));
		}
	}

	private void deleteGenreIfNotUsed(Genre aGenre) {
		if (aGenre != null && songDao.countByGenreId(aGenre.getId()) == 0) {

			genreDao.delete(aGenre);

			logService.debug(log, "libraryService.deletingGenre", "Deleting genre without songs " + aGenre + ".",
					Arrays.asList(aGenre.toString()));
		}
	}

	private void deleteArtworkIfNotUsed(StoredFile aArtwork) {
		if (aArtwork != null && songDao.countByArtworkId(aArtwork.getId()) == 0) {

			logService.debug(log, "libraryService.deletingNotUsedArtwork",
					"Deleting not used artwork " + aArtwork + ".",
					Arrays.asList(aArtwork.toString()));

			songDao.clearArtworkByArtworkId(aArtwork.getId());
			albumDao.clearArtworkByArtworkId(aArtwork.getId());
			artistDao.clearArtworkByArtworkId(aArtwork.getId());
			genreDao.clearArtworkByArtworkId(aArtwork.getId());

			storedFileService.delete(aArtwork.getId());
		}
	}

	private enum ExternalArtworkDeletionReason {
		NOT_FOUND, MODIFIED
	}

	private class ExternalArtworkDeletionTask {

		private final Long id;

		private final String path;

		private final ExternalArtworkDeletionReason reason;

		public ExternalArtworkDeletionTask(Long aId, String aPath, ExternalArtworkDeletionReason aReason) {
			id = aId;
			path = aPath;
			reason = aReason;
		}

		public Long getId() {
			return id;
		}

		public String getPath() {
			return path;
		}

		public ExternalArtworkDeletionReason getReason() {
			return reason;
		}
	}

}
