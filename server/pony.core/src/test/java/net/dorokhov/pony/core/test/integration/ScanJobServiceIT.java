package net.dorokhov.pony.core.test.integration;

import net.dorokhov.pony.core.config.ConfigService;
import net.dorokhov.pony.core.domain.ScanJob;
import net.dorokhov.pony.core.domain.ScanType;
import net.dorokhov.pony.core.library.ScanJobService;
import net.dorokhov.pony.core.library.exception.LibraryNotDefinedException;
import net.dorokhov.pony.core.test.AbstractIntegrationCase;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.util.Arrays;

public class ScanJobServiceIT extends AbstractIntegrationCase {

	private static final String TEST_FOLDER_PATH = "data/library";

	private final Object lock = new Object();

	private ScanJobService scanJobService;

	private ConfigService configService;

	private ScanJobService.Delegate delegate;

	private int creationCallCounter;
	private int updateCallCounter;

	@Before
	public void setUp() throws Exception {

		scanJobService = context.getBean(ScanJobService.class);
		configService = context.getBean(ConfigService.class);

		delegate = new ScanJobService.Delegate() {
			@Override
			public void onJobCreation(ScanJob aJob) {
				creationCallCounter++;
			}

			@Override
			public void onJobUpdate(ScanJob aJob) {

				updateCallCounter++;

				if (aJob.getStatus() == ScanJob.Status.COMPLETE || aJob.getStatus() == ScanJob.Status.FAILED) {
					synchronized (lock) {
						lock.notifyAll();
					}
				}
			}
		};
		scanJobService.addDelegate(delegate);

		resetCounters();
	}

	@After
	public void tearDown() {
		scanJobService.removeDelegate(delegate);
	}

	private void resetCounters() {
		creationCallCounter = 0;
		updateCallCounter = 0;
	}

	@Test
	public void testNotDefinedLibraryScan() throws Exception {

		boolean isExceptionThrown;

		isExceptionThrown = false;
		try {
			scanJobService.startScanJob();
		} catch (LibraryNotDefinedException e) {
			isExceptionThrown = true;
		}

		Assert.assertTrue(isExceptionThrown);
	}

	@Test
	public void testSuccessfulScanJob() throws Exception {

		configService.saveLibraryFolders(Arrays.asList(new ClassPathResource(TEST_FOLDER_PATH).getFile()));

		ScanJob job;

		job = scanJobService.startScanJob();

		Assert.assertEquals(1, creationCallCounter);
		Assert.assertEquals(0, updateCallCounter);

		Assert.assertNotNull(job.getCreationDate());
		Assert.assertNull(job.getUpdateDate());
		Assert.assertEquals(ScanType.FULL, job.getScanType());
		Assert.assertEquals(ScanJob.Status.STARTING, job.getStatus());
		Assert.assertNotNull(job.getLogMessage());
		Assert.assertNull(job.getScanResult());

		synchronized (lock) {
			lock.wait();
		}

		Assert.assertEquals(1, creationCallCounter);
		Assert.assertEquals(2, updateCallCounter);

		job = scanJobService.getById(job.getId());

		Assert.assertNotNull(job.getCreationDate());
		Assert.assertNotNull(job.getUpdateDate());
		Assert.assertEquals(ScanType.FULL, job.getScanType());
		Assert.assertEquals(ScanJob.Status.COMPLETE, job.getStatus());
		Assert.assertNotNull(job.getLogMessage());
		Assert.assertNotNull(job.getScanResult());
	}

	@Test
	public void testFailedScanJob() throws Exception {

		configService.saveLibraryFolders(Arrays.asList(new File("/notExistingFile")));

		ScanJob job;

		job = scanJobService.startScanJob();

		Assert.assertEquals(1, creationCallCounter);
		Assert.assertEquals(0, updateCallCounter);

		Assert.assertNotNull(job.getCreationDate());
		Assert.assertNull(job.getUpdateDate());
		Assert.assertEquals(ScanType.FULL, job.getScanType());
		Assert.assertEquals(ScanJob.Status.STARTING, job.getStatus());
		Assert.assertNotNull(job.getLogMessage());
		Assert.assertNull(job.getScanResult());

		synchronized (lock) {
			lock.wait();
		}

		Assert.assertEquals(1, creationCallCounter);
		Assert.assertEquals(2, updateCallCounter);

		job = scanJobService.getById(job.getId());

		Assert.assertNotNull(job.getCreationDate());
		Assert.assertNotNull(job.getUpdateDate());
		Assert.assertEquals(ScanType.FULL, job.getScanType());
		Assert.assertEquals(ScanJob.Status.FAILED, job.getStatus());
		Assert.assertNotNull(job.getLogMessage());
		Assert.assertNull(job.getScanResult());
	}
}
