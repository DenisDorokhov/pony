package net.dorokhov.pony.core.upgrade;

import net.dorokhov.pony.core.upgrade.exception.UpgradeInvalidException;

import java.util.List;

public interface UpgradeWorkerLookupService {

	public List<UpgradeWorker> lookupUpgradeWorkers(String aFromVersion, String aToVersion) throws UpgradeInvalidException;

}
