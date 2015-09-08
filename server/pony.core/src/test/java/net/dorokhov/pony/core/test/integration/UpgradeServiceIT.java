package net.dorokhov.pony.core.test.integration;

import net.dorokhov.pony.core.test.AbstractIntegrationCase;
import net.dorokhov.pony.core.test.integration.upgrade.WorkerMock_999_0_20;
import net.dorokhov.pony.core.test.integration.upgrade.WorkerMock_999_0_5;
import net.dorokhov.pony.core.test.integration.upgrade.WorkerMock_999_1_0;
import net.dorokhov.pony.core.test.integration.upgrade.WorkerMock_999_1_3;
import net.dorokhov.pony.core.upgrade.UpgradeService;
import net.dorokhov.pony.core.upgrade.UpgradeWorker;
import net.dorokhov.pony.core.upgrade.UpgradeWorkerLookupService;
import net.dorokhov.pony.core.upgrade.exception.UpgradeInvalidException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

public class UpgradeServiceIT extends AbstractIntegrationCase {

	private UpgradeService upgradeService;

	private UpgradeWorkerLookupService upgradeWorkerLookupService;

	@Before
	public void setUp() throws Exception {
		upgradeService = context.getBean(UpgradeService.class);
		upgradeWorkerLookupService = context.getBean(UpgradeWorkerLookupService.class);
	}

	@Test
	public void testUpgradeWorkerLookup() throws Exception {

		List<UpgradeWorker> workerList;

		workerList = upgradeWorkerLookupService.lookupUpgradeWorkers("999.0.5", "999.1.3");

		Assert.assertEquals(4, workerList.size());

		Assert.assertEquals("999.0.5", workerList.get(0).getVersion());
		Assert.assertEquals("999.0.20", workerList.get(1).getVersion());
		Assert.assertEquals("999.1.0", workerList.get(2).getVersion());
		Assert.assertEquals("999.1.3", workerList.get(3).getVersion());

		workerList = upgradeWorkerLookupService.lookupUpgradeWorkers("999.0.4", "1000.0.0-SNAPSHOT");

		Assert.assertEquals(4, workerList.size());

		Assert.assertEquals("999.0.5", workerList.get(0).getVersion());
		Assert.assertEquals("999.0.20", workerList.get(1).getVersion());
		Assert.assertEquals("999.1.0", workerList.get(2).getVersion());
		Assert.assertEquals("999.1.3", workerList.get(3).getVersion());

		workerList = upgradeWorkerLookupService.lookupUpgradeWorkers("1000.0.0-SNAPSHOT", "999.1.4");

		Assert.assertEquals(0, workerList.size());

		boolean isExceptionThrown;

		isExceptionThrown = false;

		try {
			upgradeWorkerLookupService.lookupUpgradeWorkers("1000.0.0-SNAPSHOT", "999.1.3");
		} catch (UpgradeInvalidException e) {
			isExceptionThrown = true;
		}

		Assert.assertTrue(isExceptionThrown);

		isExceptionThrown = false;

		try {
			upgradeWorkerLookupService.lookupUpgradeWorkers("999.1.3", "999.1.0");
		} catch (UpgradeInvalidException e) {
			isExceptionThrown = true;
		}

		Assert.assertTrue(isExceptionThrown);
	}

	@Test
	public void testUpgrade() throws Exception {

		upgradeService.upgrade("1000.0.0-SNAPSHOT");

		Assert.assertEquals("1000.0.0-SNAPSHOT", installationService.getInstallation().getVersion());
	}

}
