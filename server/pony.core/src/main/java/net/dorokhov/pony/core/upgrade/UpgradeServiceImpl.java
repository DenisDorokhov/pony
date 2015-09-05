package net.dorokhov.pony.core.upgrade;

import net.dorokhov.pony.core.domain.LogMessage;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
public class UpgradeServiceImpl implements UpgradeService {

	private final Logger log = LoggerFactory.getLogger(getClass());

	private JdbcTemplate jdbcTemplate;

	private TransactionTemplate transactionTemplate;

	private UpgradeWorkerLookupService upgradeWorkerLookupService;

	@Autowired
	public void setDataSource(DataSource aDataSource) {
		jdbcTemplate = new JdbcTemplate(aDataSource);
	}

	@Autowired
	public void setUpgradeWorkerLookupService(UpgradeWorkerLookupService aUpgradeWorkerLookupService) {
		upgradeWorkerLookupService = aUpgradeWorkerLookupService;
	}

	@Autowired
	public void setTransactionManager(PlatformTransactionManager aTransactionManager) {
		transactionTemplate = new TransactionTemplate(aTransactionManager, new DefaultTransactionDefinition(TransactionDefinition.PROPAGATION_REQUIRES_NEW));
	}

	@Override
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public synchronized void upgrade(String aVersion) {

		String installationVersion = fetchInstallationVersion();

		if (installationVersion != null) {

			if (installationVersion.equals("1.0")) {
				installationVersion = "0.1.2"; // Migrate legacy installation.
			}

			for (final UpgradeWorker worker : upgradeWorkerLookupService.lookupUpgradeWorkers(installationVersion, aVersion)) {

				String oldVersion = installationVersion;

				try {

					log.info("Upgrading from version [{}] to [{}]...", oldVersion, worker.getVersion());

					transactionTemplate.execute(new TransactionCallbackWithoutResult() {
						@Override
						protected void doInTransactionWithoutResult(TransactionStatus status) {

							worker.run();

							updateInstallationVersion(worker.getVersion());
						}
					});

					installationVersion = worker.getVersion();

				} catch (RuntimeException e) {

					try {
						logFailedUpgrade(oldVersion, installationVersion, e);
					} catch (RuntimeException logException) {
						log.error("Could not insert log entry after failed upgrade.", logException);
					}

					throw e;
				}

				logSuccessfulUpgrade(oldVersion, installationVersion);
			}

			if (!installationVersion.equals(aVersion)) {

				updateInstallationVersion(aVersion);

				logSuccessfulUpgrade(installationVersion, aVersion);
			}
		}
	}

	private void logSuccessfulUpgrade(String aFromVersion, String aToVersion) {
		insertLogMessage(LogMessage.Type.INFO, "upgradeService.upgraded", "Upgraded from version [" + aFromVersion + "] to [" + aToVersion + "].",
				null, Arrays.asList(aFromVersion, aToVersion));
	}

	private void logFailedUpgrade(String aFromVersion, String aToVersion, Throwable aThrowable) {
		insertLogMessage(LogMessage.Type.ERROR, "upgradeService.couldNotUpgrade", "Could not upgrade from version [" + aFromVersion + "] to [" + aToVersion + "].",
				ExceptionUtils.getStackTrace(aThrowable).trim(), Arrays.asList(aFromVersion, aToVersion));
	}

	private String fetchInstallationVersion() {

		List<Map<String, Object>> installationList = jdbcTemplate.queryForList("SELECT * FROM installation");
		if (installationList.size() > 0) {
			return (String)installationList.get(0).get("version");
		}

		return null;
	}

	private void updateInstallationVersion(String aVersion) {
		jdbcTemplate.update("UPDATE installation SET version = ?", aVersion);
	}

	private void insertLogMessage(final LogMessage.Type aType, final String aCode, final String aText, final String aDetails, final List<String> aArguments) {
		transactionTemplate.execute(new TransactionCallbackWithoutResult() {
			@Override
			protected void doInTransactionWithoutResult(TransactionStatus status) {

				String messageToLog = aText;
				if (aDetails != null) {
					messageToLog += "\n" + aDetails;
				}

				switch (aType) {
					case DEBUG:
						log.debug(messageToLog);
						break;
					case INFO:
						log.info(messageToLog);
						break;
					case WARN:
						log.warn(messageToLog);
						break;
					case ERROR:
						log.error(messageToLog);
						break;
				}

				KeyHolder keyHolder = new GeneratedKeyHolder();

				jdbcTemplate.update(new PreparedStatementCreator() {
					@Override
					public PreparedStatement createPreparedStatement(Connection aConnection) throws SQLException {

						PreparedStatement query = aConnection.prepareStatement("INSERT INTO log_message " +
								"(date, type, code, text, details) VALUES (NOW(), ?, ?, ?, ?)");

						query.setInt(1, aType.ordinal());
						query.setString(2, aCode);
						query.setString(3, aText);
						query.setString(4, aDetails);

						return query;
					}
				}, keyHolder);

				if (aArguments != null) {
					for (int i = 0; i < aArguments.size(); i++) {
						jdbcTemplate.update("INSERT INTO log_message_argument " +
										"(sort, value, log_message_id) VALUES (?, ?, ?)",
								i, aArguments.get(i), keyHolder.getKey());
					}
				}
			}
		});
	}

}
