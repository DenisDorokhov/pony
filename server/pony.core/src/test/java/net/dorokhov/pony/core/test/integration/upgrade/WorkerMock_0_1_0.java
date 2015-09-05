package net.dorokhov.pony.core.test.integration.upgrade;

import net.dorokhov.pony.core.upgrade.UpgradeWorker;
import org.springframework.stereotype.Service;

@Service
@UpgradeWorker.Version("0.1.0")
public class WorkerMock_0_1_0  implements UpgradeWorker {

	@Override
	public void performUpgrade() {}

}
