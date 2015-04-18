package net.dorokhov.pony.core.library;

import net.dorokhov.pony.core.common.PageProcessor;
import net.dorokhov.pony.core.config.ConfigService;
import net.dorokhov.pony.core.dao.ScanJobDao;
import net.dorokhov.pony.core.domain.LogMessage;
import net.dorokhov.pony.core.domain.ScanJob;
import net.dorokhov.pony.core.domain.ScanResult;
import net.dorokhov.pony.core.domain.ScanType;
import net.dorokhov.pony.core.library.exception.*;
import net.dorokhov.pony.core.logging.LogService;
import org.apache.commons.lang3.mutable.MutableInt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Service
public class ScanJobServiceImpl implements ScanJobService {

	private static final int INTERRUPTION_BUFFER_SIZE = 100;

	private final Logger log = LoggerFactory.getLogger(getClass());

	private final Object delegatesLock = new Object();

	private final List<Delegate> delegates = new ArrayList<>();

	private TransactionTemplate transactionTemplate;

	private ScanJobDao scanJobDao;

	private ConfigService configService;

	private ScanService scanService;

	private LogService logService;

	@Autowired
	public void setTransactionManager(PlatformTransactionManager aTransactionManager) {
		transactionTemplate = new TransactionTemplate(aTransactionManager, new DefaultTransactionDefinition(TransactionDefinition.PROPAGATION_REQUIRES_NEW));
	}

	@Autowired
	public void setScanJobDao(ScanJobDao aScanJobDao) {
		scanJobDao = aScanJobDao;
	}

	@Autowired
	public void setConfigService(ConfigService aConfigService) {
		configService = aConfigService;
	}

	@Autowired
	public void setScanService(ScanService aScanService) {
		scanService = aScanService;
	}

	@Autowired
	public void setLogService(LogService aLogService) {
		logService = aLogService;
	}

	@Override
	public void addDelegate(Delegate aDelegate) {
		synchronized (delegatesLock) {
			if (!delegates.contains(aDelegate)) {
				delegates.add(aDelegate);
			}
		}
	}

