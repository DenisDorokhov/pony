package net.dorokhov.pony.web.server.controller;

import net.dorokhov.pony.core.installation.exception.AlreadyInstalledException;
import net.dorokhov.pony.web.shared.command.InstallCommandDto;
import net.dorokhov.pony.web.server.service.InstallationServiceFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.validation.Valid;

@Controller
public class InstallationController {

	private InstallationServiceFacade installationServiceFacade;

	@Autowired
	public void setInstallationServiceFacade(InstallationServiceFacade aInstallationServiceFacade) {
		installationServiceFacade = aInstallationServiceFacade;
	}

	@RequestMapping(value = "/install", method = RequestMethod.GET)
	public String install(Model aModel) {

		InstallCommandDto command = new InstallCommandDto();

		command.getLibraryFolders().add(new InstallCommandDto.LibraryFolder());

		aModel.addAttribute("installCommand", command);

		return "install";
	}

	@RequestMapping(value = "/install", method = RequestMethod.POST)
	public String install(@Valid @ModelAttribute("installCommand") InstallCommandDto aCommand, BindingResult aBindingResult) {

		if (!aBindingResult.hasErrors()) {
			try {
				installationServiceFacade.install(aCommand);
			} catch (AlreadyInstalledException e) {
				// Ignore when already installed
			} catch (RuntimeException e) {
				aBindingResult.reject("install.error");
			}
		}

		return !aBindingResult.hasErrors() ? "redirect:/" : "install";
	}

}
