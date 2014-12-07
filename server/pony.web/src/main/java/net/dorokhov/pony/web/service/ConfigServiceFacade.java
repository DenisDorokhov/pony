package net.dorokhov.pony.web.service;

import net.dorokhov.pony.web.domain.ConfigDto;

public interface ConfigServiceFacade {

	public ConfigDto get();

	public ConfigDto save(ConfigDto aConfig);

}