	@Override
	public void removeDelegate(Delegate aDelegate) {
		synchronized (delegatesLock) {
			delegates.remove(aDelegate);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public Page<ScanJob> getAll(Pageable aPageable) {
		return scanJobDao.findAll(aPageable);
	}

	@Override
	@Transactional(readOnly = true)
	public ScanJob getById(Long aId) {
		return scanJobDao.findOne(aId);
	}

	@Override
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public ScanJob startScanJob() throws LibraryNotDefinedException {
		return doCreateScanJob(configService.fetchLibraryFolders());
	}

	@Override
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public ScanJob startEditJob(final List<ScanEditCommand> aCommands) {

		final ScanJob job = transactionTemplate.execute(new TransactionCallback<ScanJob>() {
			@Override
			public ScanJob doInTransaction(TransactionStatus status) {

				LogMessage logMessage = logService.info(log, "scanJobService.editJobStarting", "Starting edit job for [" + aCommands.size() + "] songs...", String.valueOf(aCommands.size()));

				ScanJob startingJob = new ScanJob();

				startingJob.setScanType(ScanType.EDIT);
				startingJob.setStatus(ScanJob.Status.STARTING);
				startingJob.setLogMessage(logMessage);

				return scanJobDao.save(startingJob);
			}
		});

		for (Delegate next : new ArrayList<>(delegates)) {
			try {
				next.onJobCreation(job);
			} catch (Exception e) {
				log.error("Exception thrown when delegating onJobCreation to " + next, e);
			}
		}

		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					doEditJob(job.getId(), aCommands);
				} catch (final Exception e) {
					propagateUpdate(transactionTemplate.execute(new TransactionCallback<ScanJob>() {
						@Override
						public ScanJob doInTransaction(TransactionStatus status) {

							ScanJob failedJob = scanJobDao.findOne(job.getId());

							failedJob.setStatus(ScanJob.Status.FAILED);
							failedJob.setLogMessage(logService.error(log, "scanJobService.editJobErrorUnknown", "Unexpected error occurred when performing edit job.", e));

							return scanJobDao.save(failedJob);
						}
					}));
				}
			}
		}).start();

		return job;
	}

	@Transactional
	public void interruptCurrentJobs() {

		final MutableInt interruptedJobsCount = new MutableInt();

		transactionTemplate.execute(new TransactionCallbackWithoutResult() {
			@Override
			protected void doInTransactionWithoutResult(TransactionStatus aTransactionStatus) {
				PageProcessor.Handler<ScanJob> handler = new PageProcessor.Handler<ScanJob>() {
					@Override
					public void process(ScanJob aJob, Page<ScanJob> aPage, int aIndexInPage, long aIndexInAll) {

						aJob.setStatus(ScanJob.Status.INTERRUPTED);

						scanJobDao.save(aJob);

						interruptedJobsCount.increment();
					}

					@Override
					public Page<ScanJob> getPage(Pageable aPageable) {
						return scanJobDao.findByStatusIn(Arrays.asList(ScanJob.Status.STARTING, ScanJob.Status.STARTED), aPageable);
					}
				};
				new PageProcessor<>(INTERRUPTION_BUFFER_SIZE, new Sort("id"), handler).run();
			}
		});

		if (interruptedJobsCount.getValue() > 0) {
			logService.warn(log, "scanJobService.scanJobInterrupting", "Interrupted [" + interruptedJobsCount.getValue() + "] job(s).", Arrays.asList(String.valueOf(interruptedJobsCount.getValue())));
		}
	}

	@Override
	@Transactional
	synchronized public void startAutoScanJob() {

		log.trace("Checking if automatic scan needed...");

		boolean shouldScan = false;

		List<File> libraryFolders = configService.fetchLibraryFolders();

		if (libraryFolders.size() > 0) {

			if (scanService.getStatus() == null) {

				Integer autoScanInterval = configService.getAutoScanInterval();

				if (autoScanInterval != null) {

					Page<ScanResult> page = scanService.getAll(new PageRequest(0, 1, Sort.Direction.DESC, "date"));

					ScanResult lastResult = page.getTotalElements() > 0 ? page.getContent().get(0) : null;

					if (lastResult != null) {

						long secondsSinceLastScan = (new Date().getTime() - lastResult.getDate().getTime()) / 1000;

						if (secondsSinceLastScan >= autoScanInterval) {
							shouldScan = true;
						} else {
							log.trace("Too early for automatic scan.");
						}

					} else {

						log.trace("Library was never scanned before.");

						shouldScan = true;
					}

				} else {
					log.trace("Automatic scan is off.");
				}

			} else {
				log.trace("Library is already being scanned.");
			}

		} else {
			log.trace("No library files defined.");
		}

		if (shouldScan) {

			log.info("Starting automatic scan...");

			try {
				doCreateScanJob(libraryFolders);
			} catch (LibraryNotDefinedException e) {
				log.warn("Library is not defined.");
			}
		}
	}

	private ScanJob doCreateScanJob(final List<File> aTargetFolders) throws LibraryNotDefinedException {

		if (aTargetFolders.size() == 0) {
			throw new LibraryNotDefinedException();
		}

		final List<String> targetPaths = new ArrayList<>();
		for (File file : aTargetFolders) {
			targetPaths.add(file.getAbsolutePath());
		}

		final ScanJob job = transactionTemplate.execute(new TransactionCallback<ScanJob>() {
			@Override
			public ScanJob doInTransaction(TransactionStatus status) {

				ScanJob startingJob = new ScanJob();

				startingJob.setScanType(ScanType.FULL);
				startingJob.setStatus(ScanJob.Status.STARTING);
				startingJob.setLogMessage(logService.info(log, "scanJobService.scanJobStarting", "Starting scan job for " + aTargetFolders + "...", targetPaths));

				return scanJobDao.save(startingJob);
			}
		});

		for (Delegate next : new ArrayList<>(delegates)) {
			try {
				next.onJobCreation(job);
			} catch (Exception e) {
				log.error("Exception thrown when delegating onJobCreation to " + next, e);
			}
		}

		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					doScanJob(job.getId(), aTargetFolders);
				} catch (final Exception e) {
					propagateUpdate(transactionTemplate.execute(new TransactionCallback<ScanJob>() {
						@Override
						public ScanJob doInTransaction(TransactionStatus status) {

							ScanJob failedJob = scanJobDao.findOne(job.getId());

							failedJob.setStatus(ScanJob.Status.FAILED);
							failedJob.setLogMessage(logService.error(log, "scanJobService.scanJobErrorUnknown", "Unexpected error occurred when performing scan job.", e));

							return scanJobDao.save(failedJob);
						}
					}));
				}
			}
		}).start();

		return job;
	}

	private void doScanJob(final Long aJobId, final List<File> aTargetFolders) {

		final List<String> targetPaths = new ArrayList<>();
		for (File file : aTargetFolders) {
			targetPaths.add(file.getAbsolutePath());
		}

		propagateUpdate(transactionTemplate.execute(new TransactionCallback<ScanJob>() {
			@Override
			public ScanJob doInTransaction(TransactionStatus status) {

				ScanJob job = scanJobDao.findOne(aJobId);

				job.setStatus(ScanJob.Status.STARTED);
				job.setLogMessage(logService.info(log, "scanJobService.scanJobStarted", "Started scan job for " + aTargetFolders + ".", targetPaths));

				return scanJobDao.save(job);
			}
		}));

		ScanResult result = null;
		LogMessage logMessage = null;

		try {
			result = scanService.scan(aTargetFolders);
		} catch (FileNotFoundException e) {
			logMessage = logService.error(log, "scanJobService.scanJobErrorFileNotFound", "File [" + e.getFile().getAbsolutePath() + "] not found.", Arrays.asList(e.getFile().getAbsolutePath()));
		} catch (NotFolderException e) {
			logMessage = logService.error(log, "scanJobService.scanJobErrorNotFolder", "File [" + e.getFile().getAbsolutePath() + "] must be a folder.", Arrays.asList(e.getFile().getAbsolutePath()));
		} catch (ConcurrentScanException e) {
			logMessage = logService.error(log, "scanJobService.scanJobErrorConcurrentScan", "Library is already scanning.", e);
		} catch (Exception e) {
			logMessage = logService.error(log, "scanJobService.scanJobErrorUnknown", "Unexpected error occurred when performing scan job.", e);
		}

		final ScanResult currentResult = result;
		final LogMessage currentLogMessage = logMessage;

		propagateUpdate(transactionTemplate.execute(new TransactionCallback<ScanJob>() {
			@Override
			public ScanJob doInTransaction(TransactionStatus status) {

				ScanJob job = scanJobDao.findOne(aJobId);

				job.setScanResult(currentResult);

				if (currentResult != null) {
					job.setStatus(ScanJob.Status.COMPLETE);
					job.setLogMessage(logService.info(log, "scanJobService.scanJobComplete", "Scan job complete for " + aTargetFolders + ".", targetPaths));
				} else {
					job.setStatus(ScanJob.Status.FAILED);
					job.setLogMessage(currentLogMessage);
				}

				return scanJobDao.save(job);
			}
		}));
	}

	private void doEditJob(final Long aJobId, final List<ScanEditCommand> aCommands) {

		propagateUpdate(transactionTemplate.execute(new TransactionCallback<ScanJob>() {
			@Override
			public ScanJob doInTransaction(TransactionStatus status) {

				ScanJob job = scanJobDao.findOne(aJobId);

				job.setStatus(ScanJob.Status.STARTED);
				job.setLogMessage(logService.info(log, "scanJobService.editJobStarted", "Started edit job for [" + aCommands.size() + "] songs...", String.valueOf(aCommands.size())));

				return scanJobDao.save(job);
			}
		}));

		ScanResult result = null;
		LogMessage logMessage = null;

		try {
			result = scanService.edit(aCommands);
		} catch (SongNotFoundException e) {
			logMessage = logService.error(log, "scanJobService.editJobErrorSongNotFound", "Song [" + e.getSongId() + "] not found.", Arrays.asList(String.valueOf(e.getSongId())));
		} catch (FileNotFoundException e) {
			logMessage = logService.error(log, "scanJobService.editJobErrorFileNotFound", "File [" + e.getFile().getAbsolutePath() + "] not found.", Arrays.asList(e.getFile().getAbsolutePath()));
		} catch (NotSongException e) {
			logMessage = logService.error(log, "scanJobService.editJobErrorNotSong", "File [" + e.getFile().getAbsolutePath() + "] is not a song.", Arrays.asList(e.getFile().getAbsolutePath()));
		} catch (ConcurrentScanException e) {
			logMessage = logService.error(log, "scanJobService.editJobErrorConcurrentScan", "Library is already scanning.", e);
		} catch (Exception e) {
			logMessage = logService.error(log, "scanJobService.editJobErrorUnknown", "Unexpected error occurred when performing edit job.", e);
		}

		final ScanResult currentResult = result;
		final LogMessage currentLogMessage = logMessage;

		propagateUpdate(transactionTemplate.execute(new TransactionCallback<ScanJob>() {
			@Override
			public ScanJob doInTransaction(TransactionStatus status) {

				ScanJob job = scanJobDao.findOne(aJobId);

				job.setScanResult(currentResult);

				if (currentResult != null) {
					job.setStatus(ScanJob.Status.COMPLETE);
					job.setLogMessage(logService.info(log, "scanJobService.editJobComplete", "Edit job complete for [" + aCommands.size() + "] songs.", String.valueOf(aCommands.size())));
				} else {
					job.setStatus(ScanJob.Status.FAILED);
					job.setLogMessage(currentLogMessage);
				}

				return scanJobDao.save(job);
			}
		}));
	}

	private void propagateUpdate(ScanJob aJob) {
		for (Delegate next : new ArrayList<>(delegates)) {
			try {
				next.onJobUpdate(aJob);
			} catch (Exception e) {
				log.error("Exception thrown when delegating onJobUpdate to " + next, e);
			}
		}
	}

}
