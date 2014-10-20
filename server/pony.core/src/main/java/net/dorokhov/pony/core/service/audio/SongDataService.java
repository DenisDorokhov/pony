package net.dorokhov.pony.core.service.audio;

import java.io.File;

public interface SongDataService {

	public SongDataReadable read(File aFile) throws Exception;

	public SongDataReadable write(File aFile, SongDataWritable aSongData) throws Exception;

}
