package net.dorokhov.pony.core.test;

import net.dorokhov.pony.core.service.InstallationService;
import net.dorokhov.pony.core.service.SearchService;
import net.dorokhov.pony.core.service.StoredFileService;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class AbstractIntegrationCase extends AbstractCase {

	protected ConfigurableApplicationContext context;

	protected InstallationService installationService;

	protected StoredFileService storedFileService;

	protected SearchService searchService;

	public void baseSetUp() throws Exception {

		super.baseSetUp();

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

	public void baseTearDown() throws Exception {

		super.baseTearDown();

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
