package net.dorokhov.pony.core.test.unit;

import net.dorokhov.pony.core.common.FileType;
import net.dorokhov.pony.core.service.FileTypeServiceImpl;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

public class FileTypeServiceImplTest {

	private FileTypeServiceImpl service;

	@Before
	public void setUp() {
		service = new FileTypeServiceImpl();
	}

	@Test
	public void test() {

		Assert.assertEquals("image/png", service.getFileMimeType(new File("foobar.png")));
		Assert.assertEquals("image/jpeg", service.getFileMimeType(new File("foobar.jpg")));
		Assert.assertEquals("image/jpeg", service.getFileMimeType(new File("foobar.jpeg")));
		Assert.assertEquals("audio/mpeg3", service.getFileMimeType(new File("foobar.mp3")));
		Assert.assertNull(service.getFileMimeType(new File("foobar.txt")));

		Assert.assertEquals("png", service.getFileExtension("image/png"));
		Assert.assertEquals("jpg", service.getFileExtension("image/jpeg"));
		Assert.assertEquals("mp3", service.getFileExtension("audio/mpeg3"));
		Assert.assertNull(service.getFileExtension("text/plain"));

		Assert.assertEquals(FileType.IMAGE, service.getFileType(new File("foobar.jpg")));
		Assert.assertEquals(FileType.IMAGE, service.getFileType(new File("foobar.jpeg")));
		Assert.assertEquals(FileType.IMAGE, service.getFileType(new File("foobar.png")));
		Assert.assertEquals(FileType.SONG, service.getFileType(new File("foobar.mp3")));
		Assert.assertNull(service.getFileType(new File("foobar.txt")));
	}

}
