package net.dorokhov.pony.core.domain;

import net.dorokhov.pony.core.domain.common.BaseEntity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "genre")
public class Genre extends BaseEntity<Long> {

	private String name;

	private Integer songCount = 0;

	private Long songSize = 0L;

	private StoredFile artwork;

	private List<Song> songs;

	@Column(name = "name")
	public String getName() {
		return name;
	}

	public void setName(String aName) {
		name = aName;
	}

	@Column(name = "song_count", nullable = false)
	@NotNull
	public Integer getSongCount() {
		return songCount;
	}

	public void setSongCount(Integer aSongCount) {
		songCount = aSongCount;
	}

	@Column(name = "song_size", nullable = false)
	@NotNull
	public Long getSongSize() {
		return songSize;
	}

	public void setSongSize(Long aSongSize) {
		songSize = aSongSize;
	}

	@OneToOne(optional = true, fetch = FetchType.LAZY)
	@JoinColumn(name = "artwork_stored_file_id")
	public StoredFile getArtwork() {
		return artwork;
	}

	public void setArtwork(StoredFile aArtwork) {
		artwork = aArtwork;
	}

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "genre")
	public List<Song> getSongs() {

		if (songs == null) {
			songs = new ArrayList<>();
		}

		return songs;
	}

	public void setSongs(List<Song> aSongs) {
		songs = aSongs;
	}
}
