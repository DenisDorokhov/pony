package net.dorokhov.pony.core.config;

import java.io.File;
import java.util.List;

public interface ConfigService {

	public Integer getAutoScanInterval();

	public void saveAutoScanInterval(Integer aValue);

	public List<File> fetchLibraryFolders();

	public void saveLibraryFolders(List<File> aValue);

}
