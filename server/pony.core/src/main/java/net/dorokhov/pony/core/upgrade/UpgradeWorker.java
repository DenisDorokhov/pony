package net.dorokhov.pony.core.upgrade;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

public interface UpgradeWorker {

	@Target(TYPE)
	@Retention(RUNTIME)
	public @interface Version {
		String value();
	}

	public void performUpgrade();

}
