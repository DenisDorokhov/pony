package net.dorokhov.pony.core.test.integration;

import net.dorokhov.pony.core.config.ConfigService;
import net.dorokhov.pony.core.domain.Installation;
import net.dorokhov.pony.core.installation.InstallCommand;
import net.dorokhov.pony.core.installation.InstallationService;
import net.dorokhov.pony.core.installation.exception.AlreadyInstalledException;
import net.dorokhov.pony.core.installation.exception.NotInstalledException;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.File;
import java.util.Arrays;

public class InstallationServiceIT {

	protected ConfigurableApplicationContext context;

	private InstallationService installationService;

	private ConfigService configService;

	@Before
	public void setUp() throws Exception {

		context = new ClassPathXmlApplicationContext("context.xml");

		installationService = context.getBean(InstallationService.class);
		configService = context.getBean(ConfigService.class);

		restore();
	}

	@After
	public void tearDown() throws Exception {

		restore();

		if (context != null) {
			context.close();
		}
	}

	@Test
	public void test() throws Exception {

		boolean isExceptionThrown;

		InstallCommand defaultCommand = new InstallCommand();

		Assert.assertNull(installationService.getInstallation());

		checkInstallation(installationService.install(defaultCommand));
		checkInstallation(installationService.getInstallation());

		isExceptionThrown = false;

		try {
			installationService.install(new InstallCommand());
		} catch (AlreadyInstalledException e) {
			isExceptionThrown = true;
		}

		Assert.assertTrue(isExceptionThrown);

		installationService.uninstall();

		Assert.assertNull(installationService.getInstallation());

		isExceptionThrown = false;

		try {
			installationService.uninstall();
		} catch (NotInstalledException e) {
			isExceptionThrown = true;
		}

		Assert.assertTrue(isExceptionThrown);

		InstallCommand testCommand = new InstallCommand();

		testCommand.setAutoScanInterval(1000);
		testCommand.setLibraryFolders(Arrays.asList(new File("/folder1"), new File("/folder2")));

		installationService.install(testCommand);

		checkInstallation(installationService.getInstallation());

		Assert.assertEquals(Integer.valueOf(1000), configService.getAutoScanInterval());
		Assert.assertEquals(Arrays.asList(new File("/folder1"), new File("/folder2")), configService.fetchLibraryFolders());
	}

	private void restore() throws Exception {
		if (installationService.getInstallation() != null) {
			installationService.uninstall();
		}
	}

	private void checkInstallation(Installation aInstallation) {
		Assert.assertNotNull(aInstallation.getId());
		Assert.assertNotNull(aInstallation.getCreationDate());
		Assert.assertNull(aInstallation.getUpdateDate());
		Assert.assertNotNull(aInstallation.getVersion());
	}

}
