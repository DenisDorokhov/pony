package net.dorokhov.pony.core.service;

import net.dorokhov.pony.core.common.SongData;

import java.io.File;

public interface SongDataService {

	public SongData read(File aFile) throws Exception;

}
