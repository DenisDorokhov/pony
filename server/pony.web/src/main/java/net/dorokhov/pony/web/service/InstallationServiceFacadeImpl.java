package net.dorokhov.pony.web.service;

import net.dorokhov.pony.core.domain.Installation;
import net.dorokhov.pony.core.exception.AlreadyInstalledException;
import net.dorokhov.pony.core.exception.NotInstalledException;
import net.dorokhov.pony.core.service.InstallationService;
import net.dorokhov.pony.web.domain.InstallationDto;
import net.dorokhov.pony.web.utils.DtoConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class InstallationServiceFacadeImpl implements InstallationServiceFacade {

	private InstallationService installationService;

	@Autowired
	public void setInstallationService(InstallationService aInstallationService) {
		installationService = aInstallationService;
	}

	@Override
	@Transactional(readOnly = true)
	public InstallationDto getInstallation() {

		Installation installation = installationService.getInstallation();

		return installation != null ? DtoConverter.installationToDto(installation) : null;
	}

	@Override
	@Transactional
	public InstallationDto install() throws AlreadyInstalledException {
		return DtoConverter.installationToDto(installationService.install());
	}

	@Override
	@Transactional
	public void uninstall() throws NotInstalledException {
		installationService.uninstall();
	}
}
