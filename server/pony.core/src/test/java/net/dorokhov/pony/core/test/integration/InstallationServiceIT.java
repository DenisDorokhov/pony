package net.dorokhov.pony.core.test.integration;

import net.dorokhov.pony.core.domain.Installation;
import net.dorokhov.pony.core.exception.AlreadyInstalledException;
import net.dorokhov.pony.core.exception.NotInstalledException;
import net.dorokhov.pony.core.service.InstallationService;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class InstallationServiceIT {

	private InstallationService service;

	@Before
	public void setUp() throws Exception {

		ApplicationContext context = new ClassPathXmlApplicationContext("context.xml");

		service = context.getBean(InstallationService.class);

		restore();
	}

	@After
	public void tearDown() throws Exception {
		restore();
	}

	@Test
	public void test() throws Exception {

		boolean isExceptionThrown;

		Assert.assertNull(service.getInstallation());

		checkInstallation(service.install());
		checkInstallation(service.getInstallation());

		isExceptionThrown = false;

		try {
			service.install();
		} catch (AlreadyInstalledException e) {
			isExceptionThrown = true;
		}

		Assert.assertTrue(isExceptionThrown);

		service.uninstall();

		Assert.assertNull(service.getInstallation());

		isExceptionThrown = false;

		try {
			service.uninstall();
		} catch (NotInstalledException e) {
			isExceptionThrown = true;
		}

		Assert.assertTrue(isExceptionThrown);

		service.install(); // check installation after uninstallation

		checkInstallation(service.getInstallation());
	}

	private void restore() throws Exception {
		if (service.getInstallation() != null) {
			service.uninstall();
		}
	}

	private void checkInstallation(Installation aInstallation) {
		Assert.assertNotNull(aInstallation.getId());
		Assert.assertNotNull(aInstallation.getCreationDate());
		Assert.assertNotNull(aInstallation.getUpdateDate());
		Assert.assertNotNull(aInstallation.getVersion());
	}
}
