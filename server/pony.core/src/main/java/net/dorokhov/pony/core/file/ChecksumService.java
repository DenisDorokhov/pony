package net.dorokhov.pony.core.file;

import java.io.File;
import java.io.IOException;

public interface ChecksumService {

	public String calculateChecksum(File aFile) throws IOException;

	public String calculateChecksum(byte[] aData);

}
