package net.dorokhov.pony.web.controller;

import net.dorokhov.pony.core.dao.SongDao;
import net.dorokhov.pony.core.domain.Song;
import net.dorokhov.pony.core.domain.StoredFile;
import net.dorokhov.pony.core.storage.StoredFileService;
import net.dorokhov.pony.web.common.StreamingViewRenderer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;

@Controller
@ResponseBody
public class FileController {

	private StoredFileService storedFileService;

	private SongDao songDao;

	@Autowired
	public void setStoredFileService(StoredFileService aStoredFileService) {
		storedFileService = aStoredFileService;
	}

	@Autowired
	public void setSongDao(SongDao aSongDao) {
		songDao = aSongDao;
	}

	@RequestMapping(value = "/files/{id}", method = RequestMethod.GET)
	public Object getStoredFile(@PathVariable("id") Long aStoredFileId) throws FileNotFoundException {

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
	public Object getSongFile(@PathVariable("id") Long aSongId) throws FileNotFoundException {

		Song song = songDao.findOne(aSongId);

		if (song != null) {

			StreamingViewRenderer renderer = new StreamingViewRenderer();

			HashMap<String, Object> model = new HashMap<>();

			model.put(StreamingViewRenderer.DownloadConstants.CONTENT_LENGTH, song.getSize());
			model.put(StreamingViewRenderer.DownloadConstants.FILENAME, song.getName());
			model.put(StreamingViewRenderer.DownloadConstants.LAST_MODIFIED, song.getUpdateDate() != null ? song.getUpdateDate() : song.getCreationDate());
			model.put(StreamingViewRenderer.DownloadConstants.CONTENT_TYPE, song.getMimeType());
			model.put(StreamingViewRenderer.DownloadConstants.INPUT_STREAM, new FileInputStream(new File(song.getPath())));

			return new ModelAndView(renderer, model);
		}

		return new ResponseEntity<>("Audio not found.", HttpStatus.NOT_FOUND);
	}
}
