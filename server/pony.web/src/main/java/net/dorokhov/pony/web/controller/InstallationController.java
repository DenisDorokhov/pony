package net.dorokhov.pony.web.controller;

import net.dorokhov.pony.core.installation.exception.AlreadyInstalledException;
import net.dorokhov.pony.web.domain.command.InstallCommand;
import net.dorokhov.pony.web.service.InstallationServiceFacade;
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

		InstallCommand command = new InstallCommand();

		command.getLibraryFolders().add(new InstallCommand.LibraryFolder());

		aModel.addAttribute("installCommand", command);

		return "install";
	}

	@RequestMapping(value = "/install", method = RequestMethod.POST)
	public String install(@Valid @ModelAttribute("installCommand") InstallCommand aInstallCommand, BindingResult aBindingResult) {

		if (!aBindingResult.hasErrors()) {
			try {
				installationServiceFacade.install(aInstallCommand);
			} catch (AlreadyInstalledException e) {
				// Ignore when already installed
			} catch (RuntimeException e) {
				aBindingResult.reject("install.error");
			}
		}

		return !aBindingResult.hasErrors() ? "redirect:/" : "install";
	}

}
