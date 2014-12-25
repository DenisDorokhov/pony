package net.dorokhov.pony.core.test;

import net.dorokhov.pony.core.installation.InstallationCommand;
import net.dorokhov.pony.core.installation.InstallationService;
import net.dorokhov.pony.core.search.SearchService;
import net.dorokhov.pony.core.storage.StoredFileService;
import org.junit.After;
import org.junit.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.security.core.context.SecurityContextHolder;

public class AbstractIntegrationCase {

	protected final Logger log = LoggerFactory.getLogger(getClass());

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

		installationService.install(new InstallationCommand());
	}

	@After
	public void baseTearDown() throws Exception {

		SecurityContextHolder.clearContext();

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
