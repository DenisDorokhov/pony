package net.dorokhov.pony.core.audio.data;

import java.io.File;

public class SongDataWritable extends SongDataAbstract {

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

	public void setDiscNumber(Integer aDiscNumber) {
		super.setDiscNumber(aDiscNumber);
		setWriteDiscNumber(true);
	}

	public void setDiscCount(Integer aDiscCount) {
		super.setDiscCount(aDiscCount);
		setWriteDiscCount(true);
	}

	public void setTrackNumber(Integer aTrackNumber) {
		super.setTrackNumber(aTrackNumber);
		setWriteTrackNumber(true);
	}

	public void setTrackCount(Integer aTrackCount) {
		super.setTrackCount(aTrackCount);
		setWriteTrackCount(true);
	}

	public void setTitle(String aTitle) {
		super.setTitle(aTitle);
		setWriteTitle(true);
	}

	public void setArtist(String aArtist) {
		super.setArtist(aArtist);
		setWriteArtist(true);
	}

	public void setAlbumArtist(String aAlbumArtist) {
		super.setAlbumArtist(aAlbumArtist);
		setWriteAlbumArtist(true);
	}

	public void setAlbum(String aAlbum) {
		super.setAlbum(aAlbum);
		setWriteAlbum(true);
	}

	public void setYear(Integer aYear) {
		super.setYear(aYear);
		setWriteYear(true);
	}

	public void setGenre(String aGenre) {
		super.setGenre(aGenre);
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

	@Override
	public String toString() {
		return "SongDataWritable{" +
				"discNumber=" + getDiscNumber() +
				", discCount=" + getDiscCount() +
				", trackNumber=" + getTrackNumber() +
				", trackCount=" + getTrackCount() +
				", title='" + getTitle() + '\'' +
				", artist='" + getArtist() + '\'' +
				", albumArtist='" + getAlbumArtist() + '\'' +
				", album='" + getAlbum() + '\'' +
				", year=" + getYear() +
				", genre='" + getGenre() + '\'' +
				", artwork='" + (artwork != null ? artwork.getAbsolutePath() : null) + "'" +
				'}';
	}

}
