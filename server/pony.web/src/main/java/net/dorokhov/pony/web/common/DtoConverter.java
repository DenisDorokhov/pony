package net.dorokhov.pony.web.common;

import net.dorokhov.pony.core.domain.Installation;
import net.dorokhov.pony.web.domain.InstallationDto;

public class DtoConverter {

	public static InstallationDto installationToDto(Installation aInstallation) {

		InstallationDto dto = new InstallationDto();

		dto.setVersion(aInstallation.getVersion());

		return dto;
	}

}
