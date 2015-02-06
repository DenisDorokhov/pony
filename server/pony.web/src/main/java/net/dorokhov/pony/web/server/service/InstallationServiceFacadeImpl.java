package net.dorokhov.pony.web.server.service;

import net.dorokhov.pony.core.domain.Installation;
import net.dorokhov.pony.core.domain.User;
import net.dorokhov.pony.core.installation.InstallCommand;
import net.dorokhov.pony.core.installation.InstallationService;
import net.dorokhov.pony.core.installation.exception.AlreadyInstalledException;
import net.dorokhov.pony.web.shared.InstallationDto;
import net.dorokhov.pony.web.shared.RoleDto;
import net.dorokhov.pony.web.shared.command.InstallCommandDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;

@Service
public class InstallationServiceFacadeImpl implements InstallationServiceFacade {

	private static final int AUTO_SCAN_INTERVAL = 86400;

	private InstallationService installationService;

	@Autowired
	public void setInstallationService(InstallationService aInstallationService) {
		installationService = aInstallationService;
	}

	@Override
	@Transactional(readOnly = true)
	public InstallationDto getInstallation() {

		Installation installation = installationService.getInstallation();

		return installation != null ? InstallationDto.valueOf(installation) : null;
	}

	@Override
	@Transactional
	public InstallationDto install(InstallCommandDto aCommand) throws AlreadyInstalledException {

		InstallCommand command = new InstallCommand();

		command.setAutoScanInterval(AUTO_SCAN_INTERVAL);

		for (InstallCommandDto.LibraryFolder folder : aCommand.getLibraryFolders()) {

			String normalizedPath = folder.getPath().trim();

			if (normalizedPath.length() > 0) {
				command.getLibraryFolders().add(new File(normalizedPath));
			}
		}

		User admin = new User();

		admin.setName(aCommand.getUserName());
		admin.setEmail(aCommand.getUserEmail());
		admin.setPassword(aCommand.getUserPassword());

		admin.getRoles().add(RoleDto.USER.toString());
		admin.getRoles().add(RoleDto.ADMIN.toString());

		command.getUsers().add(admin);

		return InstallationDto.valueOf(installationService.install(command));
	}

}
