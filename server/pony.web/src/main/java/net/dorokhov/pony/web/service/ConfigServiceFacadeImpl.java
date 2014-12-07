package net.dorokhov.pony.web.service;

import net.dorokhov.pony.core.dao.ConfigDao;
import net.dorokhov.pony.core.domain.Config;
import net.dorokhov.pony.web.domain.ConfigDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ConfigServiceFacadeImpl implements ConfigServiceFacade {

	private ConfigDao configDao;

	private DtoConverter dtoConverter;

	@Autowired
	public void setConfigDao(ConfigDao aConfigDao) {
		configDao = aConfigDao;
	}

	@Autowired
	public void setDtoConverter(DtoConverter aDtoConverter) {
		dtoConverter = aDtoConverter;
	}

	@Override
	@Transactional(readOnly = true)
	public ConfigDto get() {
		return dtoConverter.configToDto(configDao.findAll());
	}

	@Override
	@Transactional
	public ConfigDto save(ConfigDto aConfig) {

		Config autoScanInterval = configDao.findOne(Config.AUTO_SCAN_INTERVAL);
		Config libraryFolders = configDao.findOne(Config.LIBRARY_FOLDERS);

		autoScanInterval.setInteger(aConfig.getAutoScanInterval());
		libraryFolders.setValue(dtoConverter.libraryFoldersToConfig(aConfig.getLibraryFolders()));

		configDao.save(autoScanInterval);
		configDao.save(libraryFolders);

		return dtoConverter.configToDto(configDao.findAll());
	}
}
