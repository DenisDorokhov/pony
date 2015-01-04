package net.dorokhov.pony.core.library;

import net.dorokhov.pony.core.dao.SongDao;
import net.dorokhov.pony.core.domain.Album;
import net.dorokhov.pony.core.domain.Artist;
import net.dorokhov.pony.core.domain.Song;
import net.dorokhov.pony.core.library.exception.AlbumNotFoundException;
import net.dorokhov.pony.core.library.exception.ArtistNotFoundException;
import net.dorokhov.pony.core.library.exception.FileNotFoundException;
import net.dorokhov.pony.core.library.exception.SongNotFoundException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
public class LibraryExportServiceImpl implements LibraryExportService {

	private SongDao songDao;

	@Autowired
	public void setSongDao(SongDao aSongDao) {
		songDao = aSongDao;
	}

	@Override
	public ExportDescriptor exportArtistSongs(Long aId, OutputStream aOutputStream) throws ArtistNotFoundException, FileNotFoundException {

		List<Song> songList = songDao.findByAlbumArtistId(aId, new Sort("album.year", "album.name", "discNumber", "trackNumber", "name"));

		if (songList.size() == 0) {
			throw new ArtistNotFoundException(aId);
		}

		File tempFolder = createTempFolder();

		File targetFolder = createSongListFolder(songList, tempFolder);

		try {
			compressFolder(targetFolder, new ZipOutputStream(aOutputStream));
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			FileUtils.deleteQuietly(targetFolder);
		}

		Artist artist = songList.get(0).getAlbum().getArtist();

		return new ExportDescriptorImpl("application/zip", buildArtistFolderName(artist) + ".zip");
	}

	@Override
	public ExportDescriptor exportAlbumSongs(Long aId, OutputStream aOutputStream) throws AlbumNotFoundException, FileNotFoundException {

		List<Song> songList = songDao.findByAlbumId(aId, new Sort("album.year", "album.name", "discNumber", "trackNumber", "name"));

		if (songList.size() == 0) {
			throw new AlbumNotFoundException(aId);
		}

		File tempFolder = createTempFolder();

		File targetFolder = createSongListFolder(songList, tempFolder);

		try {
			compressFolder(targetFolder, new ZipOutputStream(aOutputStream));
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			FileUtils.deleteQuietly(targetFolder);
		}

		Album album = songList.get(0).getAlbum();

		return new ExportDescriptorImpl("application/zip", buildArtistFolderName(album.getArtist()) + " - " + buildAlbumFolderName(album) + ".zip");
	}

	@Override
	public ExportDescriptor exportSong(Long aId, OutputStream aOutputStream) throws SongNotFoundException, FileNotFoundException {

		Song song = songDao.findOne(aId);

		if (song == null) {
			throw new SongNotFoundException(aId);
		}

		File file = new File(song.getPath());

		if (!file.exists()) {
			throw new FileNotFoundException(file);
		}

		try {
			FileUtils.copyFile(file, aOutputStream);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		return new ExportDescriptorImpl(song.getMimeType(), file.getName());
	}

	private File createSongListFolder(List<Song> aSongList, File aParentFolder) throws FileNotFoundException {

		Collections.sort(aSongList);

		int discCount = 1;

		for (Song song : aSongList) {
			if (song.getDiscNumber() != null && song.getDiscNumber() > 1) {
				discCount = song.getDiscNumber();
			}
		}

		Artist artist = aSongList.get(0).getAlbum().getArtist();

		File artistFolder = createFolder(aParentFolder, buildArtistFolderName(artist));

		Album currentAlbum = null;
		File currentAlbumFolder = null;

		for (Song song : aSongList) {

			if (currentAlbum == null || !currentAlbum.equals(song.getAlbum())) {
				currentAlbum = song.getAlbum();
				currentAlbumFolder = createFolder(artistFolder, buildAlbumFolderName(currentAlbum));
			}

			createSongFile(song, discCount > 1, currentAlbumFolder);
		}

		return artistFolder;
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

		File destinationFile = new File(currentParentFolder, normalizeFileName(fileName));

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
			fileName = normalizeTrackNumber(aSong.getTrackNumber()) + " - " + aSong.getName() + "." + FilenameUtils.getExtension(aSong.getPath());
		} else {
			fileName = FilenameUtils.getName(aSong.getPath());
		}

		return fileName;
	}

	private String normalizeFileName(String aFileName) {
		return aFileName.replaceAll("[^ a-zA-Z0-9.-]", "_").trim();
	}

	private String normalizeTrackNumber(int aTrackNumber) {
		return aTrackNumber < 9 ? "0" + aTrackNumber : String.valueOf(aTrackNumber);
	}

	private File createFolder(File aParentFolder, String aFileName) {

		File file = new File(aParentFolder, normalizeFileName(aFileName));

		file.mkdirs();

		return file;
	}

	private File createTempFolder() {

		File file = new File(FileUtils.getTempDirectory(), "pony.export." + UUID.randomUUID() + ".tmp");

		file.mkdir();

		return file;
	}

	private void compressFolder(File aFolder, ZipOutputStream aZipStream) throws IOException {

		File[] files = aFolder.listFiles();

		if (files != null) {

			byte[] buf = new byte[1024];

			for (File file : files) {

				if (file.isDirectory()) {

					compressFolder(file, aZipStream);

				} else {

					FileInputStream fileStream = new FileInputStream(file.getAbsolutePath());

					aZipStream.putNextEntry(new ZipEntry(file.getAbsolutePath()));

					int len;
					while ((len = fileStream.read(buf)) > 0) {
						aZipStream.write(buf, 0, len);
					}

					aZipStream.closeEntry();

					fileStream.close();
				}
			}
		}
	}

	private class ExportDescriptorImpl implements ExportDescriptor {

		private final String mimeType;

		private final String fileName;

		public ExportDescriptorImpl(String aMimeType, String aFileName) {
			mimeType = aMimeType;
			fileName = aFileName;
		}

		@Override
		public String getMimeType() {
			return mimeType;
		}

		@Override
		public String getFileName() {
			return fileName;
		}

	}

}
