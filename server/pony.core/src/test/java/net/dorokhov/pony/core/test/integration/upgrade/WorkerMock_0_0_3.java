package net.dorokhov.pony.core.test.integration.upgrade;

import net.dorokhov.pony.core.upgrade.UpgradeWorker;
import org.springframework.stereotype.Service;

@Service
@UpgradeWorker.Version("0.0.3")
public class WorkerMock_0_0_3 implements UpgradeWorker {

	@Override
	public void performUpgrade() {}

}
