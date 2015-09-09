package net.dorokhov.pony.web.server.upgrade;

import net.dorokhov.pony.core.upgrade.UpgradeService;
import net.dorokhov.pony.core.upgrade.exception.UpgradeInvalidException;
import net.dorokhov.pony.core.version.VersionProvider;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;
import org.springframework.core.io.ByteArrayResource;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.io.InputStream;

public class UpgradeListener implements ServletContextListener {

	private final Logger log = LoggerFactory.getLogger(getClass());

	@Override
	public void contextInitialized(ServletContextEvent aEvent) {

		InputStream xmlStream = aEvent.getServletContext().getResourceAsStream("/WEB-INF/context/upgrade.xml");

		ConfigurableApplicationContext context = null;

		try {
			context = new GenericXmlApplicationContext(new ByteArrayResource(IOUtils.toByteArray(xmlStream)));
		} catch (Exception e) {
			log.error("Could not initialize application context.", e);
		} finally {
			IOUtils.closeQuietly(xmlStream);
		}

		if (context != null) {
			try {

				VersionProvider versionProvider = context.getBean(VersionProvider.class);
				UpgradeService upgradeService = context.getBean(UpgradeService.class);

				try {
					upgradeService.upgrade(versionProvider.getVersion());
				} catch (Exception e) {

					log.error("Unexpected error occurred running upgrade to version [" + versionProvider.getVersion() + "].", e);

					throw new RuntimeException(e);
				}

			} finally {
				context.close();
			}
		}
	}

	@Override
	public void contextDestroyed(ServletContextEvent aEvent) {}

}
