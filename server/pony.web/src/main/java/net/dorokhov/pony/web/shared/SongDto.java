package net.dorokhov.pony.web.shared;

import net.dorokhov.pony.web.client.util.ObjectUtils;

public class SongDto extends AbstractDto<Long> implements Comparable<SongDto> {

	private String url;

	private Integer duration;

	private Integer discNumber;

	private Integer trackNumber;

	private String name;

	private Long artwork;

	private String artworkUrl;

	private Long genre;

	private String genreName;

	private Long artist;

	private String artistName;

	private Long album;

	private String albumName;

	private String albumArtistName;

	private Integer albumYear;

	public String getUrl() {
		return url;
	}

	public void setUrl(String aUrl) {
		url = aUrl;
	}

	public Integer getDuration() {
		return duration;
	}

	public void setDuration(Integer aDuration) {
		duration = aDuration;
	}

	public Integer getDiscNumber() {
		return discNumber;
	}

	public void setDiscNumber(Integer aDiscNumber) {
		discNumber = aDiscNumber;
	}

	public Integer getTrackNumber() {
		return trackNumber;
	}

	public void setTrackNumber(Integer aTrackNumber) {
		trackNumber = aTrackNumber;
	}

	public String getName() {
		return name;
	}

	public void setName(String aName) {
		name = aName;
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

	public Long getGenre() {
		return genre;
	}

	public void setGenre(Long aGenre) {
		genre = aGenre;
	}

	public String getGenreName() {
		return genreName;
	}

	public void setGenreName(String aGenreName) {
		genreName = aGenreName;
	}

	public Long getArtist() {
		return artist;
	}

	public void setArtist(Long aArtist) {
		artist = aArtist;
	}

	public String getArtistName() {
		return artistName;
	}

	public void setArtistName(String aArtistName) {
		artistName = aArtistName;
	}

	public Long getAlbum() {
		return album;
	}

	public void setAlbum(Long aAlbum) {
		album = aAlbum;
	}

	public String getAlbumName() {
		return albumName;
	}

	public void setAlbumName(String aAlbumName) {
		albumName = aAlbumName;
	}

	public String getAlbumArtistName() {
		return albumArtistName;
	}

	public void setAlbumArtistName(String aAlbumArtistName) {
		albumArtistName = aAlbumArtistName;
	}

	public Integer getAlbumYear() {
		return albumYear;
	}

	public void setAlbumYear(Integer aAlbumYear) {
		albumYear = aAlbumYear;
	}

	@Override
	public String toString() {
		return "SongDto{" +
				"id=" + getId() +
				", artistName='" + artistName + '\'' +
				", albumName='" + albumName + '\'' +
				", name='" + name + '\'' +
				'}';
	}

	@Override
	@SuppressWarnings("NullableProblems")
	public int compareTo(SongDto aSong) {

		int result = 0;

		if (!equals(aSong)) {

			result = ObjectUtils.compare(getArtistName(), aSong.getArtistName());

			if (result == 0) {
				result = ObjectUtils.compare(getAlbumYear(), aSong.getAlbumYear());
			}
			if (result == 0) {
				result = ObjectUtils.compare(getAlbumName(), aSong.getAlbumName());
			}

			if (result == 0) {

				Integer discNumber1 = getDiscNumber() != null ? getDiscNumber() : 1;
				Integer discNumber2 = aSong.getDiscNumber() != null ? aSong.getDiscNumber() : 1;

				result = ObjectUtils.compare(discNumber1, discNumber2);
			}
			if (result == 0) {

				Integer trackNumber1 = getTrackNumber() != null ? getTrackNumber() : 1;
				Integer trackNumber2 = aSong.getTrackNumber() != null ? aSong.getTrackNumber() : 1;

				result = ObjectUtils.compare(trackNumber1, trackNumber2);
			}
			if (result == 0) {
				result = ObjectUtils.compare(getName(), aSong.getName());
			}
		}

		return result;
	}
}
