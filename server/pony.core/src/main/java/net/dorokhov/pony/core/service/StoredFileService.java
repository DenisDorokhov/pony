package net.dorokhov.pony.core.service;

import net.dorokhov.pony.core.domain.StoredFile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.File;
import java.util.List;

public interface StoredFileService {

	public long getCount();

	public StoredFile getById(Long aId);
	public StoredFile getByTagAndChecksum(String aTag, String aChecksum);

	public Page<StoredFile> getAll(Pageable aPageable);
	public Page<StoredFile> getByTag(String aTag, Pageable aPageable);
	public List<StoredFile> getByChecksum(String aChecksum);

	public File getFile(Long aId);
	public File getFile(StoredFile aStoredFile);

	public StoredFile save(SaveCommand aCommand);

	public void deleteById(Long aId);
	public void deleteAll();

	public static class SaveCommand {

		public static enum Type {
			COPY, MOVE
		}

		private final Type type;

		private final File file;

		private String name;

		private String mimeType;

		private String checksum;

		private String tag;

		private String userData;

		public SaveCommand(Type aType, File aFile) {

			if (aType == null) {
				throw new NullPointerException();
			}
			if (aFile == null) {
				throw new NullPointerException();
			}

			type = aType;
			file = aFile;
		}

		public Type getType() {
			return type;
		}

		public File getFile() {
			return file;
		}

		public String getName() {
			return name;
		}

		public void setName(String aName) {
			name = aName;
		}

		public String getMimeType() {
			return mimeType;
		}

		public void setMimeType(String aMimeType) {
			mimeType = aMimeType;
		}

		public String getChecksum() {
			return checksum;
		}

		public void setChecksum(String aChecksum) {
			checksum = aChecksum;
		}

		public String getTag() {
			return tag;
		}

		public void setTag(String aTag) {
			tag = aTag;
		}

		public String getUserData() {
			return userData;
		}

		public void setUserData(String aUserData) {
			userData = aUserData;
		}

		@Override
		public String toString() {
			return "StoredFileSaveCommand{" +
					"type=" + type +
					", mimeType='" + mimeType + '\'' +
					", checksum='" + checksum + '\'' +
					", file=" + file.getAbsolutePath() +
					'}';
		}
	}
}
