package net.dorokhov.pony.web.shared;

import net.dorokhov.pony.core.domain.Installation;

public class InstallationDto {

	private String version;

	public String getVersion() {
		return version;
	}

	public void setVersion(String aVersion) {
		version = aVersion;
	}

	@Override
	public int hashCode() {
		return getVersion() != null ? getVersion().hashCode() : super.hashCode();
	}

	@Override
	public boolean equals(Object aObj) {

		if (this == aObj) {
			return true;
		}

		if (aObj != null && getVersion() != null && getClass().equals(aObj.getClass())) {

			InstallationDto entity = (InstallationDto)aObj;

			return getVersion().equals(entity.getVersion());
		}

		return false;
	}

	public static InstallationDto valueOf(Installation aInstallation) {

		InstallationDto dto = new InstallationDto();

		dto.setVersion(aInstallation.getVersion());

		return dto;
	}

}
