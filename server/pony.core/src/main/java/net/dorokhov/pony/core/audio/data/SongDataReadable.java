package net.dorokhov.pony.core.audio.data;

public class SongDataReadable extends SongDataAbstract {

	private String path;

	private String format;

	private String mimeType;

	private Long size;

	private Integer duration;

	private Long bitRate;

	private Artwork artwork;

	public String getPath() {
		return path;
	}

	public void setPath(String aPath) {
		path = aPath;
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String aFormat) {
		format = aFormat;
	}

	public String getMimeType() {
		return mimeType;
	}

	public void setMimeType(String aMimeType) {
		mimeType = aMimeType;
	}

	public Long getSize() {
		return size;
	}

	public void setSize(Long aSize) {
		size = aSize;
	}

	public Integer getDuration() {
		return duration;
	}

	public void setDuration(Integer aDuration) {
		duration = aDuration;
	}

	public Long getBitRate() {
		return bitRate;
	}

	public void setBitRate(Long aBitRate) {
		bitRate = aBitRate;
	}

	public Artwork getArtwork() {
		return artwork;
	}

	public void setArtwork(Artwork aArtwork) {
		artwork = aArtwork;
	}

	@Override
	public String toString() {
		return "SongDataReadable{" +
				"path='" + path + '\'' +
				", format='" + format + '\'' +
				", mimeType='" + mimeType + '\'' +
				", size=" + size +
				", duration=" + duration +
				", bitRate=" + bitRate +
				", discNumber=" + getDiscNumber() +
				", discCount=" + getDiscCount() +
				", trackNumber=" + getTrackNumber() +
				", trackCount=" + getTrackCount() +
				", title='" + getTitle() + '\'' +
				", artist='" + getArtist() + '\'' +
				", albumArtist='" + getAlbumArtist() + '\'' +
				", album='" + getAlbum() + '\'' +
				", year=" + getYear() +
				", genre='" + getGenre() + '\'' +
				", artwork=" + artwork +
				'}';
	}

	public static class Artwork {

		private byte[] binaryData;

		private String mimeType;

		private String checksum;

		public byte[] getBinaryData() {
			return binaryData;
		}

		public void setBinaryData(byte[] aBinaryData) {
			binaryData = aBinaryData;
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

		@Override
		public String toString() {
			return "Artwork{" +
					"mimeType='" + mimeType + '\'' +
					", checksum='" + checksum + '\'' +
					'}';
		}

	}

}
