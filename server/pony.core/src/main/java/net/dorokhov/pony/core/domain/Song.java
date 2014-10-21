package net.dorokhov.pony.core.domain;

import net.dorokhov.pony.core.domain.common.BaseEntity;
import net.dorokhov.pony.core.service.search.SearchAnalyzer;
import org.apache.commons.lang.ObjectUtils;
import org.hibernate.search.annotations.Analyzer;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "song")
@Indexed
public class Song extends BaseEntity<Long> implements Comparable<Song> {

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

	private String genreName;

	private String artistName;

	private String albumArtistName;

	private String albumName;

	private Integer year;

	private StoredFile artwork;

	private Album album;

	private Genre genre;

	@Column(name = "path", nullable = false, unique = true)
	@NotNull
	public String getPath() {
		return path;
	}

	public void setPath(String aPath) {
		path = aPath;
	}

	@Column(name = "format", nullable = false)
	@NotNull
	public String getFormat() {
		return format;
	}

	public void setFormat(String aType) {
		format = aType;
	}

	@Column(name = "mime_type", nullable = false)
	@NotNull
	public String getMimeType() {
		return mimeType;
	}

	public void setMimeType(String aMimeType) {
		mimeType = aMimeType;
	}

	@Column(name = "size", nullable = false)
	@NotNull
	public Long getSize() {
		return size;
	}

	public void setSize(Long aSize) {
		size = aSize;
	}

	@Column(name = "duration", nullable = false)
	@NotNull
	public Integer getDuration() {
		return duration;
	}

	public void setDuration(Integer aDuration) {
		duration = aDuration;
	}

	@Column(name = "bit_rate", nullable = false)
	@NotNull
	public Long getBitRate() {
		return bitRate;
	}

	public void setBitRate(Long aBitRate) {
		bitRate = aBitRate;
	}

	@Column(name = "disc_number")
	public Integer getDiscNumber() {
		return discNumber;
	}

	public void setDiscNumber(Integer aDiscNumber) {
		discNumber = aDiscNumber;
	}

	@Column(name = "disc_count")
	public Integer getDiscCount() {
		return discCount;
	}

	public void setDiscCount(Integer aDiscCount) {
		discCount = aDiscCount;
	}

	@Column(name = "track_number")
	public Integer getTrackNumber() {
		return trackNumber;
	}

	public void setTrackNumber(Integer aTrackNumber) {
		trackNumber = aTrackNumber;
	}

	@Column(name = "track_count")
	public Integer getTrackCount() {
		return trackCount;
	}

	public void setTrackCount(Integer aTrackCount) {
		trackCount = aTrackCount;
	}

	@Column(name = "name")
	@Field(analyzer = @Analyzer(impl = SearchAnalyzer.class))
	public String getName() {
		return name;
	}

	public void setName(String aName) {
		name = aName;
	}

	@Column(name = "genre_name")
	public String getGenreName() {
		return genreName;
	}

	public void setGenreName(String aGenreName) {
		genreName = aGenreName;
	}

	@Column(name = "artist_name")
	public String getArtistName() {
		return artistName;
	}

	public void setArtistName(String aArtist) {
		artistName = aArtist;
	}

	@Column(name = "album_artist_name")
	public String getAlbumArtistName() {
		return albumArtistName;
	}

	public void setAlbumArtistName(String aAlbumArtist) {
		albumArtistName = aAlbumArtist;
	}

	@Column(name = "album_name")
	public String getAlbumName() {
		return albumName;
	}

	public void setAlbumName(String aAlbum) {
		albumName = aAlbum;
	}

	@Column(name = "year")
	public Integer getYear() {
		return year;
	}

	public void setYear(Integer aYear) {
		year = aYear;
	}

	@ManyToOne(optional = true, fetch = FetchType.LAZY)
	@JoinColumn(name = "artwork_stored_file_id")
	public StoredFile getArtwork() {
		return artwork;
	}

	public void setArtwork(StoredFile aArtwork) {
		artwork = aArtwork;
	}

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "album_id", nullable = false)
	@NotNull
	public Album getAlbum() {
		return album;
	}

	public void setAlbum(Album aAlbum) {
		album = aAlbum;
	}

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "genre_id", nullable = false)
	@NotNull
	public Genre getGenre() {
		return genre;
	}

	public void setGenre(Genre aGenre) {
		genre = aGenre;
	}

	@Override
	public String toString() {
		return "Song{" +
				"id=" + getId() +
				", album='" + album + '\'' +
				", name='" + name + '\'' +
				", path='" + path + '\'' +
				'}';
	}

	@Override
	@SuppressWarnings("NullableProblems")
	public int compareTo(Song aSong) {

		int result = 0;

		if (!equals(aSong)) {

			result = ObjectUtils.compare(getAlbum(), aSong.getAlbum());

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
