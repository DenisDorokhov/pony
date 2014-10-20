package net.dorokhov.pony.core.service.image;

import java.io.File;

public interface ThumbnailService {

	public void makeThumbnail(byte[] aImage, File aOutFile) throws Exception;

	public void makeThumbnail(File aImage, File aOutFile) throws Exception;

}
