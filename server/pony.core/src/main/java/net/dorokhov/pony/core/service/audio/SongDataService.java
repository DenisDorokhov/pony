package net.dorokhov.pony.core.service.audio;

import java.io.File;

public interface SongDataService {

	public SongData read(File aFile) throws Exception;

	public SongData write(SongDataWriteCommand aCommand) throws Exception;

}
