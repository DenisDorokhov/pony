package net.dorokhov.pony.core.audio;

import net.dorokhov.pony.core.audio.data.SongDataReadable;
import net.dorokhov.pony.core.audio.data.SongDataWritable;

import java.io.File;

public interface SongDataService {

	public SongDataReadable read(File aFile) throws Exception;

	public SongDataReadable write(File aFile, SongDataWritable aSongData) throws Exception;

}
