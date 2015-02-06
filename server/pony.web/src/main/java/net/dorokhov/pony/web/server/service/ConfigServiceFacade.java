package net.dorokhov.pony.web.server.service;

import net.dorokhov.pony.web.shared.ConfigDto;

public interface ConfigServiceFacade {

	public ConfigDto get();

	public ConfigDto save(ConfigDto aConfig);

}
