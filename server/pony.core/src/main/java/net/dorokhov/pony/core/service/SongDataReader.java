package net.dorokhov.pony.core.service;

import java.io.File;

public interface SongDataReader {

	public SongData read(File aFile) throws Exception;

	public static interface SongData {

		public String getPath();
		public String getFormat();
		public String getMimeType();

		public Long getSize();

		public Integer getDuration();

		public Long getBitRate();

		public Integer getDiscNumber();
		public Integer getDiscCount();

		public Integer getTrackNumber();
		public Integer getTrackCount();

		public String getName();
		public String getArtist();
		public String getAlbumArtist();
		public String getAlbum();

		public Integer getYear();

		public String getGenre();

		public ArtworkData getArtwork();
	}

	public static interface ArtworkData {

		public byte[] getBinaryData();

		public String getMimeType();
		public String getChecksum();
	}
}
