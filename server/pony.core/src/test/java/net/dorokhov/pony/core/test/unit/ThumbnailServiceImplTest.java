package net.dorokhov.pony.core.test.unit;

import net.dorokhov.pony.core.service.image.ThumbnailServiceImpl;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

public class ThumbnailServiceImplTest {

	private static final String TEST_FILE_PATH = "data/image.png"; // red picture 90x100
	private static final File TEST_TARGET_FILE = new File(FileUtils.getTempDirectory(), "ImageScalingServiceImplTest.jpg");
	private static final int TEST_TARGET_WIDTH = 50;
	private static final int TEST_TARGET_HEIGHT = 50;

	private ThumbnailServiceImpl service;

	@Before
	public void setUp() {

		service = new ThumbnailServiceImpl();

		TEST_TARGET_FILE.delete();
	}

	@After
	public void tearDown() {
		TEST_TARGET_FILE.delete();
	}

	@Test
	public void testResizing() throws Exception {

		service.setImageWidth(TEST_TARGET_WIDTH);
		service.setImageHeight(TEST_TARGET_HEIGHT);

		File sourceFile = new ClassPathResource(TEST_FILE_PATH).getFile();

		service.makeThumbnail(sourceFile, TEST_TARGET_FILE);

		checkTargetImageSize();

		service.makeThumbnail(FileUtils.readFileToByteArray(sourceFile), TEST_TARGET_FILE);

		checkTargetImageSize();
	}

	@Test
	public void testConfiguration() {

		service.setImageSize("23,45");

		Assert.assertEquals(23, service.getImageWidth());
		Assert.assertEquals(45, service.getImageHeight());

		boolean isExceptionThrown = false;

		try {
			service.setImageSize("incorrect-format");
		} catch (Exception e) {
			isExceptionThrown = true;
		}

		Assert.assertTrue(isExceptionThrown);
	}

	private void checkTargetImageSize() throws Exception{

		BufferedImage targetImage = ImageIO.read(TEST_TARGET_FILE);

		Assert.assertEquals(45, targetImage.getWidth());
		Assert.assertEquals(50, targetImage.getHeight());
	}

}
