package net.dorokhov.pony.web.controller;

import net.dorokhov.pony.web.domain.InstallationDto;
import net.dorokhov.pony.web.domain.response.ResponseWithResult;
import net.dorokhov.pony.web.service.InstallationServiceFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value = "/api", produces = "application/json")
public class ApiController {

	private final Logger log = LoggerFactory.getLogger(getClass());

	private InstallationServiceFacade installationServiceFacade;

	@Autowired
	public void setInstallationServiceFacade(InstallationServiceFacade aInstallationServiceFacade) {
		installationServiceFacade = aInstallationServiceFacade;
	}

	@RequestMapping(value = "/installation", method = RequestMethod.GET)
	@ResponseBody
	public ResponseWithResult<InstallationDto> getInstallation() {

		try {
			return new ResponseWithResult<>(installationServiceFacade.getInstallation());
		} catch (Exception e) {
			log.error("could not get installation", e);
		}

		return new ResponseWithResult<>();
	}

}
