package net.dorokhov.pony.core.audio;

import net.dorokhov.pony.core.audio.data.SongDataReadable;
import net.dorokhov.pony.core.audio.data.SongDataWritable;
import net.dorokhov.pony.core.file.ChecksumService;
import org.apache.commons.lang3.StringUtils;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.AudioHeader;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.datatype.Artwork;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
public class SongDataServiceImpl implements SongDataService {

	private ChecksumService checksumService;

	@Autowired
	public void setChecksumService(ChecksumService aChecksumService) {
		checksumService = aChecksumService;
	}

	@Override
	public SongDataReadable read(File aFile) throws Exception {
		return readSongData(AudioFileIO.read(aFile));
	}

	@Override
	public SongDataReadable write(File aFile, SongDataWritable aCommand) throws Exception {

		AudioFile audioFile = AudioFileIO.read(aFile);
		AudioHeader header = audioFile.getAudioHeader();

		String mimeType = getMimeType(header);

		if (mimeType == null) {
			throw new Exception("Unsupported file format '" + header.getFormat() + "'.");
		}

		Tag tag = audioFile.getTag();

		if (aCommand.getWriteDiscNumber()) {
			setOrDeleteTagField(tag, FieldKey.DISC_NO, aCommand.getDiscNumber());
		}
		if (aCommand.getWriteDiscCount()) {
			setOrDeleteTagField(tag, FieldKey.DISC_TOTAL, aCommand.getDiscCount());
		}

		if (aCommand.getWriteTrackNumber()) {
			setOrDeleteTagField(tag, FieldKey.TRACK, aCommand.getTrackNumber());
		}
		if (aCommand.getWriteTrackCount()) {
			setOrDeleteTagField(tag, FieldKey.TRACK_TOTAL, aCommand.getTrackCount());
		}

		if (aCommand.getWriteTitle()) {
			setOrDeleteTagField(tag, FieldKey.TITLE, aCommand.getTitle());
		}
		if (aCommand.getWriteAlbum()) {
			setOrDeleteTagField(tag, FieldKey.ALBUM, aCommand.getAlbum());
		}
		if (aCommand.getWriteYear()) {
			setOrDeleteTagField(tag, FieldKey.YEAR, aCommand.getYear());
		}

		if (aCommand.getWriteArtist()) {
			setOrDeleteTagField(tag, FieldKey.ARTIST, aCommand.getArtist());
		}
		if (aCommand.getWriteAlbumArtist()) {
			setOrDeleteTagField(tag, FieldKey.ALBUM_ARTIST, aCommand.getAlbumArtist());
		}

		if (aCommand.getWriteGenre()) {
			setOrDeleteTagField(tag, FieldKey.GENRE, aCommand.getGenre());
		}

		if (aCommand.getWriteArtwork()) {

			tag.deleteArtworkField();

			if (aCommand.getArtwork() != null) {
				tag.setField(Artwork.createArtworkFromFile(aCommand.getArtwork()));
			}
		}

		AudioFileIO.write(audioFile);

		return readSongData(audioFile);
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

	private SongDataReadable readSongData(AudioFile aAudioFile) throws Exception {

		AudioHeader header = aAudioFile.getAudioHeader();
		Tag tag = aAudioFile.getTag();

		String mimeType = getMimeType(header);

		if (mimeType == null) {
			throw new Exception("Unsupported file format '" + header.getFormat() + "'.");
		}

		SongDataReadable songData = new SongDataReadable();

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

	private void readTagToSongData(Tag aTag, SongDataReadable aSongData) {

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

			SongDataReadable.Artwork songDataArtwork = new SongDataReadable.Artwork();

			songDataArtwork.setBinaryData(artwork.getBinaryData());
			songDataArtwork.setChecksum(checksumService.calculateChecksum(artwork.getBinaryData()));
			songDataArtwork.setMimeType(artwork.getMimeType());

			aSongData.setArtwork(songDataArtwork);
		}
	}

}
