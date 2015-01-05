package net.dorokhov.pony.web.controller;

import net.dorokhov.pony.core.dao.SongDao;
import net.dorokhov.pony.core.domain.Song;
import net.dorokhov.pony.core.domain.StoredFile;
import net.dorokhov.pony.core.library.LibraryExportService;
import net.dorokhov.pony.core.library.LibraryExportTaskService;
import net.dorokhov.pony.core.library.exception.AlbumNotFoundException;
import net.dorokhov.pony.core.library.exception.ArtistNotFoundException;
import net.dorokhov.pony.core.library.exception.SongNotFoundException;
import net.dorokhov.pony.core.library.export.LibraryBatchExportTask;
import net.dorokhov.pony.core.library.export.LibrarySingleExportTask;
import net.dorokhov.pony.core.storage.StoredFileService;
import net.dorokhov.pony.web.common.StreamingViewRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.UriUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;

@Controller
@ResponseBody
public class FileController {

	private final Logger log = LoggerFactory.getLogger(getClass());

	private StoredFileService storedFileService;

	private SongDao songDao;

	private LibraryExportTaskService libraryExportTaskService;

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
	public void setLibraryExportTaskService(LibraryExportTaskService aLibraryExportTaskService) {
		libraryExportTaskService = aLibraryExportTaskService;
	}

	@Autowired
	public void setLibraryExportService(LibraryExportService aLibraryExportService) {
		libraryExportService = aLibraryExportService;
	}

	@RequestMapping(value = "/files/{id}", method = RequestMethod.GET)
	public Object getStoredFile(@PathVariable("id") Long aStoredFileId, HttpServletResponse aResponse) throws IOException {

		StoredFile storedFile = storedFileService.getById(aStoredFileId);

		if (storedFile != null) {

			File file = storedFileService.getFile(storedFile);

			if (file != null) {

				aResponse.setHeader("Content-Type", storedFile.getMimeType());

				return new FileSystemResource(file);
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

	@RequestMapping(value = "/export/artists/{artistId}", method = RequestMethod.GET)
	public Object exportArtist(@PathVariable("artistId") Long aArtistId, HttpServletResponse aResponse) throws IOException {

		LibraryBatchExportTask task = null;

		try {
			task = libraryExportTaskService.getArtistExportTask(aArtistId);
		} catch (ArtistNotFoundException e) {
			log.warn("Album [" + aArtistId + "] not found.");
		}

		if (task != null) {

			exportBatchTask(task, aResponse);

			return null;
		}

		return new ResponseEntity<>("Artist not found.", HttpStatus.NOT_FOUND);
	}

	@RequestMapping(value = "/export/albums/{albumId}", method = RequestMethod.GET)
	public Object exportAlbum(@PathVariable("albumId") Long aAlbumId, HttpServletResponse aResponse) throws IOException {

		LibraryBatchExportTask task = null;

		try {
			task = libraryExportTaskService.getAlbumExportTask(aAlbumId);
		} catch (AlbumNotFoundException e) {
			log.warn("Album [" + aAlbumId + "] not found.");
		}

		if (task != null) {

			exportBatchTask(task, aResponse);

			return null;
		}

		return new ResponseEntity<>("Album not found.", HttpStatus.NOT_FOUND);
	}

	@RequestMapping(value = "/export/songs/{songId}", method = RequestMethod.GET)
	public Object exportSong(@PathVariable("songId") Long aSongId, HttpServletResponse aResponse) throws IOException {

		LibrarySingleExportTask task = null;

		try {
			task = libraryExportTaskService.getSongExportTask(aSongId);
		} catch (SongNotFoundException e) {
			log.warn("Song [" + aSongId + "] not found.");
		}

		if (task != null) {

			String extension = libraryExportService.getSingleTaskExportFileExtension();
			if (extension.length() > 0) {
				extension = "." + extension;
			}

			aResponse.setHeader("Content-Type", libraryExportService.getSingleTaskExportMimeType());
			aResponse.setHeader("Content-Disposition", "attachment; filename=\"" + UriUtils.encodeQuery(task.getBaseName() + extension, "UTF-8") + "\"");

			libraryExportService.exportSingleTask(task, aResponse.getOutputStream());

			return null;
		}

		return new ResponseEntity<>("Song not found.", HttpStatus.NOT_FOUND);
	}

	private void exportBatchTask(LibraryBatchExportTask aTask, HttpServletResponse aResponse) throws IOException {

		String extension = libraryExportService.getBatchTaskExportFileExtension();
		if (extension.length() > 0) {
			extension = "." + extension;
		}

		aResponse.setHeader("Content-Type", libraryExportService.getBatchTaskExportMimeType());
		aResponse.setHeader("Content-Disposition", "attachment; filename=\"" + UriUtils.encodeQuery(aTask.getBaseName() + extension, "UTF-8") + "\"");

		libraryExportService.exportBatchTask(aTask, aResponse.getOutputStream());
	}

}
