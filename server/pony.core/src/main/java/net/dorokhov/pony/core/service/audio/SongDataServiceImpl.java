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

		SongData songData = readSongData(AudioFileIO.read(aFile));

		log.debug("song data has been read: {}", songData);

		return songData;
	}

	@Override
	public SongData write(SongDataWriteCommand aCommand) throws Exception {

		AudioFile audioFile = AudioFileIO.read(aCommand.getFile());

		Tag tag = audioFile.getTag();

		setOrDeleteTagField(tag, FieldKey.DISC_NO, aCommand.getDiscNumber());
		setOrDeleteTagField(tag, FieldKey.DISC_TOTAL, aCommand.getDiscCount());

		setOrDeleteTagField(tag, FieldKey.TRACK, aCommand.getTrackNumber());
		setOrDeleteTagField(tag, FieldKey.TRACK_TOTAL, aCommand.getTrackCount());

		setOrDeleteTagField(tag, FieldKey.TITLE, aCommand.getTitle());
		setOrDeleteTagField(tag, FieldKey.ALBUM, aCommand.getAlbum());
		setOrDeleteTagField(tag, FieldKey.YEAR, aCommand.getYear());

		setOrDeleteTagField(tag, FieldKey.ARTIST, aCommand.getArtist());
		setOrDeleteTagField(tag, FieldKey.ALBUM_ARTIST, aCommand.getAlbumArtist());

		setOrDeleteTagField(tag, FieldKey.GENRE, aCommand.getGenre());

		tag.deleteArtworkField();

		if (aCommand.getArtwork() != null) {
			tag.setField(Artwork.createArtworkFromFile(aCommand.getArtwork()));
		}

		AudioFileIO.write(audioFile);

		SongData songData = readSongData(audioFile);

		log.debug("song data has been written: {}", songData);

		return songData;
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
			mimeType = "audio/mpeg3";
		}

		return mimeType;
	}

	private void setOrDeleteTagField(Tag aTag, FieldKey aKey, Object aValue) throws Exception {
		if (aValue != null) {
			aTag.setField(aKey, aValue.toString());
		} else {
			aTag.deleteField(aKey);
		}
	}

	private SongData readSongData(AudioFile aAudioFile) throws Exception {

		AudioHeader header = aAudioFile.getAudioHeader();
		Tag tag = aAudioFile.getTag();

		String mimeType = getMimeType(header);

		if (mimeType == null) {
			throw new Exception("Unsupported file format '" + header.getFormat() + "'.");
		}

		SongDataImpl songData = new SongDataImpl();

		songData.setPath(aAudioFile.getFile().getAbsolutePath());
		songData.setFormat(header.getFormat());
		songData.setMimeType(mimeType);
		songData.setSize(aAudioFile.getFile().length());
		songData.setDuration(header.getTrackLength());
		songData.setBitRate(header.getBitRateAsNumber());

		if (tag != null) {
			readTagToSongData(tag, songData);
		}

		return songData;
	}

	private void readTagToSongData(Tag aTag, SongDataImpl aSongData) {

		aSongData.setDiscNumber(parseIntegerTag(aTag, FieldKey.DISC_NO));
		aSongData.setDiscCount(parseIntegerTag(aTag, FieldKey.DISC_TOTAL));

		aSongData.setTrackNumber(parseIntegerTag(aTag, FieldKey.TRACK));
		aSongData.setTrackCount(parseIntegerTag(aTag, FieldKey.TRACK_TOTAL));

		aSongData.setTitle(StringUtils.trim(parseStringTag(aTag, FieldKey.TITLE)));
		aSongData.setAlbum(StringUtils.trim(parseStringTag(aTag, FieldKey.ALBUM)));
		aSongData.setYear(parseIntegerTag(aTag, FieldKey.YEAR));

		aSongData.setArtist(StringUtils.trim(parseStringTag(aTag, FieldKey.ARTIST)));
		aSongData.setAlbumArtist(StringUtils.trim(parseStringTag(aTag, FieldKey.ALBUM_ARTIST)));

		aSongData.setGenre(StringUtils.trim(parseStringTag(aTag, FieldKey.GENRE)));

		Artwork artwork = aTag.getFirstArtwork();

		if (artwork != null) {
			aSongData.setArtwork(new ArtworkDataImpl(artwork.getBinaryData(), checksumService.calculateChecksum(artwork.getBinaryData()), artwork.getMimeType()));
		}
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

		private String title;

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

		public String getTitle() {
			return title;
		}

		public void setTitle(String aTitle) {
			title = aTitle;
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
					", title='" + title + '\'' +
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
