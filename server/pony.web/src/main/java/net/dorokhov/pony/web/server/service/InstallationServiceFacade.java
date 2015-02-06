package net.dorokhov.pony.web.server.service;

import net.dorokhov.pony.core.installation.exception.AlreadyInstalledException;
import net.dorokhov.pony.web.shared.command.InstallCommandDto;
import net.dorokhov.pony.web.shared.InstallationDto;

public interface InstallationServiceFacade {

	public InstallationDto getInstallation();

	public InstallationDto install(InstallCommandDto aCommand) throws AlreadyInstalledException;

}
