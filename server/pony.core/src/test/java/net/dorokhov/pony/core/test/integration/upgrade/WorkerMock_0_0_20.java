package net.dorokhov.pony.core.test.integration.upgrade;

import net.dorokhov.pony.core.upgrade.UpgradeWorker;
import org.springframework.stereotype.Service;

@Service
@UpgradeWorker.Version("0.0.20")
public class WorkerMock_0_0_20  implements UpgradeWorker {

	@Override
	public void performUpgrade() {}

}
