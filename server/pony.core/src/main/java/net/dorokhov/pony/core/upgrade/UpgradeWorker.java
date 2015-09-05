package net.dorokhov.pony.core.upgrade;

public interface UpgradeWorker {

	public String getVersion();

	public void run();

}
