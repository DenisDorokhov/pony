package net.dorokhov.pony.web.controller;

import net.dorokhov.pony.core.common.PonyUtils;
import net.dorokhov.pony.core.dao.SongDao;
import net.dorokhov.pony.core.domain.Album;
import net.dorokhov.pony.core.domain.Song;
import net.dorokhov.pony.core.domain.StoredFile;
import net.dorokhov.pony.core.library.LibraryExportService;
import net.dorokhov.pony.core.library.exception.FileNotFoundException;
import net.dorokhov.pony.core.storage.StoredFileService;
import net.dorokhov.pony.web.common.StreamingViewRenderer;
import net.dorokhov.pony.web.common.ZipCompressor;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;

@Controller
@ResponseBody
public class FileController {

	private final Logger log = LoggerFactory.getLogger(getClass());

	private StoredFileService storedFileService;

	private SongDao songDao;

	private LibraryExportService libraryExportService;

	@Autowired
	public void setStoredFileService(StoredFileService aStoredFileService) {
		storedFileService = aStoredFileService;
	}

	@Autowired
	public void setSongDao(SongDao aSongDao) {
		songDao = aSongDao;
	}

	@Autowired
	public void setLibraryExportService(LibraryExportService aLibraryExportService) {
		libraryExportService = aLibraryExportService;
	}

	@RequestMapping(value = "/files/{id}", method = RequestMethod.GET)
	public Object getStoredFile(@PathVariable("id") Long aStoredFileId) throws IOException {

		StoredFile storedFile = storedFileService.getById(aStoredFileId);

		if (storedFile != null) {

			File file = storedFileService.getFile(storedFile);

			if (file != null) {

				StreamingViewRenderer renderer = new StreamingViewRenderer();

				HashMap<String, Object> model = new HashMap<>();

				model.put(StreamingViewRenderer.DownloadConstants.CONTENT_LENGTH, file.length());
				model.put(StreamingViewRenderer.DownloadConstants.FILENAME, file.getName());
				model.put(StreamingViewRenderer.DownloadConstants.LAST_MODIFIED, storedFile.getDate());
				model.put(StreamingViewRenderer.DownloadConstants.CONTENT_TYPE, storedFile.getMimeType());
				model.put(StreamingViewRenderer.DownloadConstants.INPUT_STREAM, new FileInputStream(file));

				return new ModelAndView(renderer, model);
			}
		}

		return new ResponseEntity<>("File not found.", HttpStatus.NOT_FOUND);
	}

	@RequestMapping(value = "/audio/{id}", method = RequestMethod.GET)
	public Object getSongFile(@PathVariable("id") Long aSongId) throws IOException {

		Song song = songDao.findOne(aSongId);

		if (song != null) {

			File songFile = new File(song.getPath());

			if (songFile.exists()) {

				StreamingViewRenderer renderer = new StreamingViewRenderer();

				HashMap<String, Object> model = new HashMap<>();

				model.put(StreamingViewRenderer.DownloadConstants.CONTENT_LENGTH, song.getSize());
				model.put(StreamingViewRenderer.DownloadConstants.FILENAME, song.getName());
				model.put(StreamingViewRenderer.DownloadConstants.LAST_MODIFIED, song.getUpdateDate() != null ? song.getUpdateDate() : song.getCreationDate());
				model.put(StreamingViewRenderer.DownloadConstants.CONTENT_TYPE, song.getMimeType());
				model.put(StreamingViewRenderer.DownloadConstants.INPUT_STREAM, new FileInputStream(songFile));

				return new ModelAndView(renderer, model);

			} else {
				log.warn("Song file [" + song.getPath() + "] not found.");
			}
		}

		return new ResponseEntity<>("Audio not found.", HttpStatus.NOT_FOUND);
	}

	@RequestMapping(value = "/export/albums/{albumId}", method = RequestMethod.GET)
	public Object exportAlbum(@PathVariable("albumId") Long aAlbumId, HttpServletResponse aResponse) throws IOException {

		List<Song> songList = songDao.findByAlbumId(aAlbumId, new Sort("discNumber", "trackNumber", "name"));

		if (songList.size() > 0) {

			File tempFolder = Files.createTempDirectory("pony.export").toFile();

			List<File> exportedFiles = null;

			try {
				exportedFiles = libraryExportService.exportSongList(songList, tempFolder);
			}  catch (FileNotFoundException e) {
				log.warn("File [" + e.getFile().getAbsolutePath() + "] not found.");
			}

			if (exportedFiles != null) {

				aResponse.setHeader("Content-Disposition", "attachment; filename=\"" + buildAlbumExportFileName(songList.get(0).getAlbum()) + "\"");

				ZipCompressor.compress(exportedFiles, aResponse.getOutputStream());

				try {
					return null;
				} finally {
					FileUtils.deleteQuietly(tempFolder);
				}
			}
		}

		return new ResponseEntity<>("Album not found.", HttpStatus.NOT_FOUND);
	}

	@RequestMapping(value = "/export/songs/{songId}", method = RequestMethod.GET)
	public Object exportSong(@PathVariable("songId") Long aSongId, HttpServletResponse aResponse) throws IOException {

		Song song = songDao.findOne(aSongId);

		if (song != null) {

			File tempFolder = Files.createTempDirectory("pony.export").toFile();

			File exportedFile = null;

			try {
				exportedFile = libraryExportService.exportSong(song, tempFolder);
			} catch (FileNotFoundException e) {
				log.warn("File [" + e.getFile().getAbsolutePath() + "] not found.");
			}

			if (exportedFile != null) {

				aResponse.setHeader("Content-Disposition", "attachment; filename=\"" + buildSongExportFileName(song, exportedFile) + "\"");

				FileUtils.copyFile(exportedFile, aResponse.getOutputStream());

				try {
					return null;
				} finally {
					FileUtils.deleteQuietly(tempFolder);
				}
			}
		}

		return new ResponseEntity<>("Song not found.", HttpStatus.NOT_FOUND);
	}

	private String buildAlbumExportFileName(Album aAlbum) {

		String fileName = aAlbum.getArtist().getName() != null ? aAlbum.getArtist().getName() : "Unknown";

		fileName += " - ";
		fileName += aAlbum.getYear() != null ? aAlbum.getYear() + " - " : "";
		fileName += aAlbum.getName() != null ? aAlbum.getName() : "Unknown";

		return PonyUtils.sanitizeFileName(fileName);
	}

	private String buildSongExportFileName(Song aSong, File aExportedFile) {

		String fileName = buildAlbumExportFileName(aSong.getAlbum());

		fileName += " - " + aExportedFile.getName();

		return PonyUtils.sanitizeFileName(fileName);
	}

}
