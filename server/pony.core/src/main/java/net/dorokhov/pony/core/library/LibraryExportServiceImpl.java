package net.dorokhov.pony.core.library;

import net.dorokhov.pony.core.library.export.LibraryBatchExportTask;
import net.dorokhov.pony.core.library.export.LibrarySingleExportTask;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
public class LibraryExportServiceImpl implements LibraryExportService {

	@Override
	public String getSingleTaskExportMimeType() {
		return "audio/mpeg3";
	}

	@Override
	public String getSingleTaskExportFileExtension() {
		return "mp3";
	}

	@Override
	public String getBatchTaskExportMimeType() {
		return "application/zip";
	}

	@Override
	public String getBatchTaskExportFileExtension() {
		return "zip";
	}

	@Override
	public void exportSingleTask(LibrarySingleExportTask aTask, OutputStream aOutputStream) throws IOException {

		String extension = FilenameUtils.getExtension(aTask.getTarget().getName());

		if (!extension.equals("mp3")) {
			throw new IOException("Task target extension [" + extension + "] not supported.");
		}

		FileUtils.copyFile(aTask.getTarget(), aOutputStream);
	}

	@Override
	public void exportBatchTask(LibraryBatchExportTask aTask, OutputStream aOutputStream) throws IOException {

		ZipOutputStream zipOutputStream = new ZipOutputStream(aOutputStream);

		for (LibraryBatchExportTask.Item item : aTask.getItems()) {

			String path = item.getExportPath();

			String extension = FilenameUtils.getExtension(item.getTarget().getName());
			if (extension.length() > 0) {
				path += "." + extension;
			}

			compressFile(item.getTarget(), path, zipOutputStream);
		}

		zipOutputStream.close();
	}

	private void compressFile(File aFile, String aPath, ZipOutputStream aZipOutputStream) throws IOException {

		byte[] buf = new byte[1024];

		FileInputStream fileStream = new FileInputStream(aFile);

		aZipOutputStream.putNextEntry(new ZipEntry(aPath));

		int len;
		while ((len = fileStream.read(buf)) > 0) {
			aZipOutputStream.write(buf, 0, len);
		}

		aZipOutputStream.closeEntry();

		fileStream.close();
	}

}
