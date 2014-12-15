package net.dorokhov.pony.web.service;

import net.dorokhov.pony.core.domain.Config;
import net.dorokhov.pony.core.domain.Installation;
import net.dorokhov.pony.core.domain.User;
import net.dorokhov.pony.core.installation.InstallationCommand;
import net.dorokhov.pony.core.installation.InstallationService;
import net.dorokhov.pony.core.installation.exception.AlreadyInstalledException;
import net.dorokhov.pony.web.domain.InstallationDto;
import net.dorokhov.pony.web.domain.RoleDto;
import net.dorokhov.pony.web.domain.command.InstallCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class InstallationServiceFacadeImpl implements InstallationServiceFacade {

	private InstallationService installationService;

	private DtoConverter dtoConverter;

	@Autowired
	public void setInstallationService(InstallationService aInstallationService) {
		installationService = aInstallationService;
	}

	@Autowired
	public void setDtoConverter(DtoConverter aDtoConverter) {
		dtoConverter = aDtoConverter;
	}

	@Override
	@Transactional(readOnly = true)
	public InstallationDto getInstallation() {

		Installation installation = installationService.getInstallation();

		return installation != null ? dtoConverter.installationToDto(installation) : null;
	}

	@Override
	@Transactional
	public InstallationDto install(InstallCommand aCommand) throws AlreadyInstalledException {

		InstallationCommand command = new InstallationCommand();

		String libraryFoldersConfig = dtoConverter.libraryFoldersToConfig(aCommand.getLibraryFolders());

		command.getConfig().add(new Config(Config.LIBRARY_FOLDERS, libraryFoldersConfig));

		User admin = new User();

		admin.setName(aCommand.getUserName());
		admin.setEmail(aCommand.getUserEmail());
		admin.setPassword(aCommand.getUserPassword());

		admin.getRoles().add(RoleDto.Strings.USER);
		admin.getRoles().add(RoleDto.Strings.ADMIN);

		command.getUsers().add(admin);

		return dtoConverter.installationToDto(installationService.install(command));
	}
}
