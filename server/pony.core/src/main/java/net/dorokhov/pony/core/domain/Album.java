package net.dorokhov.pony.core.domain;

import net.dorokhov.pony.core.domain.common.BaseEntity;
import net.dorokhov.pony.core.service.SearchAnalyzer;
import org.apache.commons.lang.ObjectUtils;
import org.hibernate.search.annotations.Analyzer;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "album")
@Indexed
public class Album extends BaseEntity<Long> implements Comparable<Album> {

	private String name;

	private Integer year;

	private StoredFile artwork;

	private List<Song> songs;

	private Artist artist;

	@Column(name = "name")
	@Field(analyzer = @Analyzer(impl = SearchAnalyzer.class))
	public String getName() {
		return name;
	}

	public void setName(String aName) {
		name = aName;
	}

	@Column(name = "year")
	public Integer getYear() {
		return year;
	}

	public void setYear(Integer aYear) {
		year = aYear;
	}

	@OneToOne(optional = true, fetch = FetchType.LAZY)
	@JoinColumn(name = "artwork_stored_file_id")
	public StoredFile getArtwork() {
		return artwork;
	}

	public void setArtwork(StoredFile aArtwork) {
		artwork = aArtwork;
	}

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "album")
	public List<Song> getSongs() {

		if (songs == null) {
			songs = new ArrayList<>();
		}

		return songs;
	}

	public void setSongs(List<Song> aSongs) {
		songs = aSongs;
	}

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "artist_id", nullable = false)
	public Artist getArtist() {
		return artist;
	}

	public void setArtist(Artist aArtist) {
		artist = aArtist;
	}

	@Override
	@SuppressWarnings("NullableProblems")
	public int compareTo(Album aAlbum) {

		int result = 0;

		if (!equals(aAlbum)) {

			result = ObjectUtils.compare(getArtist(), aAlbum.getArtist());

			if (result == 0) {
				result = ObjectUtils.compare(getYear(), aAlbum.getYear());
			}
			if (result == 0) {
				result = ObjectUtils.compare(getName(), aAlbum.getName());
			}
		}

		return result;
	}

	@Override
	public String toString() {
		return "Album{" +
				"id=" + getId() +
				", artist='" + artist + '\'' +
				", name='" + name + '\'' +
				'}';
	}
}
