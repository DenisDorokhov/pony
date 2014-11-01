package net.dorokhov.pony.core.test.integration;

import net.dorokhov.pony.core.entity.ScanResult;
import net.dorokhov.pony.core.library.LibraryScanService;
import net.dorokhov.pony.core.test.AbstractIntegrationCase;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.text.DecimalFormat;
import java.text.Format;
import java.util.ArrayList;
import java.util.List;

public class LibraryScanServiceIT extends AbstractIntegrationCase {

	private static final String TEST_FOLDER_PATH = "data/library";

	private final Format progressFormatter = new DecimalFormat("###.##");

	private LibraryScanService service;

	private LibraryScanService.Delegate delegate;

	private boolean didCallStart;
	private boolean didCallFinish;
	private boolean didCallFail;

	@Before
	public void setUp() throws Exception {

		didCallStart = false;
		didCallFinish = false;
		didCallFail = false;

		service = context.getBean(LibraryScanService.class);

		delegate = new LibraryScanService.Delegate() {

			@Override
			public void onScanStart(List<File> aTargetFolders) {
				didCallStart = true;
			}

			@Override
			public void onScanProgress(LibraryScanService.Status aStatus) {
				log.info("library scanner did progress {}%", progressFormatter.format(aStatus.getProgress() * 100.0));
			}

			@Override
			public void onScanFinish(ScanResult aResult) {
				didCallFinish = true;
			}

			@Override
			public void onScanFail(Throwable aThrowable) {
				didCallFail = true;
			}
		};

		service.addDelegate(delegate);
	}

	@After
	public void tearDown() throws Exception {
		service.removeDelegate(delegate);
	}

	@Test
	public void testScan() throws Exception {

		List<File> filesToScan = new ArrayList<>();

		filesToScan.add(new ClassPathResource(TEST_FOLDER_PATH).getFile());

		ScanResult result = service.scan(filesToScan);

		Assert.assertTrue(didCallStart);
		Assert.assertTrue(didCallFinish);
		Assert.assertFalse(didCallFail);

		Assert.assertNotNull(result.getId());
		Assert.assertNotNull(result.getDate());

		Assert.assertEquals(1, result.getFolders().size());
		Assert.assertEquals(filesToScan.get(0).getAbsolutePath(), result.getFolders().get(0));

		Assert.assertTrue(result.getDuration() > 0);

		Assert.assertEquals(Long.valueOf(14), result.getFoundSongCount());

		result = service.getLastResult();

		Assert.assertNotNull(result);
	}
}
