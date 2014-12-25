package net.dorokhov.pony.core.config;

import net.dorokhov.pony.core.dao.ConfigDao;
import net.dorokhov.pony.core.domain.Config;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Service
public class ConfigServiceImpl implements ConfigService {

	public static final String CONFIG_AUTO_SCAN_INTERVAL = "autoScanInterval";
	public static final String CONFIG_LIBRARY_FOLDERS = "libraryFolders";

	private ConfigDao configDao;

	private String libraryFoldersSeparator;

	@Autowired
	public void setConfigDao(ConfigDao aConfigDao) {
		configDao = aConfigDao;
	}

	@Value("${libraryFoldersConfig.separator}")
	public void setLibraryFoldersSeparator(String aLibraryFoldersSeparator) {
		libraryFoldersSeparator = aLibraryFoldersSeparator;
	}

	@Override
	public Integer getAutoScanInterval() {

		Config config = configDao.findOne(CONFIG_AUTO_SCAN_INTERVAL);

		return config != null ? config.getInteger() : null;
	}

	@Override
	public void saveAutoScanInterval(Integer aValue) {

		Config config = configDao.findOne(CONFIG_AUTO_SCAN_INTERVAL);
		if (config == null) {
			config = new Config(CONFIG_AUTO_SCAN_INTERVAL);
		}

		config.setInteger(aValue);

		configDao.save(config);
	}

	@Override
	public List<File> fetchLibraryFolders() {

		Config config = configDao.findOne(CONFIG_LIBRARY_FOLDERS);

		List<File> fileList = new ArrayList<>();

		if (config != null && config.getValue() != null) {
			for (String path : config.getValue().split(libraryFoldersSeparator)) {

				String normalizedPath = path.trim();

				if (normalizedPath.length() > 0) {
					fileList.add(new File(normalizedPath));
				}
			}
		}

		return fileList;
	}

	@Override
	public void saveLibraryFolders(List<File> aValue) {

		List<String> pathList = new ArrayList<>();

		for (File file : aValue) {
			pathList.add(file.getAbsolutePath());
		}

		String stringValue = StringUtils.join(pathList, libraryFoldersSeparator);
		if (stringValue.length() == 0) {
			stringValue = null;
		}

		Config config = configDao.findOne(CONFIG_LIBRARY_FOLDERS);
		if (config == null) {
			config = new Config(CONFIG_LIBRARY_FOLDERS);
		}

		config.setValue(stringValue);

		configDao.save(config);
	}
}
