package net.dorokhov.pony.core.test.unit;

import net.dorokhov.pony.core.service.image.ImageSize;
import net.dorokhov.pony.core.service.image.ImageSizeReaderImpl;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

public class ImageSizeReaderImplTest {

	private static final String TEST_FILE_PATH = "data/image.png"; // red picture 90x100

	private ImageSizeReaderImpl service;

	@Before
	public void setUp() {
		service = new ImageSizeReaderImpl();
	}

	@Test
	public void test() throws Exception {

		ImageSize size = service.read(new ClassPathResource(TEST_FILE_PATH).getFile());

		Assert.assertEquals(90.0, size.getWidth(), 0.01);
		Assert.assertEquals(100.0, size.getHeight(), 0.01);
	}

}
