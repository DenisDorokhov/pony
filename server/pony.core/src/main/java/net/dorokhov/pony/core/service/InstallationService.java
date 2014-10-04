package net.dorokhov.pony.core.service;

import net.dorokhov.pony.core.domain.Installation;
import net.dorokhov.pony.core.exception.AlreadyInstalledException;
import net.dorokhov.pony.core.exception.NotInstalledException;

public interface InstallationService {

	public Installation getInstallation();

	public Installation install() throws AlreadyInstalledException;

	public void uninstall() throws NotInstalledException;

}
