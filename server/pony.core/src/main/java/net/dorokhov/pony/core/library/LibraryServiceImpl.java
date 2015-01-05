package net.dorokhov.pony.core.library;

import net.dorokhov.pony.core.audio.SongDataService;
import net.dorokhov.pony.core.audio.data.SongDataReadable;
import net.dorokhov.pony.core.audio.data.SongDataWritable;
import net.dorokhov.pony.core.common.PageProcessor;
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
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.ObjectUtils;

import java.io.File;
import java.util.*;

@Service
public class LibraryServiceImpl implements LibraryService {

	private static final int CLEANING_BUFFER_SIZE = 300;

	private final Logger log = LoggerFactory.getLogger(getClass());

	private final Object lock = new Object();

	private TransactionTemplate transactionTemplate;

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
		transactionTemplate = new TransactionTemplate(aTransactionManager, new DefaultTransactionDefinition(TransactionDefinition.PROPAGATION_REQUIRES_NEW));
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
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void cleanSongs(List<LibrarySong> aSongFiles, final ProgressDelegate aDelegate) {

		final Set<String> songPaths = new HashSet<>();
		for (LibrarySong songFile : aSongFiles) {
			songPaths.add(songFile.getFile().getAbsolutePath());
		}

		final List<Long> itemsToDelete = new ArrayList<>();

		PageProcessor.Handler<Song> handler = new PageProcessor.Handler<Song>() {

			@Override
			public void process(Song aSong, Page<Song> aPage, int aIndexInPage, long aIndexInAll) {

				if (!songPaths.contains(aSong.getPath())) {

					itemsToDelete.add(aSong.getId());

					logService.debug(log, "libraryService.deletingNotFoundSong",
							"Song file not found [" + aSong.getPath() + "], deleting song [" + aSong + "].",
							Arrays.asList(aSong.getPath(), aSong.toString()));
				}

				if (aDelegate != null) {
					aDelegate.onProgress((aIndexInAll + 1) / (double) aPage.getTotalElements());
				}
			}

			@Override
			public Page<Song> getPage(Pageable aPageable) {
				return songDao.findAll(aPageable);
			}
		};
		new PageProcessor<>(CLEANING_BUFFER_SIZE, new Sort("id"), handler).run();

		for (Long id : itemsToDelete) {
			deleteSong(id);
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void cleanArtworks(List<LibraryImage> aImageFiles, final ProgressDelegate aDelegate) {

		final Set<String> imagePaths = new HashSet<>();
		for (LibraryImage imageFile : aImageFiles) {
			imagePaths.add(imageFile.getFile().getAbsolutePath());
		}

		final List<Long> itemsToDelete = new ArrayList<>();

		PageProcessor.Handler<StoredFile> handler = new PageProcessor.Handler<StoredFile>() {

			@Override
			public void process(StoredFile aStoredFile, Page<StoredFile> aPage, int aIndexInPage, long aIndexInAll) {

				boolean shouldDelete = false;

				if (aStoredFile.getTag() != null && aStoredFile.getTag().equals(StoredFile.TAG_ARTWORK_FILE)) {

					String externalFilePath = null;
					if (aStoredFile.getUserData() != null) {
						externalFilePath = aStoredFile.getUserData();
					}

					if (externalFilePath == null || !imagePaths.contains(externalFilePath)) {

						logService.debug(log, "libraryService.deletingNotFoundStoredFile",
								"Artwork file [" + externalFilePath + "] not found, deleting stored file [" + aStoredFile + "].",
								Arrays.asList(externalFilePath, aStoredFile.toString()));

						shouldDelete = true;

					} else {

						File externalFile = new File(externalFilePath);

						shouldDelete = (aStoredFile.getDate().getTime() < externalFile.lastModified());

						if (shouldDelete) {
							logService.debug(log, "libraryService.deletingModifiedStoredFile",
									"Artwork file [" + externalFilePath + "] modified, deleting stored file [" + aStoredFile + "].",
									Arrays.asList(externalFilePath, aStoredFile.toString()));
						}
					}
				}

				if (!shouldDelete && aStoredFile.getTag() != null) {
					if (aStoredFile.getTag().equals(StoredFile.TAG_ARTWORK_EMBEDDED) || aStoredFile.getTag().equals(StoredFile.TAG_ARTWORK_FILE)) {
						shouldDelete = (songDao.countByArtworkId(aStoredFile.getId()) == 0);
					}
				}

				if (shouldDelete) {
					itemsToDelete.add(aStoredFile.getId());
				}

				if (aDelegate != null) {
					aDelegate.onProgress((aIndexInAll + 1) / (double) aPage.getTotalElements());
				}
			}

			@Override
			public Page<StoredFile> getPage(Pageable aPageable) {
				return storedFileService.getAll(aPageable);
			}
		};
		new PageProcessor<>(CLEANING_BUFFER_SIZE, new Sort("id"), handler).run();

		for (final Long id : itemsToDelete) {
			deleteArtwork(id);
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

					if (songPage.getNumberOfElements() > 0) {

						StoredFile artwork = songPage.getContent().get(0).getArtwork();

						logService.debug(log, "libraryService.settingGenreArtwork", "Setting genre " + aGenre + " artwork " + artwork,
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

					if (albumPage.getNumberOfElements() > 0) {

						StoredFile artwork = albumPage.getContent().get(0).getArtwork();

						logService.debug(log, "libraryService.settingArtistArtwork", "Setting artist " + aArtist + " artwork " + artwork,
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
				song = transactionTemplate.execute(new TransactionCallback<Song>() {
					@Override
					public Song doInTransaction(TransactionStatus status) {
						return importSong(aSongFile, songData);
					}
				});
			}

		} else {
			if (song.getArtwork() == null) {
				synchronized (lock) {
					song = transactionTemplate.execute(new TransactionCallback<Song>() {
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
				song = transactionTemplate.execute(new TransactionCallback<Song>() {
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
					!ObjectUtils.nullSafeEquals(song.getYear(), aSongData.getYear()) ||
					!ObjectUtils.nullSafeEquals(song.getArtwork(), artwork)) {

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
			song.setArtistName(aSongData.getArtist());
			song.setAlbumArtistName(aSongData.getAlbumArtist());
			song.setAlbumName(aSongData.getAlbum());
			song.setYear(aSongData.getYear());

			song.setAlbum(aAlbum);
			song.setGenre(aGenre);
			song.setArtwork(artwork);

			boolean newSong = (song.getId() == null);

			song = songDao.save(song);

			deleteEntitiesWithoutSongs(overriddenAlbum, overriddenAlbum != null ? overriddenAlbum.getArtist() : null, overriddenGenre, overriddenArtwork);

			if (newSong) {
				logService.debug(log, "libraryService.creatingSong", "Creating song " + song + ".",
						Arrays.asList(song.toString()));
			} else {
				logService.debug(log, "libraryService.updatingSong", "Updating song " + song + ".",
						Arrays.asList(song.toString()));
			}

			song.setAlbum(albumDao.findOne(song.getAlbum().getId()));

			if (artwork != null && song.getAlbum().getArtwork() == null) {

				logService.debug(log, "libraryService.settingAlbumArtwork", "Setting album " + song.getAlbum() + " artwork " + artwork,
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

		Album album = albumDao.findByArtistIdAndName(aArtist.getId(), aSongData.getAlbum());

		boolean shouldSave = false;

		if (album == null) {

			album = new Album();

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

			album.setArtist(aArtist);

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

				logService.debug(log, "libraryService.settingAlbumArtwork", "Setting album " + aSong.getAlbum() + " artwork " + artwork,
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

	private void deleteSong(Long aId) {

		Song song = songDao.findOne(aId);

		songDao.delete(song);

		deleteEntitiesWithoutSongs(song.getAlbum(), song.getAlbum().getArtist(), song.getGenre(), song.getArtwork());
	}

	private void deleteArtwork(Long aId) {

		songDao.clearArtworkByArtworkId(aId);
		albumDao.clearArtworkByArtworkId(aId);
		artistDao.clearArtworkByArtworkId(aId);
		genreDao.clearArtworkByArtworkId(aId);

		storedFileService.delete(aId);
	}

	private void deleteEntitiesWithoutSongs(Album aAlbum, Artist aArtist, Genre aGenre, StoredFile aArtwork) {

		if (aAlbum != null && songDao.countByAlbumId(aAlbum.getId()) == 0) {

			albumDao.delete(aAlbum);

			logService.debug(log, "libraryService.deletingAlbum", "Deleting album without songs " + aAlbum + ".",
					Arrays.asList(aAlbum.toString()));
		}
		if (aArtist != null && albumDao.countByArtistId(aArtist.getId()) == 0) {

			artistDao.delete(aArtist);

			logService.debug(log, "libraryService.deletingArtist", "Deleting artist without albums " + aArtist + ".",
					Arrays.asList(aArtist.toString()));
		}
		if (aGenre != null && songDao.countByGenreId(aGenre.getId()) == 0) {

			genreDao.delete(aGenre);

			logService.debug(log, "libraryService.deletingGenre", "Deleting genre without songs " + aGenre + ".",
					Arrays.asList(aGenre.toString()));
		}
		if (aArtwork != null && songDao.countByArtworkId(aArtwork.getId()) == 0) {

			logService.debug(log, "libraryService.deletingNotUsedArtwork",
					"Deleting not used artwork " + aArtwork + ".",
					Arrays.asList(aArtwork.toString()));

			deleteArtwork(aArtwork.getId());
		}
	}

}
