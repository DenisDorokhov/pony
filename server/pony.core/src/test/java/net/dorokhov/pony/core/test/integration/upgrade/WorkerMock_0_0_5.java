package net.dorokhov.pony.core.test.integration.upgrade;

import net.dorokhov.pony.core.upgrade.UpgradeWorker;
import org.springframework.stereotype.Service;

@Service
@UpgradeWorker.Version("0.0.5")
public class WorkerMock_0_0_5  implements UpgradeWorker {

	@Override
	public void performUpgrade() {}

}
