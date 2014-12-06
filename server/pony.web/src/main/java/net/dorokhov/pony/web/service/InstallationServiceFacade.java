package net.dorokhov.pony.web.service;

import net.dorokhov.pony.core.installation.exception.AlreadyInstalledException;
import net.dorokhov.pony.web.domain.command.InstallCommand;
import net.dorokhov.pony.web.domain.InstallationDto;

public interface InstallationServiceFacade {

	public InstallationDto getInstallation();

	public InstallationDto install(InstallCommand aCommand) throws AlreadyInstalledException;

}
