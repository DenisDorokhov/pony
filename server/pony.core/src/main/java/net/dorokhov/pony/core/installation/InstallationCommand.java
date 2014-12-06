package net.dorokhov.pony.core.installation;

import net.dorokhov.pony.core.domain.Config;
import net.dorokhov.pony.core.domain.User;

import java.util.ArrayList;
import java.util.List;

public class InstallationCommand {

	private List<Config> config;

	private List<User> users;

	public List<Config> getConfig() {

		if (config == null) {
			config = new ArrayList<>();
		}

		return config;
	}

	public void setConfig(List<Config> aConfig) {
		config = aConfig;
	}

	public List<User> getUsers() {

		if (users == null) {
			users = new ArrayList<>();
		}

		return users;
	}

	public void setUsers(List<User> aUsers) {
		users = aUsers;
	}
}
