package net.dorokhov.pony.core.library.export;

import java.io.File;
import java.util.List;

public interface LibraryBatchExportTask {

	public String getBaseName();

	public List<Item> getItems();

	public static interface Item {

		public File getTarget();

		public String getExportPath();

	}

}
