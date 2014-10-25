package net.dorokhov.pony.core.library;

import net.dorokhov.pony.core.audio.SongDataService;
import net.dorokhov.pony.core.audio.data.SongDataReadable;
import net.dorokhov.pony.core.image.ThumbnailService;
import net.dorokhov.pony.core.library.file.LibraryFolder;
import net.dorokhov.pony.core.library.file.LibraryImage;
import net.dorokhov.pony.core.library.file.LibrarySong;
import net.dorokhov.pony.core.storage.StoredFileSaveCommand;
import net.dorokhov.pony.core.utils.PageProcessor;
import net.dorokhov.pony.core.dao.AlbumDao;
import net.dorokhov.pony.core.dao.ArtistDao;
import net.dorokhov.pony.core.dao.GenreDao;
import net.dorokhov.pony.core.dao.SongDao;
import net.dorokhov.pony.core.entity.*;
import net.dorokhov.pony.core.logging.LogService;
import net.dorokhov.pony.core.audio.data.SongDataWritable;
import net.dorokhov.pony.core.storage.StoredFileService;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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

	private static final String FILE_TAG_ARTWORK_EMBEDDED = "artworkEmbedded";
	private static final String FILE_TAG_ARTWORK_FILE = "artworkFile";

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
	public void cleanStoredFiles(List<LibraryFolder> aLibrary, final ProgressDelegate aDelegate) {
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

		if (song == null || song.getUpdateDate().getTime() < aSongFile.getFile().lastModified()) {

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

						Song song;

						try {

							Genre genre = importGenre(songData);

							song = importSong(aSongFile, songData, importAlbum(songData, importArtist(songData)), genre);

						} catch (Exception e) {
							throw new RuntimeException("Could not import song data " + songData, e);
						}

						return song;
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
								song = discoverSongArtwork(aSongFile, song);
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
	public Song writeAndImportSong(LibraryFolder aLibrary, final Long aId, final SongDataWritable aSongData) {

		Song song;

		synchronized (lock) {
			song = transactionTemplate.execute(new TransactionCallback<Song>() {
				@Override
				public Song doInTransaction(TransactionStatus status) {

					Song song = songDao.findById(aId);

					if (song != null) {

						try {
							songDataService.write(new File(song.getPath()), aSongData);
						} catch (Exception e) {
							throw new RuntimeException("Could not write data " + aSongData + " to song [" + song.getPath() + "]");
						}

						// TODO: update song with written data
					}

					return song;
				}
			});
		}

		return song;
	}

	@Override
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public void importArtworks(List<LibraryFolder> aLibrary, ProgressDelegate aDelegate) {
		// TODO: implement
	}

	private Genre importGenre(SongDataReadable aSongData) {

		String genreName = StringUtils.defaultIfEmpty(StringUtils.normalizeSpace(aSongData.getGenre()), null);

		Genre genre = genreDao.findByName(genreName);

		boolean shouldSave = false;

		if (genre == null) {
			genre = new Genre();
		}

		if (genre.getId() != null) {
			if (!ObjectUtils.nullSafeEquals(genre.getName(), genreName)) {
				shouldSave = true;
			}
		}

		if (shouldSave) {

			genre.setName(genreName);

			genre = genreDao.save(genre);
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
		}

		if (artist.getId() != null) {
			if (!ObjectUtils.nullSafeEquals(artist.getName(), artistName)) {
				shouldSave = true;
			}
		}

		if (shouldSave) {

			artist.setName(artistName);

			artist = artistDao.save(artist);
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

		boolean newAlbum = (album.getId() == null);

		if (!newAlbum) {
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

			album = albumDao.save(album);
		}

		return album;
	}

	private Song importSong(LibrarySong aSongFile, SongDataReadable aSongData, Album aAlbum, Genre aGenre) {

		boolean shouldSave = false;

		StoredFile artwork = null;

		if (aSongData.getArtwork() != null && aSongData.getArtwork().getChecksum() != null) {

			artwork = storedFileService.getByTagAndChecksum(FILE_TAG_ARTWORK_EMBEDDED, aSongData.getArtwork().getChecksum());

			if (artwork == null) {

				try {

					StoredFileSaveCommand saveCommand = songDataToArtworkStorageCommand(aSongData);

					artwork = storedFileService.save(saveCommand);

					logDebug("libraryService.embeddedArtworkStored", "Embedded artwork stored " + artwork, artwork.toString());

				} catch (Exception e) {
					logWarn("libraryService.embeddedArtworkNotStored", "Could not store embedded artwork " + aSongData.getArtwork(), e, aSongData.getArtwork().toString());
				}

				shouldSave = true;
			}
		}

		Song song = songDao.findByPath(aSongData.getPath());

		if (song == null) {

			song = new Song();
			song.setPath(aSongData.getPath());
			song.setAlbum(aAlbum);
			song.setGenre(aGenre);

			shouldSave = true;
		}

		boolean newSong = (song.getId() == null);

		if (!newSong) {
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

			song = songDao.save(song);

			updateCounters(song);
		}

		if (song.getArtwork() == null) {
			song = discoverSongArtwork(aSongFile, song);
		}

		return song;
	}

	private void updateCounters(Song aSong) {

		Genre genre = aSong.getGenre();
		Album album = aSong.getAlbum();
		Artist artist = album.getArtist();

		genre.setSongCount(Long.valueOf(songDao.countByGenreId(genre.getId())).intValue());

		genreDao.save(genre);

		artist.setSongCount(Long.valueOf(songDao.countByAlbumArtistId(artist.getId())).intValue());
		artist.setSongSize(songDao.sumSizeByArtistId(artist.getId()));
		artist.setAlbumCount(Long.valueOf(albumDao.countByArtistId(artist.getId())).intValue());

		artistDao.save(artist);

		album.setSongCount(Long.valueOf(songDao.countByAlbumId(album.getId())).intValue());
		album.setSongSize(songDao.sumSizeByAlbumId(album.getId()));

		albumDao.save(album);
	}

	private Song discoverSongArtwork(LibrarySong aSongFile, Song aSong) {

		LibraryImage artwork = artworkDiscoveryService.discoverArtwork(aSongFile);

		if (artwork != null) {

			String mimeType = artwork.getMimeType();

			if (mimeType != null) {

				String checksum = null;

				try {
					checksum = artwork.getChecksum();
				} catch (Exception e) {
					logWarn("libraryService.fileArtworkNotStored", "Could not store file artwork", e);
				}

				if (checksum != null) {

					StoredFile storedFile = storedFileService.getByTagAndChecksum(FILE_TAG_ARTWORK_FILE, checksum);

					if (storedFile == null) {

						File file = new File(FileUtils.getTempDirectory(), "pony." + FILE_TAG_ARTWORK_FILE + "." + UUID.randomUUID() + ".tmp");

						boolean thumbnailReady = false;

						try {

							thumbnailService.makeThumbnail(artwork.getFile(), file);

							thumbnailReady = true;

						} catch (Exception e) {
							logWarn("libraryService.fileArtworkNotStored", "Could not store file artwork", e);
						}

						if (thumbnailReady) {

							StoredFileSaveCommand saveCommand = new StoredFileSaveCommand(StoredFileSaveCommand.Type.MOVE, file);

							saveCommand.setName(aSong.getArtistName() + " " + aSong.getAlbumName() + " " + aSong.getName());
							saveCommand.setMimeType(mimeType);
							saveCommand.setChecksum(checksum);
							saveCommand.setTag(FILE_TAG_ARTWORK_FILE);
							saveCommand.setUserData(artwork.getFile().getAbsolutePath());

							storedFile = storedFileService.save(saveCommand);

							logDebug("libraryService.fileArtworkStored", "File artwork stored " + storedFile, storedFile.toString());

							aSong.getAlbum().setArtwork(storedFile);

							albumDao.save(aSong.getAlbum());

							aSong.setArtwork(storedFile);

							songDao.save(aSong);
						}
					}
				}
			}
		}

		return aSong;
	}

	private StoredFileSaveCommand songDataToArtworkStorageCommand(SongDataReadable aSongData) throws Exception {

		File file = new File(FileUtils.getTempDirectory(), "pony." + FILE_TAG_ARTWORK_EMBEDDED + "." + UUID.randomUUID() + ".tmp");

		thumbnailService.makeThumbnail(aSongData.getArtwork().getBinaryData(), file);

		StoredFileSaveCommand saveCommand = new StoredFileSaveCommand(StoredFileSaveCommand.Type.MOVE, file);

		saveCommand.setName(aSongData.getArtist() + " " + aSongData.getAlbum() + " " + aSongData.getTitle());
		saveCommand.setMimeType(aSongData.getArtwork().getMimeType());
		saveCommand.setChecksum(aSongData.getArtwork().getChecksum());
		saveCommand.setTag(FILE_TAG_ARTWORK_EMBEDDED);

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
					aDelegate.onProgress(aIndexInAll / (double) aPage.getTotalElements());
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

		PageProcessor.Handler<StoredFile> storedFileHandler = new PageProcessor.Handler<StoredFile>() {

			@Override
			public void process(StoredFile aStoredFile, Page<StoredFile> aPage, int aIndexInPage, long aIndexInAll) {

				boolean shouldDelete = false;

				if (aStoredFile.getTag() != null && aStoredFile.getTag().equals(FILE_TAG_ARTWORK_FILE)) {

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
					}
				}

				if (!shouldDelete) {
					shouldDelete = (genreDao.countByArtworkId(aStoredFile.getId()) == 0 &&
							artistDao.countByArtworkId(aStoredFile.getId()) == 0 &&
							albumDao.countByArtworkId(aStoredFile.getId()) == 0 &&
							songDao.countByArtworkId(aStoredFile.getId()) == 0);
				}

				if (shouldDelete) {
					itemsToDelete.add(aStoredFile.getId());
				}

				if (aDelegate != null) {
					aDelegate.onProgress(aIndexInAll / (double) aPage.getTotalElements());
				}
			}

			@Override
			public Page<StoredFile> getPage(Pageable aPageable) {
				return storedFileService.getAll(aPageable);
			}
		};
		new PageProcessor<>(CLEANING_BUFFER_SIZE, new Sort("id"), storedFileHandler).run();

		for (final Long id : itemsToDelete) {

			clearSongArtwork(id);
			clearAlbumArtwork(id);
			clearArtistArtwork(id);
			clearGenreArtwork(id);

			storedFileService.deleteById(id);
		}
	}

	private void deleteSong(Long aId) {

		Song song = songDao.findById(aId);

		Album album = song.getAlbum();
		Artist artist = album.getArtist();
		Genre genre = song.getGenre();

		songDao.delete(song);

		logDebug("libraryService.deletedSong", "Song " + song + " has been deleted.", song.toString());

		album.setSongCount(album.getSongCount() - 1);
		album.setSongSize(album.getSongSize() - song.getSize());

		if (album.getSongCount() <= 0) {

			albumDao.delete(album);

			logDebug("libraryService.deletedAlbum", "Album " + album + " has no songs and has been deleted.", album.toString());

			artist.setAlbumCount(artist.getAlbumCount() - 1);
		}

		artist.setSongCount(artist.getSongCount() - 1);
		artist.setSongSize(artist.getSongSize() - song.getSize());

		if (artist.getAlbumCount() <= 0 || artist.getSongCount() <= 0) {

			artistDao.delete(artist);

			logDebug("libraryService.deletedAlbum", "Artist " + artist + " has no songs and has been deleted.", artist.toString());
		}

		genre.setSongCount(genre.getSongCount() - 1);

		if (genre.getSongCount() <= 0) {

			genreDao.delete(genre);

			logDebug("libraryService.deletedGenre", "Genre " + genre + " has no songs and has been deleted.", genre.toString());
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
