package net.dorokhov.pony.web.controller;

import net.dorokhov.pony.core.domain.StoredFile;
import net.dorokhov.pony.core.storage.StoredFileService;
import net.dorokhov.pony.web.common.StreamingViewRenderer;
import net.dorokhov.pony.web.domain.InstallationDto;
import net.dorokhov.pony.web.domain.response.ResponseWithResult;
import net.dorokhov.pony.web.service.InstallationServiceFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;

@RestController
@RequestMapping(value = "/api", produces = "application/json")
public class ApiController {

	private final Logger log = LoggerFactory.getLogger(getClass());

	private InstallationServiceFacade installationServiceFacade;

	private StoredFileService storedFileService;

	@Autowired
	public void setInstallationServiceFacade(InstallationServiceFacade aInstallationServiceFacade) {
		installationServiceFacade = aInstallationServiceFacade;
	}

	@Autowired
	public void setStoredFileService(StoredFileService aStoredFileService) {
		storedFileService = aStoredFileService;
	}

	@RequestMapping(value = "/installation", method = RequestMethod.GET)
	public ResponseWithResult<InstallationDto> getInstallation() {

		try {
			return new ResponseWithResult<>(installationServiceFacade.getInstallation());
		} catch (Exception e) {
			log.error("could not get installation", e);
		}

		return new ResponseWithResult<>();
	}

	@RequestMapping(value = "/file/{storedFileId}", method = RequestMethod.GET)
	public Object getStoredFile(@PathVariable("storedFileId") Long aStoredFileId) {

		try {

			StoredFile storedFile = storedFileService.getById(aStoredFileId);

			if (storedFile != null) {

				File file = storedFileService.getFile(storedFile);

				if (file != null) {

					InputStream stream = new FileInputStream(file);

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

			return new ResponseEntity<String>(HttpStatus.NOT_FOUND);

		} catch (Exception e) {
			log.error("could not get stored file", e);
		}

		return new ResponseEntity<String>(HttpStatus.SERVICE_UNAVAILABLE);
	}

}
