package net.dorokhov.pony.web.shared;

import net.dorokhov.pony.core.domain.Song;
import net.dorokhov.pony.core.domain.StoredFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

public class SongDataDto {

	private Long songId;

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

	private Long artwork;

	private String artworkUrl;

	private String path;

	private String format;

	private String mimeType;

	private Long size;

	private Integer duration;

	private Long bitRate;

	public Long getSongId() {
		return songId;
	}

	public void setSongId(Long aSongId) {
		songId = aSongId;
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

	public Long getArtwork() {
		return artwork;
	}

	public void setArtwork(Long aArtwork) {
		artwork = aArtwork;
	}

	public String getArtworkUrl() {
		return artworkUrl;
	}

	public void setArtworkUrl(String aArtworkUrl) {
		artworkUrl = aArtworkUrl;
	}

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

	public static SongDataDto valueOf(Song aSong) {

		SongDataDto dto = new SongDataDto();

		dto.setSongId(aSong.getId());

		dto.setDiscNumber(aSong.getDiscNumber());
		dto.setDiscCount(aSong.getDiscCount());

		dto.setTrackNumber(aSong.getTrackNumber());
		dto.setTrackCount(aSong.getTrackCount());

		dto.setTitle(aSong.getName());

		dto.setArtist(aSong.getArtistName());
		dto.setAlbumArtist(aSong.getAlbumArtistName());

		dto.setAlbum(aSong.getAlbumName());

		dto.setYear(aSong.getYear());

		dto.setGenre(aSong.getGenreName());

		StoredFile artwork = aSong.getArtwork();

		if (artwork == null) {
			artwork = aSong.getAlbum().getArtwork();
		}

		if (artwork != null) {
			dto.setArtwork(artwork.getId());
			dto.setArtworkUrl(ServletUriComponentsBuilder.fromCurrentContextPath().path("/files/" + artwork.getId()).build().toUriString());
		}

		dto.setPath(aSong.getPath());
		dto.setFormat(aSong.getFormat());
		dto.setMimeType(aSong.getMimeType());
		dto.setSize(aSong.getSize());
		dto.setDuration(aSong.getDuration());
		dto.setBitRate(aSong.getBitRate());

		return dto;
	}
}
