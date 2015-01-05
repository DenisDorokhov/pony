package net.dorokhov.pony.core.library;

import net.dorokhov.pony.core.library.export.LibraryBatchExportTask;
import net.dorokhov.pony.core.library.export.LibrarySingleExportTask;

import java.io.IOException;
import java.io.OutputStream;

public interface LibraryExportService {

	public String getSingleTaskExportMimeType();

	public String getSingleTaskExportFileExtension();

	public String getBatchTaskExportMimeType();

	public String getBatchTaskExportFileExtension();

	public void exportSingleTask(LibrarySingleExportTask aTask, OutputStream aOutputStream) throws IOException;

	public void exportBatchTask(LibraryBatchExportTask aTask, OutputStream aOutputStream) throws IOException;

}
