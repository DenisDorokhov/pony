package net.dorokhov.pony.core.installation;

import net.dorokhov.pony.core.domain.Installation;
import net.dorokhov.pony.core.installation.exception.AlreadyInstalledException;
import net.dorokhov.pony.core.installation.exception.NotInstalledException;

public interface InstallationService {

	public Installation getInstallation();

	public Installation install(InstallCommand aCommand) throws AlreadyInstalledException;

	public void uninstall() throws NotInstalledException;

}
