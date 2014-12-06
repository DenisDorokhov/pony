package net.dorokhov.pony.core.test.integration;

import net.dorokhov.pony.core.dao.ConfigDao;
import net.dorokhov.pony.core.domain.Config;
import net.dorokhov.pony.core.domain.Installation;
import net.dorokhov.pony.core.installation.InstallationCommand;
import net.dorokhov.pony.core.installation.InstallationService;
import net.dorokhov.pony.core.installation.exception.AlreadyInstalledException;
import net.dorokhov.pony.core.installation.exception.NotInstalledException;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.Arrays;

public class InstallationServiceIT {

	protected ConfigurableApplicationContext context;

	private InstallationService installationService;

	private ConfigDao configDao;

	@Before
	public void setUp() throws Exception {

		context = new ClassPathXmlApplicationContext("context.xml");

		installationService = context.getBean(InstallationService.class);
		configDao = context.getBean(ConfigDao.class);

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

		InstallationCommand defaultCommand = new InstallationCommand();

		Assert.assertNull(installationService.getInstallation());

		checkInstallation(installationService.install(defaultCommand));
		checkInstallation(installationService.getInstallation());

		Assert.assertNotNull(configDao.findOne(Config.AUTO_SCAN_INTERVAL));

		isExceptionThrown = false;

		try {
			installationService.install(new InstallationCommand());
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

		InstallationCommand testCommand = new InstallationCommand();

		testCommand.setConfig(Arrays.asList(new Config(Config.AUTO_SCAN_INTERVAL, 1000), new Config("testId", "testValue")));

		installationService.install(testCommand);

		checkInstallation(installationService.getInstallation());

		Assert.assertEquals(1000, configDao.findOne(Config.AUTO_SCAN_INTERVAL).getInteger());
		Assert.assertEquals("testValue", configDao.findOne("testId").getValue());
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
