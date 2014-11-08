package net.dorokhov.pony.core.entity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "scan_result")
public class ScanResult {

	public static enum Type {
		FULL, EDIT
	}

	private Long id;

	private Date date;

	private Type type;

	private List<String> paths;

	private Long duration;

	private Long songSize;
	private Long artworkSize;

	private Long genreCount;
	private Long artistCount;
	private Long albumCount;
	private Long songCount;
	private Long artworkCount;

	private Long foundSongCount;

	private Long createdArtistCount;
	private Long updatedArtistCount;
	private Long deletedArtistCount;

	private Long createdAlbumCount;
	private Long updatedAlbumCount;
	private Long deletedAlbumCount;

	private Long createdGenreCount;
	private Long updatedGenreCount;
	private Long deletedGenreCount;

	private Long createdSongCount;
	private Long updatedSongCount;
	private Long deletedSongCount;

	private Long createdArtworkCount;
	private Long deletedArtworkCount;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	public Long getId() {
		return id;
	}

	public void setId(Long aId) {
		id = aId;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "date")
	@NotNull
	public Date getDate() {
		return date;
	}

	public void setDate(Date aDate) {
		date = aDate;
	}

	@Column(name = "type")
	@Enumerated(EnumType.STRING)
	@NotNull
	public Type getType() {
		return type;
	}

	public void setType(Type aType) {
		type = aType;
	}

	@Column(name="value")
	@ElementCollection(fetch = FetchType.EAGER)
	@CollectionTable(name="scan_result_path", joinColumns=@JoinColumn(name="scan_result_id"))
	public List<String> getPaths() {
		return paths;
	}

	public void setPaths(List<String> aTargetFiles) {
		paths = aTargetFiles;
	}

	@Column(name = "duration")
	@NotNull
	public Long getDuration() {
		return duration;
	}

	public void setDuration(Long aDuration) {
		duration = aDuration;
	}

	@Column(name = "song_size")
	@NotNull
	public Long getSongSize() {
		return songSize;
	}

	public void setSongSize(Long aSongSize) {
		songSize = aSongSize;
	}

	@Column(name = "artwork_size")
	@NotNull
	public Long getArtworkSize() {
		return artworkSize;
	}

	public void setArtworkSize(Long aArtworkSize) {
		artworkSize = aArtworkSize;
	}

	@Column(name = "genre_count")
	@NotNull
	public Long getGenreCount() {
		return genreCount;
	}

	public void setGenreCount(Long aGenreCount) {
		genreCount = aGenreCount;
	}

	@Column(name = "artist_count")
	@NotNull
	public Long getArtistCount() {
		return artistCount;
	}

	public void setArtistCount(Long aArtistCount) {
		artistCount = aArtistCount;
	}

	@Column(name = "album_count")
	@NotNull
	public Long getAlbumCount() {
		return albumCount;
	}

	public void setAlbumCount(Long aAlbumCount) {
		albumCount = aAlbumCount;
	}

	@Column(name = "song_count")
	@NotNull
	public Long getSongCount() {
		return songCount;
	}

	public void setSongCount(Long aSongCount) {
		songCount = aSongCount;
	}

	@Column(name = "artwork_count")
	@NotNull
	public Long getArtworkCount() {
		return artworkCount;
	}

	public void setArtworkCount(Long aArtworkCount) {
		artworkCount = aArtworkCount;
	}

	@Column(name = "found_song_count")
	@NotNull
	public Long getFoundSongCount() {
		return foundSongCount;
	}

	public void setFoundSongCount(Long aScannedSongCount) {
		foundSongCount = aScannedSongCount;
	}

	@Column(name = "created_artist_count")
	@NotNull
	public Long getCreatedArtistCount() {
		return createdArtistCount;
	}

	public void setCreatedArtistCount(Long aCreatedArtistCount) {
		createdArtistCount = aCreatedArtistCount;
	}

	@Column(name = "updated_artist_count")
	@NotNull
	public Long getUpdatedArtistCount() {
		return updatedArtistCount;
	}

	public void setUpdatedArtistCount(Long aUpdatedArtistCount) {
		updatedArtistCount = aUpdatedArtistCount;
	}

	@Column(name = "deleted_artist_count")
	@NotNull
	public Long getDeletedArtistCount() {
		return deletedArtistCount;
	}

	public void setDeletedArtistCount(Long aDeletedArtistCount) {
		deletedArtistCount = aDeletedArtistCount;
	}

	@Column(name = "created_album_count")
	@NotNull
	public Long getCreatedAlbumCount() {
		return createdAlbumCount;
	}

	public void setCreatedAlbumCount(Long aCreatedAlbumCount) {
		createdAlbumCount = aCreatedAlbumCount;
	}

