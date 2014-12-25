package net.dorokhov.pony.web.service;

import net.dorokhov.pony.core.config.ConfigService;
import net.dorokhov.pony.web.domain.ConfigDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Service
public class ConfigServiceFacadeImpl implements ConfigServiceFacade {

	private ConfigService configService;

	@Autowired
	public void setConfigService(ConfigService aConfigService) {
		configService = aConfigService;
	}

	@Override
	@Transactional(readOnly = true)
	public ConfigDto get() {

		ConfigDto dto = new ConfigDto();

		dto.setAutoScanInterval(configService.getAutoScanInterval());

		for (File folder : configService.fetchLibraryFolders()) {
			dto.getLibraryFolders().add(folder.getAbsolutePath());
		}

		return dto;
	}

	@Override
	@Transactional
	public ConfigDto save(ConfigDto aConfig) {

		configService.saveAutoScanInterval(aConfig.getAutoScanInterval());

		List<File> libraryFolders = new ArrayList<>();
		for (String path : aConfig.getLibraryFolders()) {

			String normalizedPath = path.trim();

			if (normalizedPath.length() > 0) {
				libraryFolders.add(new File(normalizedPath));
			}
		}
		configService.saveLibraryFolders(libraryFolders);

		return get();
	}

}
