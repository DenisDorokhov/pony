package net.dorokhov.pony.core.library;

import net.dorokhov.pony.core.common.PonyUtils;
import net.dorokhov.pony.core.domain.Album;
import net.dorokhov.pony.core.domain.Artist;
import net.dorokhov.pony.core.domain.Song;
import net.dorokhov.pony.core.library.exception.FileNotFoundException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.util.*;

@Service
public class LibraryExportServiceImpl implements LibraryExportService {

	@Override
	@Transactional(readOnly = true)
	public File exportSong(Song aSong, File aFolder) throws FileNotFoundException {

		File sourceFile = new File(aSong.getPath());

		if (!sourceFile.exists()) {
			throw new FileNotFoundException(sourceFile);
		}

		File destinationFile = new File(aFolder, buildSongFileName(aSong));

		try {
			FileUtils.copyFile(sourceFile, destinationFile);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		return destinationFile;
	}

	@Override
	@Transactional(readOnly = true)
	public List<File> exportSongList(List<Song> aSongList, File aFolder) throws FileNotFoundException {

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

		List<File> exportedFiles = new ArrayList<>();

		Album currentAlbum = null;
		Artist currentArtist = null;

		File currentAlbumFolder = null;
		File currentArtistFolder = null;

		for (Song song : aSongList) {

			if (currentArtist == null || !currentArtist.equals(song.getAlbum().getArtist())) {

				currentArtist = song.getAlbum().getArtist();
				currentArtistFolder = createFolder(aFolder, buildArtistFolderName(currentArtist));

				exportedFiles.add(currentArtistFolder);
			}

			if (currentAlbum == null || !currentAlbum.equals(song.getAlbum())) {
				currentAlbum = song.getAlbum();
				currentAlbumFolder = createFolder(currentArtistFolder, buildAlbumFolderName(currentAlbum));
			}

			createSongFile(song, albumToDiscCount.get(song.getAlbum().getId()) > 1, currentAlbumFolder);
		}

		return exportedFiles;
	}

	private File createSongFile(Song aSong, boolean aCreateDiscFolder, File aParentFolder) throws FileNotFoundException {

		File sourceFile = new File(aSong.getPath());

		if (!sourceFile.exists()) {
			throw new FileNotFoundException(sourceFile);
		}

		File currentParentFolder = aParentFolder;

		if (aCreateDiscFolder) {
			currentParentFolder = new File(aParentFolder, "CD" + (aSong.getDiscNumber() != null ? aSong.getDiscNumber() : "1"));
		}

		String fileName = buildSongFileName(aSong);

		currentParentFolder.mkdirs();

		File destinationFile = new File(currentParentFolder, PonyUtils.sanitizeFileName(fileName));

		try {
			FileUtils.copyFile(sourceFile, destinationFile);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		return destinationFile;
	}

	private String buildArtistFolderName(Artist aArtist) {
		return aArtist.getName() != null ? aArtist.getName() : "Unknown";
	}

	private String buildAlbumFolderName(Album aAlbum) {

		String fileName = aAlbum.getYear() != null ? aAlbum.getYear() + " - " : "";

		fileName += aAlbum.getName() != null ? aAlbum.getName() : "Unknown";

		return fileName;
	}

	private String buildSongFileName(Song aSong) {

		String fileName;

		if (aSong.getTrackNumber() != null && aSong.getName() != null) {

			String trackNumber = aSong.getTrackNumber() <= 9 ? "0" + aSong.getTrackNumber() : String.valueOf(aSong.getTrackNumber());

			fileName = trackNumber + " - " + aSong.getName() + "." + FilenameUtils.getExtension(aSong.getPath());

		} else {
			fileName = FilenameUtils.getName(aSong.getPath());
		}

		return fileName;
	}

	private File createFolder(File aParentFolder, String aFileName) {

		File file = new File(aParentFolder, PonyUtils.sanitizeFileName(aFileName));

		file.mkdirs();

		return file;
	}

}
