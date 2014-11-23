package net.dorokhov.pony.web.controller;

import net.dorokhov.pony.web.domain.InstallationCommandDto;
import net.dorokhov.pony.core.installation.exception.AlreadyInstalledException;
import net.dorokhov.pony.web.service.InstallationServiceFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Arrays;
import java.util.Locale;

@Controller
public class InstallationController {

	private MessageSource messageSource;

	private InstallationServiceFacade installationServiceFacade;

	@Autowired
	public void setMessageSource(MessageSource aMessageSource) {
		messageSource = aMessageSource;
	}

	@Autowired
	public void setInstallationServiceFacade(InstallationServiceFacade aInstallationServiceFacade) {
		installationServiceFacade = aInstallationServiceFacade;
	}

	@RequestMapping(value = "/install", method = RequestMethod.GET)
	public String showInstallation() {
		return "install";
	}

	@RequestMapping(value = "/install", method = RequestMethod.POST)
	public String install(
			@RequestParam("libraryFolder[]") String[] aLibraryFolders,
			@RequestParam("adminLogin") String aAdminLogin,
			@RequestParam("adminPassword") String aAdminPassword,
			Model aModel, Locale aLocale) {

		InstallationCommandDto command = new InstallationCommandDto();

		command.setAdminLogin(aAdminLogin);
		command.setAdminPassword(aAdminPassword);
		command.setLibraryFolders(Arrays.asList(aLibraryFolders));

		boolean success = true;

		try {
			installationServiceFacade.install(command);
		} catch (AlreadyInstalledException e) {
			success = true;
		} catch (RuntimeException e) {

			aModel.addAttribute("error", messageSource.getMessage("install.error", null, aLocale));

			success = false;
		}

		return success ? "redirect:/" : "install";
	}

}
