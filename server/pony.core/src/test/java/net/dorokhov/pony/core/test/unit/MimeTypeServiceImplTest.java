package net.dorokhov.pony.core.test.unit;

import net.dorokhov.pony.core.service.MimeTypeServiceImpl;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

public class MimeTypeServiceImplTest {

	private MimeTypeServiceImpl service;

	@Before
	public void setUp() {
		service = new MimeTypeServiceImpl();
	}

	@Test
	public void test() {
		Assert.assertEquals("image/png", service.getFileMimeType(new File("foobar.png")));
		Assert.assertEquals("png", service.getFileExtension("image/png"));
		Assert.assertNull(service.getFileExtension("image"));
	}

}
