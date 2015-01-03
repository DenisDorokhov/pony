package net.dorokhov.pony.core.storage;

import java.io.File;

public class StoreFileCommand {

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

	public StoreFileCommand(Type aType, File aFile) {

		if (aType == null) {
			throw new IllegalArgumentException("Type must not be null.");
		}
		if (aFile == null) {
			throw new IllegalArgumentException("File must not be null.");
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
