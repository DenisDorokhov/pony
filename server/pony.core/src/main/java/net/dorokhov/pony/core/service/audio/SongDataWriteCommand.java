package net.dorokhov.pony.core.service.audio;

import java.io.File;

public class SongDataWriteCommand {

	private File file;

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

	private File artwork;

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

	public SongDataWriteCommand(File aFile) {

		if (aFile == null) {
			throw new NullPointerException("File must not be null.");
		}

		file = aFile;
	}

	public File getFile() {
		return file;
	}

	public Integer getDiscNumber() {
		return discNumber;
	}

	public void setDiscNumber(Integer aDiscNumber) {
		discNumber = aDiscNumber;
		setWriteDiscNumber(true);
	}

	public Integer getDiscCount() {
		return discCount;
	}

	public void setDiscCount(Integer aDiscCount) {
		discCount = aDiscCount;
		setWriteDiscCount(true);
	}

	public Integer getTrackNumber() {
		return trackNumber;
	}

	public void setTrackNumber(Integer aTrackNumber) {
		trackNumber = aTrackNumber;
		setWriteTrackNumber(true);
	}

	public Integer getTrackCount() {
		return trackCount;
	}

	public void setTrackCount(Integer aTrackCount) {
		trackCount = aTrackCount;
		setWriteTrackCount(true);
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String aTitle) {
		title = aTitle;
		setWriteTitle(true);
	}

	public String getArtist() {
		return artist;
	}

	public void setArtist(String aArtist) {
		artist = aArtist;
		setWriteArtist(true);
	}

	public String getAlbumArtist() {
		return albumArtist;
	}

	public void setAlbumArtist(String aAlbumArtist) {
		albumArtist = aAlbumArtist;
		setWriteAlbumArtist(true);
	}

	public String getAlbum() {
		return album;
	}

	public void setAlbum(String aAlbum) {
		album = aAlbum;
		setWriteAlbum(true);
	}

	public Integer getYear() {
		return year;
	}

	public void setYear(Integer aYear) {
		year = aYear;
		setWriteYear(true);
	}

	public String getGenre() {
		return genre;
	}

	public void setGenre(String aGenre) {
		genre = aGenre;
		setWriteGenre(true);
	}

	public File getArtwork() {
		return artwork;
	}

	public void setArtwork(File aArtwork) {
		artwork = aArtwork;
		setWriteArtwork(true);
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

	public void setWriteDiscCount(boolean aIsWriteDiscCount) {
		writeDiscCount = aIsWriteDiscCount;
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

	public void setWriteTrackCount(boolean aIsWriteTrackCount) {
		writeTrackCount = aIsWriteTrackCount;
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
