package net.dorokhov.pony.core.library;

import net.dorokhov.pony.core.audio.data.SongDataWritable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class LibraryScanEditCommand {

	private List<Task> tasks;

	public LibraryScanEditCommand() {
		this(null);
	}

	public LibraryScanEditCommand(List<Task> aTasks) {
		tasks = aTasks;
	}

	public List<Task> getTasks() {

		if (tasks == null) {
			tasks = new ArrayList<>();
		}

		return tasks;
	}

	public void setTasks(List<Task> aTasks) {
		tasks = aTasks;
	}

	public static class Task {

		private File file;

		private SongDataWritable songData;

		public Task() {
			this(null, null);
		}

		public Task(File aFile, SongDataWritable aSongData) {

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
}
