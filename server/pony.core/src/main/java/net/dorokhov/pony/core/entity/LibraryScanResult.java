package net.dorokhov.pony.core.entity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "library_scan_result")
public class LibraryScanResult {

	private Long id;

	private Date date;

	private Boolean success;

	private List<String> folders;

	private Long duration;

	private Long scannedSongCount;

	private Long scannedFolderCount;

	private Long createdArtistCount;

	private Long deletedArtistCount;

	private Long createdAlbumCount;

	private Long deletedAlbumCount;

	private Long createdGenreCount;

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

	@Column(name = "success")
	@NotNull
	public Boolean getSuccess() {
		return success;
	}

	public void setSuccess(Boolean aSuccess) {
		success = aSuccess;
	}

	@Column(name="path")
	@ElementCollection(fetch = FetchType.EAGER)
	@CollectionTable(name="library_scan_result_folder", joinColumns=@JoinColumn(name="library_scan_result_id"))
	public List<String> getFolders() {
		return folders;
	}

	public void setFolders(List<String> aTargetFiles) {
		folders = aTargetFiles;
	}

	@Column(name = "duration")
	@NotNull
	public Long getDuration() {
		return duration;
	}

	public void setDuration(Long aDuration) {
		duration = aDuration;
	}

	@Column(name = "scanned_song_count")
	@NotNull
	public Long getScannedSongCount() {
		return scannedSongCount;
	}

	public void setScannedSongCount(Long aScannedSongCount) {
		scannedSongCount = aScannedSongCount;
	}

	@Column(name = "scanned_folder_count")
	@NotNull
	public Long getScannedFolderCount() {
		return scannedFolderCount;
	}

	public void setScannedFolderCount(Long aScannedFolderCount) {
		scannedFolderCount = aScannedFolderCount;
	}

	@Column(name = "created_artist_count")
	@NotNull
	public Long getCreatedArtistCount() {
		return createdArtistCount;
	}

	public void setCreatedArtistCount(Long aCreatedArtistCount) {
		createdArtistCount = aCreatedArtistCount;
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

	@Column(name = "imported_artwork_count")
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

			LibraryScanResult that = (LibraryScanResult) aObj;

			return id.equals(that.id);
		}

		return false;
	}
}
