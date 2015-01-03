package net.dorokhov.pony.web.service;

import net.dorokhov.pony.core.installation.exception.AlreadyInstalledException;
import net.dorokhov.pony.web.domain.command.InstallCommandDto;
import net.dorokhov.pony.web.domain.InstallationDto;

public interface InstallationServiceFacade {

	public InstallationDto getInstallation();

	public InstallationDto install(InstallCommandDto aCommand) throws AlreadyInstalledException;

}
