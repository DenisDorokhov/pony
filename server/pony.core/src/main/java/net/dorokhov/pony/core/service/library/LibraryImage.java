package net.dorokhov.pony.core.service.library;

import net.dorokhov.pony.core.common.ImageSize;

public interface LibraryImage extends LibraryFile {

	public ImageSize getSize() throws Exception;

}
