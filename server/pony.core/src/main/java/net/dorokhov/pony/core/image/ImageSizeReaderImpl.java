package net.dorokhov.pony.core.image;

import net.dorokhov.pony.core.utils.SimpleImageInfo;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
public class ImageSizeReaderImpl implements ImageSizeReader {

	@Override
	public ImageSize read(File aFile) throws Exception {

		SimpleImageInfo info = new SimpleImageInfo(aFile);

		final double width = info.getWidth();
		final double height = info.getHeight();

		return new ImageSize() {

			@Override
			public double getWidth() {
				return width;
			}

			@Override
			public double getHeight() {
				return height;
			}
		};
	}

}
