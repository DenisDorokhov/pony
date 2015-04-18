package net.dorokhov.pony.core.image;

import net.coobird.thumbnailator.Thumbnails;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;

@Service
public class ThumbnailServiceImpl implements ThumbnailService {

	private int imageWidth = 100;

	private int imageHeight = 100;

	private double imageQuality = 1.0;

	public int getImageWidth() {
		return imageWidth;
	}

	public void setImageWidth(int aImageWidth) {
		imageWidth = aImageWidth;
	}

	public int getImageHeight() {
		return imageHeight;
	}

	public void setImageHeight(int aImageHeight) {
		imageHeight = aImageHeight;
	}

	@Value("${library.artworkSize}")
	public void setImageSize(String aArtworkSize) {

		String[] stringDimensions = aArtworkSize.split(",");

		if (stringDimensions.length == 2) {

			int[] dimensions = {0, 0};

			for (int i = 0; i < stringDimensions.length; i++) {
				dimensions[i] = Integer.valueOf(stringDimensions[i].trim());
			}

			imageWidth = dimensions[0];
			imageHeight = dimensions[1];

		} else {
			throw new RuntimeException("Incorrect artwork size value [" + aArtworkSize + "]");
		}
	}

	public double getImageQuality() {
		return imageQuality;
	}

	public void setImageQuality(double aImageQuality) {
		imageQuality = aImageQuality;
	}

	@Override
	public void makeThumbnail(byte[] aImage, File aOutFile) throws Exception {

		InputStream in = null;
		OutputStream out = null;

		try {

			in = new ByteArrayInputStream(aImage);
			out = new FileOutputStream(aOutFile);

			// Using toOutputStream instead of toFile to preserve original image format
			Thumbnails.of(in).size(imageWidth, imageHeight).outputQuality(getImageQuality()).toOutputStream(out);

		} finally {
			IOUtils.closeQuietly(in);
			IOUtils.closeQuietly(out);
		}
	}

	@Override
	public void makeThumbnail(File aImage, File aOutFile) throws Exception {

		InputStream in = null;
		OutputStream out = null;

		try {

			in = new FileInputStream(aImage);
			out = new FileOutputStream(aOutFile);

			// Using toOutputStream instead of toFile to preserve original image format
			Thumbnails.of(in).size(imageWidth, imageHeight).outputQuality(getImageQuality()).toOutputStream(out);

		} finally {
			IOUtils.closeQuietly(in);
			IOUtils.closeQuietly(out);
		}
	}

}
