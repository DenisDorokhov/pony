package net.dorokhov.pony.core.test.integration.upgrade;

import net.dorokhov.pony.core.upgrade.UpgradeWorker;
import org.springframework.stereotype.Service;

@Service
@UpgradeWorker.Version("1.0")
public class WorkerIllegalVersion implements UpgradeWorker {

	@Override
	public void performUpgrade() {}

}
