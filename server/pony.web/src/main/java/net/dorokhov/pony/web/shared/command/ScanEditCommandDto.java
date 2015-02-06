package net.dorokhov.pony.web.shared.command;

import java.util.ArrayList;
import java.util.List;

public class ScanEditCommandDto {

	private List<Task> tasks;

	public List<Task> getTasks() {

		if (tasks == null) {
			tasks = new ArrayList<>();
		}

		return tasks;
	}

	public void setTasks(List<Task> aTasks) {
		tasks = aTasks;
	}

	public static class Task {

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

		private Long artworkUploadId;

		private boolean writeDiscNumber;
		private boolean writeDiscCount;

		private boolean writeTrackNumber;
		private boolean writeTrackCount;

		private boolean writeTitle;
		private boolean writeArtist;
		private boolean writeAlbumArtist;
		private boolean writeAlbum;

		private boolean writeYear;

		private boolean writeGenre;

		private boolean writeArtwork;

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

		public Long getArtworkUploadId() {
			return artworkUploadId;
		}

		public void setArtworkUploadId(Long aArtworkUploadId) {
			artworkUploadId = aArtworkUploadId;
		}

		public boolean getWriteDiscNumber() {
			return writeDiscNumber;
		}

		public void setWriteDiscNumber(boolean aWriteDiscNumber) {
			writeDiscNumber = aWriteDiscNumber;
		}

		public boolean getWriteDiscCount() {
			return writeDiscCount;
		}

		public void setWriteDiscCount(boolean aWriteDiscCount) {
			writeDiscCount = aWriteDiscCount;
		}

		public boolean getWriteTrackNumber() {
			return writeTrackNumber;
		}

		public void setWriteTrackNumber(boolean aWriteTrackNumber) {
			writeTrackNumber = aWriteTrackNumber;
		}

		public boolean getWriteTrackCount() {
			return writeTrackCount;
		}

		public void setWriteTrackCount(boolean aWriteTrackCount) {
			writeTrackCount = aWriteTrackCount;
		}

		public boolean getWriteTitle() {
			return writeTitle;
		}

		public void setWriteTitle(boolean aWriteTitle) {
			writeTitle = aWriteTitle;
		}

		public boolean getWriteArtist() {
			return writeArtist;
		}

		public void setWriteArtist(boolean aWriteArtist) {
			writeArtist = aWriteArtist;
		}

		public boolean getWriteAlbumArtist() {
			return writeAlbumArtist;
		}

		public void setWriteAlbumArtist(boolean aWriteAlbumArtist) {
			writeAlbumArtist = aWriteAlbumArtist;
		}

		public boolean getWriteAlbum() {
			return writeAlbum;
		}

		public void setWriteAlbum(boolean aWriteAlbum) {
			writeAlbum = aWriteAlbum;
		}

		public boolean getWriteYear() {
			return writeYear;
		}

		public void setWriteYear(boolean aWriteYear) {
			writeYear = aWriteYear;
		}

		public boolean getWriteGenre() {
			return writeGenre;
		}

		public void setWriteGenre(boolean aWriteGenre) {
			writeGenre = aWriteGenre;
		}

		public boolean getWriteArtwork() {
			return writeArtwork;
		}

		public void setWriteArtwork(boolean aWriteArtwork) {
			writeArtwork = aWriteArtwork;
		}
	}
}
