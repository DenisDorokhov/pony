package net.dorokhov.pony.core.installation;

import net.dorokhov.pony.core.dao.ConfigDao;
import net.dorokhov.pony.core.dao.InstallationDao;
import net.dorokhov.pony.core.entity.Config;
import net.dorokhov.pony.core.entity.Installation;
import net.dorokhov.pony.core.installation.exception.AlreadyInstalledException;
import net.dorokhov.pony.core.installation.exception.NotInstalledException;
import net.dorokhov.pony.core.logging.LogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

@Service
public class InstallationServiceImpl implements InstallationService {

	private static final int CONFIG_AUTO_SCAN_INTERVAL = 86400;

	private final Logger log = LoggerFactory.getLogger(getClass());

	private TransactionTemplate transactionTemplate;

	private InstallationDao installationDao;

	private ConfigDao configDao;

	private LogService logService;

	@Autowired
	public void setTransactionManager(PlatformTransactionManager aTransactionManager) {
		transactionTemplate = new TransactionTemplate(aTransactionManager, new DefaultTransactionDefinition(TransactionDefinition.PROPAGATION_REQUIRES_NEW));
	}

	@Autowired
	public void setInstallationDao(InstallationDao aInstallationDao) {
		installationDao = aInstallationDao;
	}

	@Autowired
	public void setConfigDao(ConfigDao aConfigDao) {
		configDao = aConfigDao;
	}

	@Autowired
	public void setLogService(LogService aLogService) {
		logService = aLogService;
	}

	@Override
	@Transactional(readOnly = true)
	public Installation getInstallation() {
		return installationDao.findInstallation();
	}

	@Override
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public synchronized Installation install() throws AlreadyInstalledException {

		log.info("Installing...");

		Installation installation = transactionTemplate.execute(new TransactionCallback<Installation>() {
			@Override
			public Installation doInTransaction(TransactionStatus status) {

				if (getInstallation() != null) {
					throw new AlreadyInstalledException();
				}

				Installation installation = installationDao.install();

				Config config = new Config();

				config.setId(Config.AUTO_SCAN_INTERVAL);
				config.setInteger(CONFIG_AUTO_SCAN_INTERVAL);

				configDao.save(config);

				return installation;
			}
		});

		logService.info(log, "installationService.installed", "Successfully installed.");

		return installation;
	}

	@Override
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public synchronized void uninstall() throws NotInstalledException {

		transactionTemplate.execute(new TransactionCallbackWithoutResult() {
			@Override
			protected void doInTransactionWithoutResult(TransactionStatus status) {

				if (getInstallation() == null) {
					throw new NotInstalledException();
				}

				installationDao.uninstall();
			}
		});

		log.info("Successfully uninstalled.");
	}
}
