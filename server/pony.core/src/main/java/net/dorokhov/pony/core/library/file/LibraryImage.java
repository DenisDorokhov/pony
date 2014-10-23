package net.dorokhov.pony.core.library.file;

import net.dorokhov.pony.core.image.ImageSize;

public interface LibraryImage extends LibraryFile {

	public ImageSize getSize() throws Exception;

}
