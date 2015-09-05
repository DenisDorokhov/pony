package net.dorokhov.pony.core.test.integration.upgrade;

import net.dorokhov.pony.core.upgrade.UpgradeWorker;
import org.springframework.stereotype.Service;

@Service
@UpgradeWorker.Version("0.1.3")
public class WorkerMock_0_1_3  implements UpgradeWorker {

	@Override
	public void performUpgrade() {}

}
