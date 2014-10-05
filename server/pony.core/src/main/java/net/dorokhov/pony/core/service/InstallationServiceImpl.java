package net.dorokhov.pony.core.service;

import net.dorokhov.pony.core.dao.ConfigDao;
import net.dorokhov.pony.core.dao.InstallationDao;
import net.dorokhov.pony.core.domain.Config;
import net.dorokhov.pony.core.dictionary.ConfigOptions;
import net.dorokhov.pony.core.domain.Installation;
import net.dorokhov.pony.core.exception.AlreadyInstalledException;
import net.dorokhov.pony.core.exception.NotInstalledException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class InstallationServiceImpl implements InstallationService {

	private static final int CONFIG_AUTO_SCAN_INTERVAL = 86400;

	private final Logger log = LoggerFactory.getLogger(getClass());

	private InstallationDao installationDao;

	private ConfigDao configDao;

	@Autowired
	public void setInstallationDao(InstallationDao aInstallationDao) {
		installationDao = aInstallationDao;
	}

	@Autowired
	public void setConfigDao(ConfigDao aConfigDao) {
		configDao = aConfigDao;
	}

	@Override
	@Transactional(readOnly = true)
	public Installation getInstallation() {
		return installationDao.findInstallation();
	}

	@Override
	@Transactional
	public synchronized Installation install() throws AlreadyInstalledException {

		log.info("Installing...");

		if (getInstallation() != null) {
			throw new AlreadyInstalledException();
		}

		Installation installation = installationDao.install();

		Config config = new Config();

		config.setId(ConfigOptions.AUTO_SCAN_INTERVAL);
		config.setInteger(CONFIG_AUTO_SCAN_INTERVAL);

		configDao.save(config);

		log.info("Successfully installed.");

		return installation;
	}

	@Override
	@Transactional
	public synchronized void uninstall() throws NotInstalledException {

		log.info("Uninstalling...");

		if (getInstallation() == null) {
			throw new NotInstalledException();
		}

		installationDao.uninstall();

		log.info("Successfully uninstalled.");
	}
}
