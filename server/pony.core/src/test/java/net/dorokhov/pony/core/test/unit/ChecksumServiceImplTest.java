package net.dorokhov.pony.core.test.unit;

import net.dorokhov.pony.core.service.ChecksumServiceImpl;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

public class ChecksumServiceImplTest {

	private static final File TEST_FILE = new File(FileUtils.getTempDirectory(), "TestChecksumServiceImpl.tmp");
	private static final String TEST_CONTENT = "test";
	private static final String TEST_CHECKSUM = "098f6bcd4621d373cade4e832627b4f6";

	private ChecksumServiceImpl service;

	@Before
	public void setUp() {

		service = new ChecksumServiceImpl();

		TEST_FILE.delete();
	}

	@After
	public void tearDown() {
		TEST_FILE.delete();
	}

	@Test
	public void test() throws Exception {

		byte[] content = TEST_CONTENT.getBytes();

		Assert.assertEquals(TEST_CHECKSUM, service.calculateChecksum(content));

		FileUtils.write(TEST_FILE, TEST_CONTENT);

		Assert.assertEquals(TEST_CHECKSUM, service.calculateChecksum(TEST_FILE));
	}

}
