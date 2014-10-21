package net.dorokhov.pony.core.service.library.common;

import net.dorokhov.pony.core.service.image.ImageSize;

public interface LibraryImage extends LibraryFile {

	public ImageSize getSize() throws Exception;

}
