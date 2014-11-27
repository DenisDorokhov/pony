package net.dorokhov.pony.web.service;

import net.dorokhov.pony.core.domain.Config;
import net.dorokhov.pony.core.domain.Installation;
import net.dorokhov.pony.core.installation.InstallationCommand;
import net.dorokhov.pony.core.installation.InstallationService;
import net.dorokhov.pony.core.installation.exception.AlreadyInstalledException;
import net.dorokhov.pony.web.domain.InstallationCommandDto;
import net.dorokhov.pony.web.domain.InstallationDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class InstallationServiceFacadeImpl implements InstallationServiceFacade {

	private InstallationService installationService;

	private DtoConverter dtoConverter;

	private String libraryFoldersSeparator;

	@Autowired
	public void setInstallationService(InstallationService aInstallationService) {
		installationService = aInstallationService;
	}

	@Autowired
	public void setDtoConverter(DtoConverter aDtoConverter) {
		dtoConverter = aDtoConverter;
	}

	@Value("${libraryFoldersConfig.separator}")
	public void setLibraryFoldersSeparator(String aLibraryFoldersSeparator) {
		libraryFoldersSeparator = aLibraryFoldersSeparator;
	}

	@Override
	@Transactional(readOnly = true)
	public InstallationDto getInstallation() {

		Installation installation = installationService.getInstallation();

		return installation != null ? dtoConverter.installationToDto(installation) : null;
	}

	@Override
	@Transactional
	public InstallationDto install(InstallationCommandDto aCommand) throws AlreadyInstalledException {

		InstallationCommand command = new InstallationCommand();

		command.getConfig().add(libraryPathsToConfig(aCommand.getLibraryFolders()));

		return dtoConverter.installationToDto(installationService.install(command));
	}

	private Config libraryPathsToConfig(List<String> aLibraryPaths) {

		StringBuilder buf = new StringBuilder();

		for (String path : aLibraryPaths) {

			String normalizedPath = path.trim();

			if (normalizedPath.length() > 0) {
				if (buf.length() > 0) {
					buf.append(libraryFoldersSeparator);
				}
				buf.append(normalizedPath);
			}
		}

		return new Config(Config.LIBRARY_FOLDERS, buf.toString());
	}
}
