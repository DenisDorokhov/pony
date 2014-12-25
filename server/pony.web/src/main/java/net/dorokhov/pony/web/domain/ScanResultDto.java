package net.dorokhov.pony.web.domain;

import net.dorokhov.pony.core.domain.ScanResult;
import net.dorokhov.pony.core.domain.ScanType;

import java.util.Date;
import java.util.List;

public class ScanResultDto {

	private Long id;

	private Date date;

	private ScanType scanType;

	private List<String> targetPaths;
	private List<String> failedPaths;

	private Long duration;

	private Long songSize;
	private Long artworkSize;

	private Long genreCount;
	private Long artistCount;
	private Long albumCount;
	private Long songCount;
	private Long artworkCount;

	private Long processedSongCount;

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

	public Long getId() {
		return id;
	}

	public void setId(Long aId) {
		id = aId;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date aDate) {
		date = aDate;
	}

	public ScanType getScanType() {
		return scanType;
	}

	public void setScanType(ScanType aScanType) {
		scanType = aScanType;
	}

	public List<String> getTargetPaths() {
		return targetPaths;
	}

	public void setTargetPaths(List<String> aTargetPaths) {
		targetPaths = aTargetPaths;
	}

	public List<String> getFailedPaths() {
		return failedPaths;
	}

	public void setFailedPaths(List<String> aFailedPaths) {
		failedPaths = aFailedPaths;
	}

	public Long getDuration() {
		return duration;
	}

	public void setDuration(Long aDuration) {
		duration = aDuration;
	}

	public Long getSongSize() {
		return songSize;
	}

	public void setSongSize(Long aSongSize) {
		songSize = aSongSize;
	}

	public Long getArtworkSize() {
		return artworkSize;
	}

	public void setArtworkSize(Long aArtworkSize) {
		artworkSize = aArtworkSize;
	}

	public Long getGenreCount() {
		return genreCount;
	}

	public void setGenreCount(Long aGenreCount) {
		genreCount = aGenreCount;
	}

	public Long getArtistCount() {
		return artistCount;
	}

	public void setArtistCount(Long aArtistCount) {
		artistCount = aArtistCount;
	}

	public Long getAlbumCount() {
		return albumCount;
	}

	public void setAlbumCount(Long aAlbumCount) {
		albumCount = aAlbumCount;
	}

	public Long getSongCount() {
		return songCount;
	}

	public void setSongCount(Long aSongCount) {
		songCount = aSongCount;
	}

	public Long getArtworkCount() {
		return artworkCount;
	}

	public void setArtworkCount(Long aArtworkCount) {
		artworkCount = aArtworkCount;
	}

	public Long getProcessedSongCount() {
		return processedSongCount;
	}

	public void setProcessedSongCount(Long aProcessedSongCount) {
		processedSongCount = aProcessedSongCount;
	}

	public Long getCreatedArtistCount() {
		return createdArtistCount;
	}

	public void setCreatedArtistCount(Long aCreatedArtistCount) {
		createdArtistCount = aCreatedArtistCount;
	}

	public Long getUpdatedArtistCount() {
		return updatedArtistCount;
	}

	public void setUpdatedArtistCount(Long aUpdatedArtistCount) {
		updatedArtistCount = aUpdatedArtistCount;
	}

	public Long getDeletedArtistCount() {
		return deletedArtistCount;
	}

	public void setDeletedArtistCount(Long aDeletedArtistCount) {
		deletedArtistCount = aDeletedArtistCount;
	}

	public Long getCreatedAlbumCount() {
		return createdAlbumCount;
	}

	public void setCreatedAlbumCount(Long aCreatedAlbumCount) {
		createdAlbumCount = aCreatedAlbumCount;
	}

	public Long getUpdatedAlbumCount() {
		return updatedAlbumCount;
	}

	public void setUpdatedAlbumCount(Long aUpdatedAlbumCount) {
		updatedAlbumCount = aUpdatedAlbumCount;
	}

	public Long getDeletedAlbumCount() {
		return deletedAlbumCount;
	}

