package net.dorokhov.pony.core.library;

import net.dorokhov.pony.core.audio.data.SongDataWritable;

import java.io.File;

public class LibraryScanEditCommand {

	private File file;

	private SongDataWritable songData;

	public LibraryScanEditCommand() {
		this(null, null);
	}

	public LibraryScanEditCommand(File aFile, SongDataWritable aSongData) {

		if (aFile == null) {
			throw new NullPointerException();
		}
		if (aSongData == null) {
			throw new NullPointerException();
		}

		file = aFile;
		songData = aSongData;
	}

	public File getFile() {
		return file;
	}

	public void setFile(File aFile) {
		file = aFile;
	}

	public SongDataWritable getSongData() {
		return songData;
	}

	public void setSongData(SongDataWritable aSongData) {
		songData = aSongData;
	}
}
