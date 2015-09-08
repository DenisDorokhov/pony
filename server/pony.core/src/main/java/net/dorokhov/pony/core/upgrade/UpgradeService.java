package net.dorokhov.pony.core.upgrade;

import net.dorokhov.pony.core.upgrade.exception.UpgradeInvalidException;

public interface UpgradeService {

	public void upgrade(String aVersion) throws UpgradeInvalidException;

}
