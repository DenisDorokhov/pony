package net.dorokhov.pony.web.server.service;

import net.dorokhov.pony.core.config.ConfigService;
import net.dorokhov.pony.web.shared.ConfigDto;
import net.dorokhov.pony.web.shared.LibraryFolderDto;
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
			dto.getLibraryFolders().add(new LibraryFolderDto(folder.getAbsolutePath()));
		}

		return dto;
	}

	@Override
	@Transactional
	public ConfigDto save(ConfigDto aConfig) {

		configService.saveAutoScanInterval(aConfig.getAutoScanInterval());

		List<File> libraryFolders = new ArrayList<>();
		for (LibraryFolderDto folder : aConfig.getLibraryFolders()) {
			if (folder != null && folder.getPath() != null) {

				String normalizedPath = folder.getPath().trim();

				if (normalizedPath.length() > 0) {
					libraryFolders.add(new File(normalizedPath));
				}
			}
		}
		configService.saveLibraryFolders(libraryFolders);

		return get();
	}

}
