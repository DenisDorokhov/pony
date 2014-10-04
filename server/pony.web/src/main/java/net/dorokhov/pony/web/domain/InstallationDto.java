package net.dorokhov.pony.web.domain;

import java.io.Serializable;

public class InstallationDto implements Serializable {

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

	@Override
	public String toString() {
		return "InstallationDto{" +
				"version='" + version + '\'' +
				'}';
	}

}
