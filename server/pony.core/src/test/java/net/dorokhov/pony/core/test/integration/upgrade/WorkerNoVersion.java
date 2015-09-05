package net.dorokhov.pony.core.test.integration.upgrade;

import net.dorokhov.pony.core.upgrade.UpgradeWorker;
import org.springframework.stereotype.Service;

@Service
public class WorkerNoVersion implements UpgradeWorker {

	@Override
	public void performUpgrade() {}

}
