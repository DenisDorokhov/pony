package net.dorokhov.pony.core.service.audio;

import net.dorokhov.pony.core.service.file.ChecksumService;
import org.apache.commons.lang3.StringUtils;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.AudioHeader;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.datatype.Artwork;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
public class SongDataServiceImpl implements SongDataService {

	private final Logger log = LoggerFactory.getLogger(getClass());

	private ChecksumService checksumService;

	@Autowired
	public void setChecksumService(ChecksumService aChecksumService) {
		checksumService = aChecksumService;
	}

	@Override
	public SongData read(File aFile) throws Exception {

		AudioFile audioFile = AudioFileIO.read(aFile);

		AudioHeader header = audioFile.getAudioHeader();
		Tag tag = audioFile.getTag();

		String mimeType = getMimeType(header);

		if (mimeType == null) {
			throw new Exception("Unsupported file format '" + header.getFormat() + "'.");
		}

		SongDataImpl metaData = new SongDataImpl();

		metaData.setPath(audioFile.getFile().getAbsolutePath());
		metaData.setFormat(header.getFormat());
		metaData.setMimeType(mimeType);
		metaData.setSize(audioFile.getFile().length());
		metaData.setDuration(header.getTrackLength());
		metaData.setBitRate(header.getBitRateAsNumber());

		if (tag != null) {

			metaData.setDiscNumber(parseIntegerTag(tag, FieldKey.DISC_NO));
			metaData.setDiscCount(parseIntegerTag(tag, FieldKey.DISC_TOTAL));

			metaData.setTrackNumber(parseIntegerTag(tag, FieldKey.TRACK));
			metaData.setTrackCount(parseIntegerTag(tag, FieldKey.TRACK_TOTAL));

			metaData.setName(StringUtils.trim(parseStringTag(tag, FieldKey.TITLE)));
			metaData.setAlbum(StringUtils.trim(parseStringTag(tag, FieldKey.ALBUM)));
			metaData.setYear(parseIntegerTag(tag, FieldKey.YEAR));

			metaData.setArtist(StringUtils.trim(parseStringTag(tag, FieldKey.ARTIST)));
			metaData.setAlbumArtist(StringUtils.trim(parseStringTag(tag, FieldKey.ALBUM_ARTIST)));

			metaData.setGenre(StringUtils.trim(parseStringTag(tag, FieldKey.GENRE)));

			Artwork artwork = tag.getFirstArtwork();

			if (artwork != null) {
				metaData.setArtwork(new ArtworkDataImpl(artwork.getBinaryData(), checksumService.calculateChecksum(artwork.getBinaryData()), artwork.getMimeType()));
			}
		}

		log.debug("read file data: {}", metaData);

		return metaData;
	}

	private String parseStringTag(Tag aTag, FieldKey aKey) {
		return StringUtils.defaultIfBlank(aTag.getFirst(aKey), null);
	}

	private Integer parseIntegerTag(Tag aTag, FieldKey aKey) {

		Integer result = null;

		//noinspection EmptyCatchBlock
		try {
			result = Integer.valueOf(aTag.getFirst(aKey));
		} catch (NumberFormatException e) {}

		return result;
	}

	private String getMimeType(AudioHeader aHeader) {

		String mimeType = null;

		if (aHeader.getFormat().equals("MPEG-1 Layer 3")) {
			mimeType = "audio/mpeg";
		}

		return mimeType;
	}

	private class SongDataImpl implements SongData {

		private String path;

		private String format;

		private String mimeType;

		private Long size;

		private Integer duration;

		private Long bitRate;

		private Integer discNumber;

		private Integer discCount;

		private Integer trackNumber;

		private Integer trackCount;

		private String name;

		private String artist;

		private String albumArtist;

		private String album;

		private Integer year;

		private String genre;

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

		public Integer getDiscNumber() {
			return discNumber;
		}

		public void setDiscNumber(Integer aDiscNumber) {
			discNumber = aDiscNumber;
		}

		public Integer getDiscCount() {
			return discCount;
		}

		public void setDiscCount(Integer aDiscCount) {
			discCount = aDiscCount;
		}

		public Integer getTrackNumber() {
			return trackNumber;
		}

		public void setTrackNumber(Integer aTrackNumber) {
			trackNumber = aTrackNumber;
		}

		public Integer getTrackCount() {
			return trackCount;
		}

		public void setTrackCount(Integer aTrackCount) {
			trackCount = aTrackCount;
		}

		public String getName() {
			return name;
		}

		public void setName(String aName) {
			name = aName;
		}

		public String getArtist() {
			return artist;
		}

		public void setArtist(String aArtist) {
			artist = aArtist;
		}

		public String getAlbumArtist() {
			return albumArtist;
		}

		public void setAlbumArtist(String aAlbumArtist) {
			albumArtist = aAlbumArtist;
		}

		public String getAlbum() {
			return album;
		}

		public void setAlbum(String aAlbum) {
			album = aAlbum;
		}

		public Integer getYear() {
			return year;
		}

		public void setYear(Integer aYear) {
			year = aYear;
		}

		public String getGenre() {
			return genre;
		}

		public void setGenre(String aGenre) {
			genre = aGenre;
		}

		public Artwork getArtwork() {
			return artwork;
		}

		public void setArtwork(Artwork aArtwork) {
			artwork = aArtwork;
		}

		@Override
		public String toString() {
			return "SongData{" +
					"path='" + path + '\'' +
					", format='" + format + '\'' +
					", mimeType='" + mimeType + '\'' +
					", size=" + size +
					", duration=" + duration +
					", bitRate=" + bitRate +
					", discNumber=" + discNumber +
					", discCount=" + discCount +
					", trackNumber=" + trackNumber +
					", trackCount=" + trackCount +
					", name='" + name + '\'' +
					", artist='" + artist + '\'' +
					", albumArtist='" + albumArtist + '\'' +
					", album='" + album + '\'' +
					", year=" + year +
					", genre='" + genre + '\'' +
					", artwork=" + artwork +
					'}';
		}
	}

	private class ArtworkDataImpl implements SongData.Artwork {

		private byte[] binaryData;

		private String mimeType;

		private String checksum;

		public ArtworkDataImpl(byte[] aBinaryData, String aChecksum, String aMimeType) {
			setBinaryData(aBinaryData);
			setChecksum(aChecksum);
			setMimeType(aMimeType);
		}

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
