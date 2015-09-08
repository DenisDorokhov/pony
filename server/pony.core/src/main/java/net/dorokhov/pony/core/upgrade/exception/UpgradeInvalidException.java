package net.dorokhov.pony.core.upgrade.exception;

public class UpgradeInvalidException extends Exception {

	private final String fromVersion;

	private final String toVersion;

	public UpgradeInvalidException(String aFromVersion, String aToVersion) {

		super("Upgrade from version [" + aFromVersion + "] to [" + aToVersion + "] is invalid.");

		fromVersion = aFromVersion;
		toVersion = aToVersion;
	}

	public String getFromVersion() {
		return fromVersion;
	}

	public String getToVersion() {
		return toVersion;
	}

}
