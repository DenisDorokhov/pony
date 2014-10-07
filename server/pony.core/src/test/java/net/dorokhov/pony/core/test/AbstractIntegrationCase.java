package net.dorokhov.pony.core.test;

import net.dorokhov.pony.core.service.InstallationService;
import net.dorokhov.pony.core.service.SearchService;
import net.dorokhov.pony.core.service.StoredFileService;
import org.junit.After;
import org.junit.Before;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class AbstractIntegrationCase {

	protected ConfigurableApplicationContext context;

	protected InstallationService installationService;

	protected StoredFileService storedFileService;

	protected SearchService searchService;

	@Before
	public void baseSetUp() throws Exception {

		context = new ClassPathXmlApplicationContext("context.xml");

		installationService = context.getBean(InstallationService.class);
		storedFileService = context.getBean(StoredFileService.class);
		searchService = context.getBean(SearchService.class);

		if (installationService.getInstallation() != null) {
			searchService.clearIndex();
			storedFileService.deleteAll();
			installationService.uninstall();
		}

		installationService.install();
	}

	@After
	public void baseTearDown() throws Exception {

		if (installationService != null && installationService.getInstallation() != null) {

			if (searchService != null) {
				searchService.clearIndex();
			}
			if (storedFileService != null) {
				storedFileService.deleteAll();
			}

			installationService.uninstall();
		}

		if (context != null) {
			context.close();
		}
	}
}
