package net.dorokhov.pony.web.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZipCompressor {

	public static void compress(List<File> aFileList, OutputStream aOutputStream) throws IOException {

		ZipOutputStream zipOutputStream = new ZipOutputStream(aOutputStream);

		for (File file : aFileList) {
			doCompress(file, zipOutputStream);
		}

		zipOutputStream.close();
	}

	public static void compress(File aFile, OutputStream aOutputStream) throws IOException {

		ZipOutputStream zipOutputStream = new ZipOutputStream(aOutputStream);

		doCompress(aFile, zipOutputStream);
	}

	private static void doCompress(File aFile, ZipOutputStream aZipOutputStream) throws IOException {
		if (aFile.isDirectory()) {
			doCompressFolder(aFile, aFile, aZipOutputStream);
		} else {
			doCompressFile(aFile, aFile, aZipOutputStream);
		}
	}

	private static void doCompressFolder(File aCurrentFolder, File aBaseFile, ZipOutputStream aZipOutputStream) throws IOException {

		File[] files = aCurrentFolder.listFiles();

		if (files != null) {
			for (File file : files) {
				if (file.isDirectory()) {
					doCompressFolder(file, aBaseFile, aZipOutputStream);
				} else {
					doCompressFile(file, aBaseFile, aZipOutputStream);
				}
			}
		}
	}

	private static void doCompressFile(File aCurrentFile, File aBaseFile, ZipOutputStream aZipOutputStream) throws IOException {

		byte[] buf = new byte[1024];

		FileInputStream fileStream = new FileInputStream(aCurrentFile.getAbsolutePath());

		String filePath = aBaseFile.toURI().relativize(aCurrentFile.toURI()).getPath();

		aZipOutputStream.putNextEntry(new ZipEntry(filePath));

		int len;
		while ((len = fileStream.read(buf)) > 0) {
			aZipOutputStream.write(buf, 0, len);
		}

		aZipOutputStream.closeEntry();

		fileStream.close();
	}

}
