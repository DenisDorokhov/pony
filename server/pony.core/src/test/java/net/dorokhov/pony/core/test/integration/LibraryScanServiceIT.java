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
				log.info("library scanner step [{}] ({} / {}) did progress {}%",
						aStatus.getStepCode(), aStatus.getStep(), aStatus.getTotalSteps(),
						progressFormatter.format(aStatus.getProgress() * 100.0));
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

		ScanResult scanResult = service.scan(filesToScan);

		Assert.assertTrue(didCallStart);
		Assert.assertTrue(didCallFinish);
		Assert.assertFalse(didCallFail);

		Assert.assertNotNull(scanResult.getId());
		Assert.assertNotNull(scanResult.getDate());

		Assert.assertEquals(1, scanResult.getFolders().size());
		Assert.assertEquals(filesToScan.get(0).getAbsolutePath(), scanResult.getFolders().get(0));

		Assert.assertTrue(scanResult.getDuration() > 0);

		Assert.assertEquals(Long.valueOf(235942), scanResult.getSongSize());
		Assert.assertEquals(Long.valueOf(0), scanResult.getArtworkSize());

		Assert.assertEquals(Long.valueOf(1), scanResult.getGenreCount());
		Assert.assertEquals(Long.valueOf(3), scanResult.getArtistCount());
		Assert.assertEquals(Long.valueOf(5), scanResult.getAlbumCount());
		Assert.assertEquals(Long.valueOf(14), scanResult.getSongCount());
		Assert.assertEquals(Long.valueOf(0), scanResult.getArtworkCount());

		Assert.assertEquals(Long.valueOf(14), scanResult.getFoundSongCount());

		Assert.assertEquals(Long.valueOf(3), scanResult.getCreatedArtistCount());
		Assert.assertEquals(Long.valueOf(0), scanResult.getUpdatedArtistCount());
		Assert.assertEquals(Long.valueOf(0), scanResult.getDeletedArtistCount());

		Assert.assertEquals(Long.valueOf(5), scanResult.getCreatedAlbumCount());
		Assert.assertEquals(Long.valueOf(0), scanResult.getUpdatedAlbumCount());
		Assert.assertEquals(Long.valueOf(0), scanResult.getDeletedAlbumCount());

		Assert.assertEquals(Long.valueOf(1), scanResult.getCreatedGenreCount());
		Assert.assertEquals(Long.valueOf(0), scanResult.getUpdatedGenreCount());
		Assert.assertEquals(Long.valueOf(0), scanResult.getDeletedGenreCount());

		Assert.assertEquals(Long.valueOf(14), scanResult.getCreatedSongCount());
		Assert.assertEquals(Long.valueOf(0), scanResult.getUpdatedSongCount());
		Assert.assertEquals(Long.valueOf(0), scanResult.getDeletedSongCount());

		Assert.assertEquals(Long.valueOf(0), scanResult.getCreatedArtworkCount());
		Assert.assertEquals(Long.valueOf(0), scanResult.getDeletedArtworkCount());

		didCallStart = false;
		didCallFinish = false;
		didCallFail = false;

		scanResult = service.scan(filesToScan);

		Assert.assertTrue(didCallStart);
		Assert.assertTrue(didCallFinish);
		Assert.assertFalse(didCallFail);

		Assert.assertNotNull(scanResult.getId());
		Assert.assertNotNull(scanResult.getDate());

		Assert.assertEquals(1, scanResult.getFolders().size());
		Assert.assertEquals(filesToScan.get(0).getAbsolutePath(), scanResult.getFolders().get(0));

		Assert.assertTrue(scanResult.getDuration() > 0);

		Assert.assertEquals(Long.valueOf(235942), scanResult.getSongSize());
		Assert.assertEquals(Long.valueOf(0), scanResult.getArtworkSize());

		Assert.assertEquals(Long.valueOf(1), scanResult.getGenreCount());
		Assert.assertEquals(Long.valueOf(3), scanResult.getArtistCount());
		Assert.assertEquals(Long.valueOf(5), scanResult.getAlbumCount());
		Assert.assertEquals(Long.valueOf(14), scanResult.getSongCount());
		Assert.assertEquals(Long.valueOf(0), scanResult.getArtworkCount());

		Assert.assertEquals(Long.valueOf(14), scanResult.getFoundSongCount());

		Assert.assertEquals(Long.valueOf(0), scanResult.getCreatedArtistCount());
		Assert.assertEquals(Long.valueOf(0), scanResult.getUpdatedArtistCount());
		Assert.assertEquals(Long.valueOf(0), scanResult.getDeletedArtistCount());

		Assert.assertEquals(Long.valueOf(0), scanResult.getCreatedAlbumCount());
		Assert.assertEquals(Long.valueOf(0), scanResult.getUpdatedAlbumCount());
		Assert.assertEquals(Long.valueOf(0), scanResult.getDeletedAlbumCount());

		Assert.assertEquals(Long.valueOf(0), scanResult.getCreatedGenreCount());
		Assert.assertEquals(Long.valueOf(0), scanResult.getUpdatedGenreCount());
		Assert.assertEquals(Long.valueOf(0), scanResult.getDeletedGenreCount());

		Assert.assertEquals(Long.valueOf(0), scanResult.getCreatedSongCount());
		Assert.assertEquals(Long.valueOf(0), scanResult.getUpdatedSongCount());
		Assert.assertEquals(Long.valueOf(0), scanResult.getDeletedSongCount());

		Assert.assertEquals(Long.valueOf(0), scanResult.getCreatedArtworkCount());
		Assert.assertEquals(Long.valueOf(0), scanResult.getDeletedArtworkCount());

		scanResult = service.getLastResult();

		Assert.assertNotNull(scanResult);
	}
}
