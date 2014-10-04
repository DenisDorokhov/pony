package net.dorokhov.pony.web.controller;

import net.dorokhov.pony.core.exception.AlreadyInstalledException;
import net.dorokhov.pony.web.service.InstallationServiceFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class InstallationController {

	private InstallationServiceFacade installationServiceFacade;

	@Autowired
	public void setInstallationServiceFacade(InstallationServiceFacade aInstallationServiceFacade) {
		installationServiceFacade = aInstallationServiceFacade;
	}

	@RequestMapping(value = "/install", method = RequestMethod.GET)
	public String showInstallation() {
		return "install";
	}

	@RequestMapping(value = "/install", method = RequestMethod.POST)
	public String doInstallation(Model aModel) {

		boolean success = true;

		try {
			installationServiceFacade.install();
		} catch (AlreadyInstalledException e) {
			success = true;
		} catch (RuntimeException e) {

			aModel.addAttribute("error", "Could not install Pony!");

			success = false;
		}

		return success ? "redirect:/" : "install";
	}

}
