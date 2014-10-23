package net.dorokhov.pony.web.utils;

import net.dorokhov.pony.core.entity.Installation;
import net.dorokhov.pony.web.domain.InstallationDto;

public class DtoConverter {

	public static InstallationDto installationToDto(Installation aInstallation) {

		InstallationDto dto = new InstallationDto();

		dto.setVersion(aInstallation.getVersion());

		return dto;
	}

}
