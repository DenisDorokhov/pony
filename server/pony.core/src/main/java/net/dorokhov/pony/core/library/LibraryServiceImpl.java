package net.dorokhov.pony.core.library;

import net.dorokhov.pony.core.library.file.LibraryFolder;
import net.dorokhov.pony.core.library.file.LibrarySong;
import net.dorokhov.pony.core.utils.PageProcessor;
import net.dorokhov.pony.core.dao.AlbumDao;
import net.dorokhov.pony.core.dao.ArtistDao;
import net.dorokhov.pony.core.dao.GenreDao;
import net.dorokhov.pony.core.dao.SongDao;
import net.dorokhov.pony.core.entity.*;
import net.dorokhov.pony.core.logging.LogService;
import net.dorokhov.pony.core.audio.data.SongDataWritable;
import net.dorokhov.pony.core.storage.StoredFileService;
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
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import java.io.File;
import java.util.*;

@Service
public class LibraryServiceImpl implements LibraryService {

	private static final Object lock = new Object();

	private static final int CLEANING_BUFFER_SIZE = 300;

	private static final String FILE_TAG_ARTWORK_EMBEDDED = "artworkEmbedded";
	private static final String FILE_TAG_ARTWORK_EXTERNAL = "artworkExternal";

	private final Logger log = LoggerFactory.getLogger(getClass());

	private TransactionTemplate transactionTemplate;

	private LogService logService;

	private SongDao songDao;

	private AlbumDao albumDao;

	private ArtistDao artistDao;

	private GenreDao genreDao;

	private StoredFileService storedFileService;

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
					doCleanArtworks(aDelegate);
				}
			});
		}
	}

	@Override
	public Song importSong(List<LibraryFolder> aLibrary, final LibrarySong aSongFile) {
		// TODO: implement
		return null;
	}

	@Override
	public Song writeAndImportSong(LibraryFolder aLibrary, Long aId, SongDataWritable aSongData) {
		// TODO: implement
		return null;
	}

	@Override
	public void importArtworks(List<LibraryFolder> aLibrary, ProgressDelegate aDelegate) {
		// TODO: implement
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

	public void doCleanArtworks(final ProgressDelegate aDelegate) {

		final List<Long> itemsToDelete = new ArrayList<>();

		PageProcessor.Handler<StoredFile> storedFileHandler = new PageProcessor.Handler<StoredFile>() {

			@Override
			public void process(StoredFile aStoredFile, Page<StoredFile> aPage, int aIndexInPage, long aIndexInAll) {

				File externalFile = null;

				if (aStoredFile.getUserData() != null) {
					externalFile = new File(aStoredFile.getUserData());
				}

				if (externalFile == null || !externalFile.exists()) {

					String filePath = (externalFile != null ? externalFile.getAbsolutePath() : null);

					logDebug("libraryService.deletingNotFoundStoredFile",
							"Artwork file not found [" + filePath + "], deleting stored file [" + aStoredFile + "]",
							filePath, aStoredFile.toString());

					itemsToDelete.add(aStoredFile.getId());
				}

				if (aDelegate != null) {
					aDelegate.onProgress(aIndexInAll / (double) aPage.getTotalElements());
				}
			}

			@Override
			public Page<StoredFile> getPage(Pageable aPageable) {
				return storedFileService.getByTag(FILE_TAG_ARTWORK_EXTERNAL, aPageable);
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

		StoredFile songArtwork = song.getArtwork();

		Album album = song.getAlbum();
		StoredFile albumArtwork = album.getArtwork();

		Artist artist = album.getArtist();
		StoredFile artistArtwork = artist.getArtwork();

		Genre genre = song.getGenre();
		StoredFile genreArtwork = genre.getArtwork();

		songDao.delete(song);

		logDebug("libraryService.deletedSong", "Song [" + song + "] has been deleted.", song.toString());

		deleteStoredFileReference(songArtwork);

		album.setSongCount(album.getSongCount() - 1);
		album.setSongSize(album.getSongSize() - song.getSize());

		if (album.getSongCount() <= 0) {

			albumDao.delete(album);

			logDebug("libraryService.deletedAlbum", "Album [" + album + "] has no songs and has been deleted.", album.toString());

			deleteStoredFileReference(albumArtwork);

			artist.setAlbumCount(artist.getAlbumCount() - 1);
		}

		artist.setSongCount(artist.getSongCount() - 1);
		artist.setSongSize(artist.getSongSize() - song.getSize());

		if (artist.getAlbumCount() <= 0 || artist.getSongCount() <= 0) {

			artistDao.delete(artist);

			logDebug("libraryService.deletedAlbum", "Artist [" + artist + "] has no songs and has been deleted.", artist.toString());

			deleteStoredFileReference(artistArtwork);
		}

		genre.setSongCount(genre.getSongCount() - 1);
		genre.setSongSize(genre.getSongSize() - song.getSize());

		if (genre.getSongCount() <= 0) {

			genreDao.delete(genre);

			deleteStoredFileReference(genreArtwork);
		}
	}

	private void deleteStoredFileReference(StoredFile aStoredFile) {

		aStoredFile.setReferenceCount(aStoredFile.getReferenceCount() - 1);

		if (aStoredFile.getReferenceCount() <= 0) {

			storedFileService.deleteById(aStoredFile.getId());

			logDebug("libraryService.deletedStoredFile", "Stored file [" + aStoredFile + "] has been deleted.", aStoredFile.toString());
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
}
