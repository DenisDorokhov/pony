package net.dorokhov.pony.core.installation;

import net.dorokhov.pony.core.config.ConfigService;
import net.dorokhov.pony.core.dao.*;
import net.dorokhov.pony.core.domain.Installation;
import net.dorokhov.pony.core.domain.User;
import net.dorokhov.pony.core.installation.exception.AlreadyInstalledException;
import net.dorokhov.pony.core.installation.exception.NotInstalledException;
import net.dorokhov.pony.core.library.ScanJobService;
import net.dorokhov.pony.core.library.exception.LibraryNotDefinedException;
import net.dorokhov.pony.core.logging.LogService;
import net.dorokhov.pony.core.search.SearchService;
import net.dorokhov.pony.core.storage.StoredFileService;
import net.dorokhov.pony.core.user.UserService;
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

	private final Logger log = LoggerFactory.getLogger(getClass());

	private TransactionTemplate transactionTemplate;

	private InstallationDao installationDao;

	private ConfigService configService;

	private UserService userService;

	private ScanJobService scanJobService;

	private LogService logService;

	private StoredFileService storedFileService;

	private SearchService searchService;

	private SongDao songDao;
	private GenreDao genreDao;
	private AlbumDao albumDao;
	private ArtistDao artistDao;

	@Autowired
	public void setTransactionManager(PlatformTransactionManager aTransactionManager) {
		transactionTemplate = new TransactionTemplate(aTransactionManager, new DefaultTransactionDefinition(TransactionDefinition.PROPAGATION_REQUIRES_NEW));
	}

	@Autowired
	public void setInstallationDao(InstallationDao aInstallationDao) {
		installationDao = aInstallationDao;
	}

	@Autowired
	public void setConfigService(ConfigService aConfigService) {
		configService = aConfigService;
	}

	@Autowired
	public void setUserService(UserService aUserService) {
		userService = aUserService;
	}

	@Autowired
	public void setScanJobService(ScanJobService aScanJobService) {
		scanJobService = aScanJobService;
	}

	@Autowired
	public void setLogService(LogService aLogService) {
		logService = aLogService;
	}

	@Autowired
	public void setStoredFileService(StoredFileService aStoredFileService) {
		storedFileService = aStoredFileService;
	}

	@Autowired
	public void setSearchService(SearchService aSearchService) {
		searchService = aSearchService;
	}

	@Autowired
	public void setSongDao(SongDao aSongDao) {
		songDao = aSongDao;
	}

	@Autowired
	public void setGenreDao(GenreDao aGenreDao) {
		genreDao = aGenreDao;
	}

	@Autowired
	public void setAlbumDao(AlbumDao aAlbumDao) {
		albumDao = aAlbumDao;
	}

	@Autowired
	public void setArtistDao(ArtistDao aArtistDao) {
		artistDao = aArtistDao;
	}

	@Override
	@Transactional(readOnly = true)
	public Installation getInstallation() {
		return installationDao.findInstallation();
	}

	@Override
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public synchronized Installation install(final InstallCommand aCommand) throws AlreadyInstalledException {

		if (getInstallation() != null) {
			throw new AlreadyInstalledException();
		}

		log.info("Installing...");

		Installation installation = transactionTemplate.execute(new TransactionCallback<Installation>() {
			@Override
			public Installation doInTransaction(TransactionStatus status) {

				Installation installation = installationDao.install();

				configService.saveLibraryFolders(aCommand.getLibraryFolders());
				configService.saveAutoScanInterval(aCommand.getAutoScanInterval());

				for (User user : aCommand.getUsers()) {

					log.debug("Creating user [" + user.getEmail() + "]...");

					try {
						userService.create(user);
					} catch (Exception e) {
						throw new RuntimeException(e);
					}
				}

				return installation;
			}
		});

		try {
			scanJobService.startScanJob();
		} catch (LibraryNotDefinedException e) {
			logService.info(log, "installationService.libraryNotDefined", "Scan job not started, library is not defined.");
		}

		logService.info(log, "installationService.installed", "Successfully installed.");

		return installation;
	}

	@Override
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public synchronized void uninstall() throws NotInstalledException {

		if (getInstallation() == null) {
			throw new NotInstalledException();
		}

		logService.info(log, "installationService.uninstalling", "Uninstalling...");

		transactionTemplate.execute(new TransactionCallbackWithoutResult() {
			@Override
			protected void doInTransactionWithoutResult(TransactionStatus status) {

				searchService.clearIndex();

				songDao.deleteAll();
				genreDao.deleteAll();
				albumDao.deleteAll();
				artistDao.deleteAll();

				storedFileService.deleteAll();
			}
		});

		installationDao.uninstall();

		log.info("Successfully uninstalled.");
	}

}
