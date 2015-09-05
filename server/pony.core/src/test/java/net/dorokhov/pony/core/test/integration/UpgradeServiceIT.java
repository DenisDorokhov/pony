package net.dorokhov.pony.core.test.integration;

import net.dorokhov.pony.core.test.AbstractIntegrationCase;
import net.dorokhov.pony.core.test.integration.upgrade.WorkerMock_999_0_20;
import net.dorokhov.pony.core.test.integration.upgrade.WorkerMock_999_0_5;
import net.dorokhov.pony.core.test.integration.upgrade.WorkerMock_999_1_0;
import net.dorokhov.pony.core.test.integration.upgrade.WorkerMock_999_1_3;
import net.dorokhov.pony.core.upgrade.UpgradeService;
import net.dorokhov.pony.core.upgrade.UpgradeWorker;
import net.dorokhov.pony.core.upgrade.UpgradeWorkerLookupService;
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

		List<UpgradeWorker> workerList = upgradeWorkerLookupService.lookupUpgradeWorkers("999.0.4", "1000.0.0-SNAPSHOT");

		Assert.assertEquals(4, workerList.size());

		Assert.assertTrue(workerList.get(0) instanceof WorkerMock_999_0_5);
		Assert.assertTrue(workerList.get(1) instanceof WorkerMock_999_0_20);
		Assert.assertTrue(workerList.get(2) instanceof WorkerMock_999_1_0);
		Assert.assertTrue(workerList.get(3) instanceof WorkerMock_999_1_3);
	}

	@Test
	public void testUpgrade() throws Exception {
		upgradeService.upgrade("1000.0.0-SNAPSHOT");
	}

}
