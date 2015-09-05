package net.dorokhov.pony.core.test.integration;

import net.dorokhov.pony.core.test.AbstractIntegrationCase;
import net.dorokhov.pony.core.test.integration.upgrade.WorkerMock_0_0_20;
import net.dorokhov.pony.core.test.integration.upgrade.WorkerMock_0_0_5;
import net.dorokhov.pony.core.test.integration.upgrade.WorkerMock_0_1_0;
import net.dorokhov.pony.core.test.integration.upgrade.WorkerMock_0_1_3;
import net.dorokhov.pony.core.upgrade.UpgradeWorker;
import net.dorokhov.pony.core.upgrade.UpgradeWorkerLookupService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

public class UpgradeWorkerLookupServiceIT extends AbstractIntegrationCase {

	private UpgradeWorkerLookupService service;

	@Before
	public void setUp() throws Exception {
		service = context.getBean(UpgradeWorkerLookupService.class);
	}

	@Test
	public void testLookup() throws Exception {

		List<UpgradeWorker> workerList = service.lookupUpgradeWorkers("net.dorokhov.pony.core.test.integration.upgrade", "0.0.4");

		Assert.assertEquals(4, workerList.size());

		Assert.assertTrue(workerList.get(0) instanceof WorkerMock_0_0_5);
		Assert.assertTrue(workerList.get(1) instanceof WorkerMock_0_0_20);
		Assert.assertTrue(workerList.get(2) instanceof WorkerMock_0_1_0);
		Assert.assertTrue(workerList.get(3) instanceof WorkerMock_0_1_3);
	}

}