	@Column(name = "updated_album_count")
	@NotNull
	public Long getUpdatedAlbumCount() {
		return updatedAlbumCount;
	}

	public void setUpdatedAlbumCount(Long aUpdatedAlbumCount) {
		updatedAlbumCount = aUpdatedAlbumCount;
	}

	@Column(name = "deleted_album_count")
	@NotNull
	public Long getDeletedAlbumCount() {
		return deletedAlbumCount;
	}

	public void setDeletedAlbumCount(Long aDeletedAlbumCount) {
		deletedAlbumCount = aDeletedAlbumCount;
	}

	@Column(name = "created_genre_count")
	@NotNull
	public Long getCreatedGenreCount() {
		return createdGenreCount;
	}

	public void setCreatedGenreCount(Long aCreatedGenreCount) {
		createdGenreCount = aCreatedGenreCount;
	}

	@Column(name = "updated_genre_count")
	@NotNull
	public Long getUpdatedGenreCount() {
		return updatedGenreCount;
	}

	public void setUpdatedGenreCount(Long aUpdatedGenreCount) {
		updatedGenreCount = aUpdatedGenreCount;
	}

	@Column(name = "deleted_genre_count")
	@NotNull
	public Long getDeletedGenreCount() {
		return deletedGenreCount;
	}

	public void setDeletedGenreCount(Long aDeletedGenreCount) {
		deletedGenreCount = aDeletedGenreCount;
	}

	@Column(name = "created_song_count")
	@NotNull
	public Long getCreatedSongCount() {
		return createdSongCount;
	}

	public void setCreatedSongCount(Long aCreatedSongCount) {
		createdSongCount = aCreatedSongCount;
	}

	@Column(name = "updated_song_count")
	@NotNull
	public Long getUpdatedSongCount() {
		return updatedSongCount;
	}

	public void setUpdatedSongCount(Long aUpdatedSongCount) {
		updatedSongCount = aUpdatedSongCount;
	}

	@Column(name = "deleted_song_count")
	@NotNull
	public Long getDeletedSongCount() {
		return deletedSongCount;
	}

	public void setDeletedSongCount(Long aDeletedSongCount) {
		deletedSongCount = aDeletedSongCount;
	}

	@Column(name = "created_artwork_count")
	@NotNull
	public Long getCreatedArtworkCount() {
		return createdArtworkCount;
	}

	public void setCreatedArtworkCount(Long aImportedArtworkCount) {
		createdArtworkCount = aImportedArtworkCount;
	}

	@Column(name = "deleted_artwork_count")
	@NotNull
	public Long getDeletedArtworkCount() {
		return deletedArtworkCount;
	}

	public void setDeletedArtworkCount(Long aDeletedArtworkCount) {
		deletedArtworkCount = aDeletedArtworkCount;
	}

	@PrePersist
	public void prePersist() {
		if (getDate() == null) {
			setDate(new Date());
		}
	}

	@Override
	public int hashCode() {
		return id != null ? id.hashCode() : super.hashCode();
	}

	@Override
	public boolean equals(Object aObj) {

		if (this == aObj) {
			return true;
		}

		if (aObj != null && id != null && getClass().equals(aObj.getClass())) {

			ScanResult that = (ScanResult) aObj;

			return id.equals(that.id);
		}

		return false;
	}

	@Override
	public String toString() {
		return "ScanResult{" +
				"id=" + id +
				", date=" + date +
				", type=" + type +
				", paths=" + paths +
				", duration=" + duration +
				", songSize=" + songSize +
				", artworkSize=" + artworkSize +
				", genreCount=" + genreCount +
				", artistCount=" + artistCount +
				", albumCount=" + albumCount +
				", songCount=" + songCount +
				", artworkCount=" + artworkCount +
				", foundSongCount=" + foundSongCount +
				", createdArtistCount=" + createdArtistCount +
				", updatedArtistCount=" + updatedArtistCount +
				", deletedArtistCount=" + deletedArtistCount +
				", createdAlbumCount=" + createdAlbumCount +
				", updatedAlbumCount=" + updatedAlbumCount +
				", deletedAlbumCount=" + deletedAlbumCount +
				", createdGenreCount=" + createdGenreCount +
				", updatedGenreCount=" + updatedGenreCount +
				", deletedGenreCount=" + deletedGenreCount +
				", createdSongCount=" + createdSongCount +
				", updatedSongCount=" + updatedSongCount +
				", deletedSongCount=" + deletedSongCount +
				", createdArtworkCount=" + createdArtworkCount +
				", deletedArtworkCount=" + deletedArtworkCount +
				'}';
	}
}
