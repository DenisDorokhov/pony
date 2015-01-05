package net.dorokhov.pony.core.library;

import net.dorokhov.pony.core.common.PonyUtils;
import net.dorokhov.pony.core.dao.SongDao;
import net.dorokhov.pony.core.domain.Album;
import net.dorokhov.pony.core.domain.Artist;
import net.dorokhov.pony.core.domain.Song;
import net.dorokhov.pony.core.library.exception.AlbumNotFoundException;
import net.dorokhov.pony.core.library.exception.ArtistNotFoundException;
import net.dorokhov.pony.core.library.exception.SongNotFoundException;
import net.dorokhov.pony.core.library.export.LibraryBatchExportTask;
import net.dorokhov.pony.core.library.export.LibrarySingleExportTask;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.util.*;

@Service
public class LibraryExportTaskServiceImpl implements LibraryExportTaskService {

	private static final String UNKNOWN_ARTIST_NAME = "Unknown Artist";
	private static final String UNKNOWN_ALBUM_NAME = "Unknown Artist";

	private SongDao songDao;

	@Autowired
	public void setSongDao(SongDao aSongDao) {
		songDao = aSongDao;
	}

	@Override
	@Transactional(readOnly = true)
	public LibrarySingleExportTask getSongExportTask(Long aSongId) throws SongNotFoundException {

		Song song = songDao.findOne(aSongId);

		if (song == null) {
			throw new SongNotFoundException(aSongId);
		}

		String baseName = "";

		if (song.getName() != null) {
			baseName += buildArtistFileName(song.getAlbum().getArtist()) + " - " + buildAlbumFileName(song.getAlbum()) + " - " + buildSongFileName(song);
		} else {
			baseName = FilenameUtils.getBaseName(song.getPath());
		}

		return new LibrarySingleTaskImpl(PonyUtils.sanitizeFileName(baseName), new File(song.getPath()));
	}

	@Override
	@Transactional(readOnly = true)
	public LibraryBatchExportTask getArtistExportTask(Long aArtistId) throws ArtistNotFoundException {

		List<Song> songList = songDao.findByAlbumArtistId(aArtistId, new Sort("album.year", "album.name", "discNumber", "trackNumber", "name"));

		if (songList.size() == 0) {
			throw new ArtistNotFoundException(aArtistId);
		}

		List<BatchTaskItemImpl> items = getSongListExportItems(songList);

		Artist artist = songList.get(0).getAlbum().getArtist();

		return new LibraryBatchTaskImpl(buildArtistFileName(artist), items);
	}

	@Override
	@Transactional(readOnly = true)
	public LibraryBatchExportTask getAlbumExportTask(Long aAlbumId) throws AlbumNotFoundException {

		List<Song> songList = songDao.findByAlbumId(aAlbumId, new Sort("discNumber", "trackNumber", "name"));

		if (songList.size() == 0) {
			throw new AlbumNotFoundException(aAlbumId);
		}

		List<BatchTaskItemImpl> items = getSongListExportItems(songList);

		Album album = songList.get(0).getAlbum();

		return new LibraryBatchTaskImpl(buildArtistFileName(album.getArtist()) + " - " + buildAlbumFileName(album), items);
	}

