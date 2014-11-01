package net.dorokhov.pony.core.library;

import net.dorokhov.pony.core.audio.SongDataService;
import net.dorokhov.pony.core.audio.data.SongDataReadable;
import net.dorokhov.pony.core.audio.data.SongDataWritable;
import net.dorokhov.pony.core.dao.AlbumDao;
import net.dorokhov.pony.core.dao.ArtistDao;
import net.dorokhov.pony.core.dao.GenreDao;
import net.dorokhov.pony.core.dao.SongDao;
import net.dorokhov.pony.core.entity.*;
import net.dorokhov.pony.core.image.ThumbnailService;
import net.dorokhov.pony.core.library.file.LibraryFolder;
import net.dorokhov.pony.core.library.file.LibraryImage;
import net.dorokhov.pony.core.library.file.LibrarySong;
import net.dorokhov.pony.core.logging.LogService;
import net.dorokhov.pony.core.storage.StoredFileSaveCommand;
import net.dorokhov.pony.core.storage.StoredFileService;
import net.dorokhov.pony.core.utils.PageProcessor;
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
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.ObjectUtils;

import java.io.File;
import java.util.*;

@Service
public class LibraryServiceImpl implements LibraryService {

	private static final Object lock = new Object();

	private static final int CLEANING_BUFFER_SIZE = 300;

	private final Logger log = LoggerFactory.getLogger(getClass());

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
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public void cleanSongs(final List<LibraryFolder> aLibrary, final ProgressDelegate aDelegate) {
		synchronized (lock) {
			transactionTemplate.execute(new TransactionCallbackWithoutResult() {
				@Override
				public void doInTransactionWithoutResult(TransactionStatus aTransactionStatus) {
					doCleanSongs(aLibrary, aDelegate);
				}
			});
		}
	}

	@Override
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public void cleanArtworks(List<LibraryFolder> aLibrary, final ProgressDelegate aDelegate) {
		synchronized (lock) {
			transactionTemplate.execute(new TransactionCallbackWithoutResult() {
				@Override
				public void doInTransactionWithoutResult(TransactionStatus aTransactionStatus) {
					doCleanStoredFiles(aDelegate);
				}
			});
		}
	}

