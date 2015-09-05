package net.dorokhov.pony.core.test.integration.upgrade;

import net.dorokhov.pony.core.upgrade.UpgradeWorker;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class WorkerMock_999_1_0 implements UpgradeWorker {

	@Override
	public String getVersion() {
		return "999.1.0";
	}

	@Override
	@Transactional
	public void run() {}

}