	public void setDeletedAlbumCount(Long aDeletedAlbumCount) {
		deletedAlbumCount = aDeletedAlbumCount;
	}

	public Long getCreatedGenreCount() {
		return createdGenreCount;
	}

	public void setCreatedGenreCount(Long aCreatedGenreCount) {
		createdGenreCount = aCreatedGenreCount;
	}

	public Long getUpdatedGenreCount() {
		return updatedGenreCount;
	}

	public void setUpdatedGenreCount(Long aUpdatedGenreCount) {
		updatedGenreCount = aUpdatedGenreCount;
	}

	public Long getDeletedGenreCount() {
		return deletedGenreCount;
	}

	public void setDeletedGenreCount(Long aDeletedGenreCount) {
		deletedGenreCount = aDeletedGenreCount;
	}

	public Long getCreatedSongCount() {
		return createdSongCount;
	}

	public void setCreatedSongCount(Long aCreatedSongCount) {
		createdSongCount = aCreatedSongCount;
	}

	public Long getUpdatedSongCount() {
		return updatedSongCount;
	}

	public void setUpdatedSongCount(Long aUpdatedSongCount) {
		updatedSongCount = aUpdatedSongCount;
	}

	public Long getDeletedSongCount() {
		return deletedSongCount;
	}

	public void setDeletedSongCount(Long aDeletedSongCount) {
		deletedSongCount = aDeletedSongCount;
	}

	public Long getCreatedArtworkCount() {
		return createdArtworkCount;
	}

	public void setCreatedArtworkCount(Long aCreatedArtworkCount) {
		createdArtworkCount = aCreatedArtworkCount;
	}

	public Long getDeletedArtworkCount() {
		return deletedArtworkCount;
	}

	public void setDeletedArtworkCount(Long aDeletedArtworkCount) {
		deletedArtworkCount = aDeletedArtworkCount;
	}

	public static ScanResultDto valueOf(ScanResult aScanResult) {

		ScanResultDto dto = new ScanResultDto();

		dto.setId(aScanResult.getId());

		dto.setDate(aScanResult.getDate());
		dto.setScanType(aScanResult.getScanType());

		dto.setTargetPaths(aScanResult.getTargetPaths());
		dto.setFailedPaths(aScanResult.getFailedPaths());

		dto.setDuration(aScanResult.getDuration());

		dto.setSongSize(aScanResult.getSongSize());
		dto.setArtworkSize(aScanResult.getArtworkSize());

		dto.setGenreCount(aScanResult.getGenreCount());
		dto.setArtistCount(aScanResult.getArtistCount());
		dto.setAlbumCount(aScanResult.getAlbumCount());
		dto.setSongCount(aScanResult.getSongCount());
		dto.setArtworkCount(aScanResult.getArtworkCount());

		dto.setProcessedSongCount(aScanResult.getProcessedSongCount());

		dto.setCreatedArtistCount(aScanResult.getCreatedArtistCount());
		dto.setUpdatedArtistCount(aScanResult.getUpdatedArtistCount());
		dto.setDeletedArtistCount(aScanResult.getDeletedArtistCount());

		dto.setCreatedAlbumCount(aScanResult.getCreatedAlbumCount());
		dto.setUpdatedAlbumCount(aScanResult.getUpdatedAlbumCount());
		dto.setDeletedAlbumCount(aScanResult.getDeletedAlbumCount());

		dto.setCreatedGenreCount(aScanResult.getCreatedGenreCount());
		dto.setUpdatedGenreCount(aScanResult.getUpdatedGenreCount());
		dto.setDeletedGenreCount(aScanResult.getDeletedGenreCount());

		dto.setCreatedSongCount(aScanResult.getCreatedSongCount());
		dto.setUpdatedSongCount(aScanResult.getUpdatedSongCount());
		dto.setDeletedSongCount(aScanResult.getDeletedSongCount());

		dto.setCreatedArtworkCount(aScanResult.getCreatedArtworkCount());
		dto.setDeletedArtworkCount(aScanResult.getDeletedArtworkCount());

		return dto;
	}

}