	private List<BatchTaskItemImpl> getSongListExportItems(List<Song> aSongList) {

		Collections.sort(aSongList);

		Map<Long, Integer> albumToDiscCount = new HashMap<>();

		for (Song song : aSongList) {

			Integer discCount = albumToDiscCount.get(song.getAlbum().getId());

			if (song.getDiscNumber() != null && song.getDiscNumber() > 1) {
				discCount = song.getDiscNumber();
			}

			if (discCount == null) {
				discCount = 1;
			}

			albumToDiscCount.put(song.getAlbum().getId(), discCount);
		}

		Set<BatchTaskItemImpl> items = new HashSet<>();

		Album currentAlbum = null;
		Artist currentArtist = null;

		String currentAlbumFileName = null;
		String currentArtistFileName = null;

		for (Song song : aSongList) {

			if (currentArtist == null || !currentArtist.equals(song.getAlbum().getArtist())) {
				currentArtist = song.getAlbum().getArtist();
				currentArtistFileName = buildArtistFileName(currentArtist);
			}

			if (currentAlbum == null || !currentAlbum.equals(song.getAlbum())) {
				currentAlbum = song.getAlbum();
				currentAlbumFileName = buildAlbumFileName(currentAlbum);
			}

			String exportPath = FilenameUtils.concat(currentArtistFileName, currentAlbumFileName);

			int discCount = albumToDiscCount.get(song.getAlbum().getId());

			if (discCount > 1) {

				int discNumber = song.getDiscNumber() != null ? song.getDiscNumber() : 1;

				exportPath = FilenameUtils.concat(exportPath, "CD" + discNumber);
			}

			exportPath = FilenameUtils.concat(exportPath, buildSongFileName(song));

			items.add(buildUniqueTaskItem(new File(song.getPath()), exportPath, items));
		}

		return new ArrayList<>(items);
	}

	private String buildArtistFileName(Artist aArtist) {
		return PonyUtils.sanitizeFileName(aArtist.getName() != null ? aArtist.getName() : UNKNOWN_ARTIST_NAME);
	}

	private String buildAlbumFileName(Album aAlbum) {

		String fileName = aAlbum.getYear() != null ? aAlbum.getYear() + " - " : "";

		fileName += aAlbum.getName() != null ? aAlbum.getName() : UNKNOWN_ALBUM_NAME;

		return PonyUtils.sanitizeFileName(fileName);
	}

	private String buildSongFileName(Song aSong) {

		String fileName = "";

		if (aSong.getName() != null) {

			if (aSong.getTrackNumber() != null) {
				fileName += buildTrackNumber(aSong.getTrackNumber()) + " - ";
			}

			fileName += aSong.getName();

		} else {
			fileName = FilenameUtils.getName(aSong.getPath());
		}

		return PonyUtils.sanitizeFileName(fileName);
	}

	private String buildTrackNumber(Integer aTrackNumber) {
		return aTrackNumber <= 9 ? "0" + aTrackNumber : String.valueOf(aTrackNumber);
	}

	private BatchTaskItemImpl buildUniqueTaskItem(File aTarget, String aExportPath, Collection<BatchTaskItemImpl> aItems) {

		Set<String> existingExportPaths = new HashSet<>();
		for (BatchTaskItemImpl item : aItems) {
			existingExportPaths.add(item.getExportPath());
		}

		String currentExportPath = aExportPath;

		int attempt = 1;

		while (existingExportPaths.contains(currentExportPath)) {
			currentExportPath = aExportPath + " (" + attempt + ")";
		}

		return new BatchTaskItemImpl(aTarget, currentExportPath);
	}

	private class LibrarySingleTaskImpl implements LibrarySingleExportTask {

		private String baseName;

		private File target;

		public LibrarySingleTaskImpl(String aBaseName, File aTarget) {
			baseName = aBaseName;
			target = aTarget;
		}

		@Override
		public String getBaseName() {
			return baseName;
		}

		@Override
		public File getTarget() {
			return target;
		}

	}

	private class LibraryBatchTaskImpl implements LibraryBatchExportTask {

		private String baseName;

		private List<BatchTaskItemImpl> items;

		public LibraryBatchTaskImpl(String aBaseName, List<BatchTaskItemImpl> aItems) {
			baseName = aBaseName;
			items = aItems;
		}

		@Override
		public String getBaseName() {
			return baseName;
		}

		@Override
		public List<Item> getItems() {
			return new ArrayList<Item>(items);
		}
	}

	private class BatchTaskItemImpl implements LibraryBatchExportTask.Item {

		private File target;

		private String exportPath;

		public BatchTaskItemImpl(File aTarget, String aExportPath) {
			target = aTarget;
			exportPath = aExportPath;
		}

		@Override
		public File getTarget() {
			return target;
		}

		@Override
		public String getExportPath() {
			return exportPath;
		}

	}

}
