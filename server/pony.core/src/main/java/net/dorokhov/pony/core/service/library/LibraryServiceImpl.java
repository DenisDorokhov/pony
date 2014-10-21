package net.dorokhov.pony.core.service.library;

import net.dorokhov.pony.core.common.PageProcessor;
import net.dorokhov.pony.core.dao.AlbumDao;
import net.dorokhov.pony.core.dao.ArtistDao;
import net.dorokhov.pony.core.dao.SongDao;
import net.dorokhov.pony.core.domain.Album;
import net.dorokhov.pony.core.domain.Artist;
import net.dorokhov.pony.core.domain.Song;
import net.dorokhov.pony.core.domain.StoredFile;
import net.dorokhov.pony.core.service.LogService;
import net.dorokhov.pony.core.service.audio.SongDataWritable;
import net.dorokhov.pony.core.service.file.StoredFileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.util.*;

@Service
public class LibraryServiceImpl implements LibraryService {

	private static final int CLEANING_BUFFER_SIZE = 300;

	private static final String FILE_TAG_ARTWORK_EMBEDDED = "artworkEmbedded";
	private static final String FILE_TAG_ARTWORK_EXTERNAL = "artworkExternal";

	private final Logger log = LoggerFactory.getLogger(getClass());

	private LogService logService;

	private SongDao songDao;

	private AlbumDao albumDao;

	private ArtistDao artistDao;

	private StoredFileService storedFileService;

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
	public void setStoredFileService(StoredFileService aStoredFileService) {
		storedFileService = aStoredFileService;
	}

	@Override
	@Transactional
	public void cleanSongs(LibraryFolder aLibrary, final ProgressDelegate aDelegate) {

		final Set<String> librarySongPaths = new HashSet<>();

		for (LibrarySong songFile : aLibrary.getChildSongs(true)) {
			librarySongPaths.add(songFile.getFile().getAbsolutePath());
		}

		final List<Long> itemsToDelete = new ArrayList<>();

		PageProcessor.Handler<Song> handler = new PageProcessor.Handler<Song>() {

			@Override
			public void process(Song aSong, Page<Song> aPage, int aIndexInPage, long aIndexInAll) {

				File file = new File(aSong.getPath());

				if (!librarySongPaths.contains(file.getAbsolutePath()) || !file.exists()) {

					itemsToDelete.add(aSong.getId());

					String message = "Song file not found [" + file.getAbsolutePath() + "], deleting song [" + aSong + "].";

					log.debug(message);
					logService.debug("libraryService.deletingSong", message, Arrays.asList(file.getAbsolutePath(), aSong.toString()));
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

		if (itemsToDelete.size() > 0) {

			String message = "Deleted [" + itemsToDelete.size() + "] songs.";

			log.info(message);
			logService.info("libraryService.deletedSongs", message, Arrays.asList(String.valueOf(itemsToDelete.size())));
		}
	}

	@Override
	@Transactional
	public void cleanStoredFiles(final ProgressDelegate aDelegate) {

		final List<Long> itemsToDelete = new ArrayList<>();

		PageProcessor.Handler<StoredFile> storedFileHandler = new PageProcessor.Handler<StoredFile>() {

			@Override
			public void process(StoredFile aStoredFile, Page<StoredFile> aPage, int aIndexInPage, long aIndexInAll) {

				File externalFile = null;

				if (aStoredFile.getUserData() != null) {
					externalFile = new File(aStoredFile.getUserData());
				}

				boolean shouldDelete = (externalFile == null || !externalFile.exists());

				if (shouldDelete) {

					String filePath = (externalFile != null ? externalFile.getAbsolutePath() : null);
					String message = "Artwork file not found [" + filePath + "], deleting stored file [" + aStoredFile + "]";

					log.debug(message);
					logService.debug("libraryService.deletingNotFoundStoredFile", message, Arrays.asList(filePath, aStoredFile.toString()));

				} else if (songDao.countByArtworkId(aStoredFile.getId()) == 0 &&
						albumDao.countByArtworkId(aStoredFile.getId()) == 0 &&
						artistDao.countByArtworkId(aStoredFile.getId()) == 0) {

					shouldDelete = true;

					String message = "Artwork file is not used, deleting stored file [" + aStoredFile + "]";

					log.debug(message);
					logService.debug("libraryService.deletingNotUsedStoredFile", message, Arrays.asList(aStoredFile.toString()));
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
				return storedFileService.getByTag(FILE_TAG_ARTWORK_EXTERNAL, aPageable);
			}
		};
		new PageProcessor<>(CLEANING_BUFFER_SIZE, new Sort("id"), storedFileHandler).run();

		for (final Long id : itemsToDelete) {

			clearSongArtwork(id);
			clearAlbumArtwork(id);
			clearArtistArtwork(id);

			storedFileService.deleteById(id);
		}

		if (itemsToDelete.size() > 0) {

			String message = "Deleted [" + itemsToDelete.size() + "] stored files.";

			log.info(message);
			logService.info("libraryService.deletedStoredFiles", message, Arrays.asList(String.valueOf(itemsToDelete.size())));
		}
	}

	@Override
	public ImportResult importSong(LibrarySong aSongFile) {
		return null;
	}

	@Override
	public ImportResult writeAndImportSong(Long aId, SongDataWritable aSongData) {
		return null;
	}

	@Override
	public void importArtworks(ProgressDelegate aDelegate) {

	}

	private void deleteSong(Long aId) {
		// TODO: implement
	}

	private void clearArtistArtwork(final Long aStoredFileId) {
		PageProcessor.Handler<Artist> artistHandler = new PageProcessor.Handler<Artist>() {

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
		new PageProcessor<>(CLEANING_BUFFER_SIZE, new Sort("id"), artistHandler).run();
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
}
