package net.dorokhov.pony.web.controller;

import net.dorokhov.pony.core.domain.StoredFile;
import net.dorokhov.pony.core.storage.StoredFileService;
import net.dorokhov.pony.web.common.StreamingViewRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import java.io.InputStream;
import java.util.HashMap;

@Controller
public class FilesController {

	private final Logger log = LoggerFactory.getLogger(getClass());

	private StoredFileService storedFileService;

	@Autowired
	public void setStoredFileService(StoredFileService aStoredFileService) {
		storedFileService = aStoredFileService;
	}

	@RequestMapping(value = "/files/{id}", method = RequestMethod.GET)
	@ResponseBody
	public Object getStoredFile(@PathVariable("id") Long aStoredFileId) {

		StoredFile storedFile = storedFileService.getById(aStoredFileId);

		if (storedFile != null) {

			File file = storedFileService.getFile(storedFile);

			if (file != null) {

				InputStream stream = null;

				try {
					stream = new FileInputStream(file);
				} catch (FileNotFoundException e) {
					log.error("could not find file", e);
				}

				if (stream != null) {

					StreamingViewRenderer renderer = new StreamingViewRenderer();

					HashMap<String, Object> model = new HashMap<>();

					model.put(StreamingViewRenderer.DownloadConstants.CONTENT_LENGTH, file.length());
					model.put(StreamingViewRenderer.DownloadConstants.FILENAME, file.getName());
					model.put(StreamingViewRenderer.DownloadConstants.LAST_MODIFIED, storedFile.getDate());
					model.put(StreamingViewRenderer.DownloadConstants.CONTENT_TYPE, storedFile.getMimeType());
					model.put(StreamingViewRenderer.DownloadConstants.INPUT_STREAM, stream);

					return new ModelAndView(renderer, model);
				}
			}
		}

		return new ResponseEntity<String>(HttpStatus.NOT_FOUND);
	}
}
