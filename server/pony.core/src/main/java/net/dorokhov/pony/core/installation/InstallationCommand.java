package net.dorokhov.pony.core.installation;

import net.dorokhov.pony.core.domain.Config;

import java.util.ArrayList;
import java.util.List;

public class InstallationCommand {

	private List<Config> config;

	public List<Config> getConfig() {

		if (config == null) {
			config = new ArrayList<>();
		}

		return config;
	}

	public void setConfig(List<Config> aConfig) {
		config = aConfig;
	}
}
