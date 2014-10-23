package net.dorokhov.pony.core.image;

import java.io.File;

public interface ImageSizeReader {

	public ImageSize read(File aFile) throws Exception;

}
