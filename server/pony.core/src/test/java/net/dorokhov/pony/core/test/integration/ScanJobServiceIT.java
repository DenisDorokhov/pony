package net.dorokhov.pony.core.test.integration;

import net.dorokhov.pony.core.entity.ScanJob;
import net.dorokhov.pony.core.entity.common.ScanType;
import net.dorokhov.pony.core.library.ScanJobService;
import net.dorokhov.pony.core.test.AbstractIntegrationCase;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ScanJobServiceIT extends AbstractIntegrationCase {

	private final Object lock = new Object();

	private ScanJobService service;

	private int creationCallCounter;
	private int updateCallCounter;

	@Before
	public void setUp() throws Exception {

		service = context.getBean(ScanJobService.class);
		service.addDelegate(new ScanJobService.Delegate() {
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
		});

		resetCounters();
	}

	private void resetCounters() {
		creationCallCounter = 0;
		updateCallCounter = 0;
	}

	@Test
	public void testScan() throws Exception {

		ScanJob job;

		job = service.createScanJob();

		Assert.assertEquals(1, creationCallCounter);
		Assert.assertEquals(0, updateCallCounter);

		Assert.assertNotNull(job.getCreationDate());
		Assert.assertNull(job.getUpdateDate());
		Assert.assertEquals(ScanType.FULL, job.getType());
		Assert.assertEquals(ScanJob.Status.STARTING, job.getStatus());
		Assert.assertNotNull(job.getLogMessage());
		Assert.assertNull(job.getScanResult());

		synchronized (lock) {
			lock.wait();
		}

		Assert.assertEquals(1, creationCallCounter);
		Assert.assertEquals(2, updateCallCounter);

		job = service.getById(job.getId());

		Assert.assertNotNull(job.getCreationDate());
		Assert.assertNotNull(job.getUpdateDate());
		Assert.assertEquals(ScanType.FULL, job.getType());
		Assert.assertEquals(ScanJob.Status.COMPLETE, job.getStatus());
		Assert.assertNotNull(job.getLogMessage());
		Assert.assertNotNull(job.getScanResult());
	}
}
