package net.dorokhov.pony.core.library;

import net.dorokhov.pony.core.audio.data.SongDataWritable;

public class ScanEditCommand {

	private Long songId;

	private SongDataWritable songData;

	public ScanEditCommand() {
		this(null, null);
	}

	public ScanEditCommand(Long aSongId, SongDataWritable aSongData) {
		songId = aSongId;
		songData = aSongData;
	}

	public Long getSongId() {
		return songId;
	}

	public void setSongId(Long aSongId) {
		songId = aSongId;
	}

	public SongDataWritable getSongData() {
		return songData;
	}

	public void setSongData(SongDataWritable aSongData) {
		songData = aSongData;
	}
}
