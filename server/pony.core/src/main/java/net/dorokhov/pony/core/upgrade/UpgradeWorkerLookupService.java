package net.dorokhov.pony.core.upgrade;

import java.util.List;

public interface UpgradeWorkerLookupService {

	public List<UpgradeWorker> lookupUpgradeWorkers(String aPackage, String aVersion);

}