	@Override
	@Transactional(readOnly = true)
	public Song importSong(List<LibraryFolder> aLibrary, final LibrarySong aSongFile) {

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
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public void importArtworks(List<LibraryFolder> aLibrary, final ProgressDelegate aDelegate) {
		synchronized (lock) {
			transactionTemplate.execute(new TransactionCallbackWithoutResult() {
				@Override
				public void doInTransactionWithoutResult(TransactionStatus aTransactionStatus) {
					doImportArtworks(aDelegate);
				}
			});
		}
	}

	@Override
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public Song writeAndImportSong(final LibrarySong aSongFile, final SongDataWritable aSongData) {

		Song song;

		synchronized (lock) {
			song = transactionTemplate.execute(new TransactionCallback<Song>() {
				@Override
				public Song doInTransaction(TransactionStatus status) {

					Song song = songDao.findByPath(aSongFile.getFile().getAbsolutePath());

					if (song != null) {

						SongDataReadable updatedSongData;

						try {
							updatedSongData = songDataService.write(new File(song.getPath()), aSongData);
						} catch (Exception e) {
							throw new RuntimeException("Could not write data " + aSongData + " to song [" + song.getPath() + "]");
						}

						song = importSong(aSongFile, updatedSongData);
					}

					return song;
				}
			});
		}

		return song;
	}

	private Song importSong(LibrarySong aSongFile, SongDataReadable aSongData) {

		Song song;

		try {

			Genre genre = importGenre(aSongData);

			song = importSong(aSongFile, aSongData, importAlbum(aSongData, importArtist(aSongData)), genre);

		} catch (Exception e) {
			throw new RuntimeException("Could not import song data " + aSongData, e);
		}

		return song;
	}

	private Song importSong(LibrarySong aSongFile, SongDataReadable aSongData, Album aAlbum, Genre aGenre) {

		boolean shouldSave = false;

		Song song = songDao.findByPath(aSongData.getPath());

		Album oldAlbum = null;
		Artist oldArtist = null;
		Genre oldGenre = null;

		if (song == null) {

			song = new Song();
			song.setPath(aSongData.getPath());
			song.setAlbum(aAlbum);
			song.setGenre(aGenre);

			shouldSave = true;

		} else {
			oldAlbum = song.getAlbum();
			oldArtist = song.getAlbum().getArtist();
			oldGenre = song.getGenre();
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
					!ObjectUtils.nullSafeEquals(song.getAlbum(), aSongData.getAlbum()) ||
					!ObjectUtils.nullSafeEquals(song.getYear(), aSongData.getYear()) ||
					!ObjectUtils.nullSafeEquals(song.getArtwork(), artwork)) {

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

			song.setArtwork(artwork);

			boolean newSong = (song.getId() == null);

			song = songDao.save(song);

			deleteEntitiesWithoutSongs(oldAlbum, oldArtist, oldGenre);

			if (newSong) {
				logDebug("libraryService.songCreated", "Song " + song + " has been created.", song.toString());
			} else {
				logDebug("libraryService.songUpdated", "Song " + song + " has been updated.", song.toString());
			}

			if (song.getAlbum().getArtwork() == null) {

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
				logDebug("libraryService.artistCreated", "Genre " + genre + " has been created.", genre.toString());
			} else {
				logDebug("libraryService.artistUpdated", "Genre " + genre + " has been updated.", genre.toString());
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
				logDebug("libraryService.artistCreated", "Artist " + artist + " has been created.", artist.toString());
			} else {
				logDebug("libraryService.artistUpdated", "Artist " + artist + " has been updated.", artist.toString());
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
			album.setArtist(aArtist);

			shouldSave = true;
		}

		if (album.getId() != null) {
			if (!ObjectUtils.nullSafeEquals(album.getName(), albumName)) {
				shouldSave = true;
			}
			if (!ObjectUtils.nullSafeEquals(album.getYear(), aSongData.getYear())) {
				shouldSave = true;
			}
		}

		if (shouldSave) {

			album.setName(albumName);
			album.setYear(aSongData.getYear());

			boolean newAlbum = (album.getId() == null);

			album = albumDao.save(album);

			if (newAlbum) {
				logDebug("libraryService.albumCreated", "Album " + album + " has been created.", album.toString());
			} else {
				logDebug("libraryService.albumUpdated", "Album " + album + " has been updated.", album.toString());
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

				try {

					StoredFileSaveCommand saveCommand = buildEmbeddedArtworkStorageCommand(aSongData);

					artwork = storedFileService.save(saveCommand);

					logDebug("libraryService.embeddedArtworkStored", "Embedded artwork stored " + artwork, artwork.toString());

				} catch (Exception e) {
					logWarn("libraryService.embeddedArtworkNotStored", "Could not store embedded artwork " + aSongData.getArtwork(), e, aSongData.getArtwork().toString());
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
					logWarn("libraryService.fileArtworkNotCreated", "Could not create file artwork", e);
				}

				if (checksum != null) {

					artwork = storedFileService.getByTagAndChecksum(StoredFile.TAG_ARTWORK_FILE, checksum);

					if (artwork == null) {

						try {

							StoredFileSaveCommand saveCommand = buildFileArtworkStorageCommand(aSong, aSongData, artworkImage, mimeType, checksum);

							artwork = storedFileService.save(saveCommand);

							logDebug("libraryService.fileArtworkCreated", "File artwork created " + artwork, artwork.toString());

						} catch (Exception e) {
							logWarn("libraryService.fileArtworkNotCreated", "Could not create file artwork", e);
						}
					}
				}
			}
		}

		return artwork;
	}

	private StoredFileSaveCommand buildEmbeddedArtworkStorageCommand(SongDataReadable aSongData) throws Exception {

		File file = new File(FileUtils.getTempDirectory(), "pony." + StoredFile.TAG_ARTWORK_EMBEDDED + "." + UUID.randomUUID() + ".tmp");

		thumbnailService.makeThumbnail(aSongData.getArtwork().getBinaryData(), file);

		StoredFileSaveCommand saveCommand = new StoredFileSaveCommand(StoredFileSaveCommand.Type.MOVE, file);

		saveCommand.setName(aSongData.getArtist() + " " + aSongData.getAlbum() + " " + aSongData.getTitle());
		saveCommand.setMimeType(aSongData.getArtwork().getMimeType());
		saveCommand.setChecksum(aSongData.getArtwork().getChecksum());
		saveCommand.setTag(StoredFile.TAG_ARTWORK_EMBEDDED);

		return saveCommand;
	}

	private StoredFileSaveCommand buildFileArtworkStorageCommand(Song aSong, SongDataReadable aSongData, LibraryImage aArtwork, String aMimeType, String aChecksum) throws Exception {

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

		StoredFileSaveCommand saveCommand = new StoredFileSaveCommand(StoredFileSaveCommand.Type.MOVE, file);

		saveCommand.setName(artist + " " + album + " " + title);
		saveCommand.setMimeType(aMimeType);
		saveCommand.setChecksum(aChecksum);
		saveCommand.setTag(StoredFile.TAG_ARTWORK_FILE);
		saveCommand.setUserData(aArtwork.getFile().getAbsolutePath());

		return saveCommand;
	}

	private void doCleanSongs(List<LibraryFolder> aLibrary, final ProgressDelegate aDelegate) {

		final Set<String> librarySongPaths = new HashSet<>();

		for (LibraryFolder libraryFolder : aLibrary) {
			for (LibrarySong songFile : libraryFolder.getChildSongs(true)) {
				librarySongPaths.add(songFile.getFile().getAbsolutePath());
			}
		}

		final List<Long> itemsToDelete = new ArrayList<>();

		PageProcessor.Handler<Song> handler = new PageProcessor.Handler<Song>() {

			@Override
			public void process(Song aSong, Page<Song> aPage, int aIndexInPage, long aIndexInAll) {

				File file = new File(aSong.getPath());

				if (!librarySongPaths.contains(file.getAbsolutePath()) || !file.exists()) {

					itemsToDelete.add(aSong.getId());

					logDebug("libraryService.deletingSong",
							"Song file not found [" + file.getAbsolutePath() + "], deleting song [" + aSong + "].",
							file.getAbsolutePath(), aSong.toString());
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

	public void doCleanStoredFiles(final ProgressDelegate aDelegate) {

		final List<Long> itemsToDelete = new ArrayList<>();

		PageProcessor.Handler<StoredFile> handler = new PageProcessor.Handler<StoredFile>() {

			@Override
			public void process(StoredFile aStoredFile, Page<StoredFile> aPage, int aIndexInPage, long aIndexInAll) {

				boolean shouldDelete = false;

				if (aStoredFile.getTag() != null && aStoredFile.getTag().equals(StoredFile.TAG_ARTWORK_FILE)) {

					File externalFile = null;

					if (aStoredFile.getUserData() != null) {
						externalFile = new File(aStoredFile.getUserData());
					}

					if (externalFile == null || !externalFile.exists()) {

						String filePath = (externalFile != null ? externalFile.getAbsolutePath() : null);

						logDebug("libraryService.deletingNotFoundStoredFile",
								"Artwork file not found [" + filePath + "], deleting stored file [" + aStoredFile + "]",
								filePath, aStoredFile.toString());

						shouldDelete = true;

					} else {
						if (aStoredFile.getUpdateDate() != null) {
							shouldDelete = (aStoredFile.getUpdateDate().getTime() < externalFile.lastModified());
						} else {
							shouldDelete = (aStoredFile.getCreationDate().getTime() < externalFile.lastModified());
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

			clearSongArtwork(id);
			clearAlbumArtwork(id);
			clearArtistArtwork(id);
			clearGenreArtwork(id);

			StoredFile storedFile = storedFileService.getById(id);

			storedFileService.deleteById(id);

			logDebug("libraryService.deletedSong", "Stored file " + storedFile + " has been deleted.", storedFile.toString());
		}
	}

	private void doImportArtworks(final ProgressDelegate aDelegate) {

		final long genreCount = genreDao.count();
		final long artistCount = artistDao.count();

		final long entityCount = genreCount + artistCount;

		PageProcessor.Handler<Genre> genreHandler = new PageProcessor.Handler<Genre>() {

			@Override
			public void process(Genre aGenre, Page<Genre> aPage, int aIndexInPage, long aIndexInAll) {

				long albumCount = albumDao.countByGenreIdAndArtworkNotNull(aGenre.getId());

				if (albumCount > 0) {

					Page<Album> albumPage = albumDao.findByGenreIdAndArtworkNotNull(aGenre.getId(),
							new PageRequest((int) Math.floor(albumCount / 2.0), 1, Sort.Direction.ASC, "year"));

					if (albumPage.getNumberOfElements() > 0) {

						aGenre.setArtwork(albumPage.getContent().get(0).getArtwork());

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

				long albumCount = albumDao.countByArtistIdAndArtworkNotNull(aArtist.getId());

				if (albumCount > 0) {

					Page<Album> albumPage = albumDao.findByArtistIdAndArtworkNotNull(aArtist.getId(),
							new PageRequest((int) Math.floor(albumCount / 2.0), 1, Sort.Direction.ASC, "year"));

					if (albumPage.getNumberOfElements() > 0) {

						aArtist.setArtwork(albumPage.getContent().get(0).getArtwork());

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

	private void deleteSong(Long aId) {

		Song song = songDao.findOne(aId);

		songDao.delete(song);

		logDebug("libraryService.deletedSong", "Song " + song + " has been deleted.", song.toString());

		deleteEntitiesWithoutSongs(song.getAlbum(), song.getAlbum().getArtist(), song.getGenre());
	}

	private void deleteEntitiesWithoutSongs(Album aAlbum, Artist aArtist, Genre aGenre) {

		if (aAlbum != null && songDao.countByAlbumId(aAlbum.getId()) == 0) {

			albumDao.delete(aAlbum);

			logDebug("libraryService.deletedAlbum", "Album " + aAlbum + " has no songs and has been deleted.", aAlbum.toString());
		}

		if (aArtist != null && songDao.countByAlbumArtistId(aArtist.getId()) == 0) {

			artistDao.delete(aArtist);

			logDebug("libraryService.deletedArtist", "Artist " + aArtist + " has no songs and has been deleted.", aArtist.toString());
		}

		if (aGenre != null && songDao.countByGenreId(aGenre.getId()) == 0) {

			genreDao.delete(aGenre);

			logDebug("libraryService.deletedGenre", "Genre " + aGenre + " has no songs and has been deleted.", aGenre.toString());
		}
	}

	private void clearGenreArtwork(final Long aStoredFileId) {
		PageProcessor.Handler<Genre> handler = new PageProcessor.Handler<Genre>() {

			@Override
			public void process(Genre aGenre, Page<Genre> aPage, int aIndexInPage, long aIndexInAll) {

				aGenre.setArtwork(null);

				genreDao.save(aGenre);
			}

			@Override
			public Page<Genre> getPage(Pageable aPageable) {
				return genreDao.findByArtworkId(aStoredFileId, aPageable);
			}
		};
		new PageProcessor<>(CLEANING_BUFFER_SIZE, new Sort("id"), handler).run();
	}

	private void clearArtistArtwork(final Long aStoredFileId) {
		PageProcessor.Handler<Artist> handler = new PageProcessor.Handler<Artist>() {

			@Override
			public void process(Artist aArtist, Page<Artist> aPage, int aIndexInPage, long aIndexInAll) {

				aArtist.setArtwork(null);

				artistDao.save(aArtist);
			}

			@Override
			public Page<Artist> getPage(Pageable aPageable) {
				return artistDao.findByArtworkId(aStoredFileId, aPageable);
			}
		};
		new PageProcessor<>(CLEANING_BUFFER_SIZE, new Sort("id"), handler).run();
	}

	private void clearAlbumArtwork(final Long aStoredFileId) {
		PageProcessor.Handler<Album> handler = new PageProcessor.Handler<Album>() {

			@Override
			public void process(Album aAlbum, Page<Album> aPage, int aIndexInPage, long aIndexInAll) {

				aAlbum.setArtwork(null);

				albumDao.save(aAlbum);
			}

			@Override
			public Page<Album> getPage(Pageable aPageable) {
				return albumDao.findByArtworkId(aStoredFileId, aPageable);
			}
		};
		new PageProcessor<>(CLEANING_BUFFER_SIZE, new Sort("id"), handler).run();
	}

	private void clearSongArtwork(final Long aStoredFileId) {
		PageProcessor.Handler<Song> handler = new PageProcessor.Handler<Song>() {

			@Override
			public void process(Song aSong, Page<Song> aPage, int aIndexInPage, long aIndexInAll) {

				aSong.setArtwork(null);

				songDao.save(aSong);
			}

			@Override
			public Page<Song> getPage(Pageable aPageable) {
				return songDao.findByArtworkId(aStoredFileId, aPageable);
			}
		};
		new PageProcessor<>(CLEANING_BUFFER_SIZE, new Sort("id"), handler).run();
	}

	private void logDebug(String aCode, String aMessage, String... aArguments) {
		log.debug(aMessage);
		logService.debug(aCode, aMessage, Arrays.asList(aArguments));
	}

	private void logWarn(String aCode, String aMessage, Throwable aThrowable, String... aArguments) {
		log.warn(aMessage, aThrowable);
		logService.warn(aCode, aMessage, aThrowable, Arrays.asList(aArguments));
	}
}
