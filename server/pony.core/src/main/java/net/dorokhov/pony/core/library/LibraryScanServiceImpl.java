package net.dorokhov.pony.core.library;

import net.dorokhov.pony.core.dao.*;
import net.dorokhov.pony.core.entity.ScanResult;
import net.dorokhov.pony.core.entity.Song;
import net.dorokhov.pony.core.entity.StoredFile;
import net.dorokhov.pony.core.library.exception.ConcurrentScanException;
import net.dorokhov.pony.core.library.file.LibraryFolder;
import net.dorokhov.pony.core.library.file.LibrarySong;
import net.dorokhov.pony.core.logging.LogService;
import net.dorokhov.pony.core.storage.StoredFileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.PreDestroy;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class LibraryScanServiceImpl implements LibraryScanService {

	private final static int NUMBER_OF_THREADS = 10;
	private final static int NUMBER_OF_STEPS = 5;

	private final Logger log = LoggerFactory.getLogger(getClass());

	private final Object delegatesLock = new Object();
	private final Object statusCheckLock = new Object();

	private final List<Delegate> delegates = new ArrayList<>();

	private final AtomicReference<StatusImpl> statusReference = new AtomicReference<>();

	private final AtomicReference<ExecutorService> executorReference = new AtomicReference<>();

	private final AtomicInteger completedImportTaskCount = new AtomicInteger();

	private TransactionTemplate transactionTemplate;

	private LogService logService;

	private ScanResultDao scanResultDao;

	private FileScanService fileScanService;

	private LibraryService libraryService;

	private SongDao songDao;
	private GenreDao genreDao;
	private ArtistDao artistDao;
	private AlbumDao albumDao;

	private StoredFileService storedFileService;

	@Autowired
	public void setTransactionManager(PlatformTransactionManager aTransactionManager) {
		transactionTemplate = new TransactionTemplate(aTransactionManager, new DefaultTransactionDefinition(TransactionDefinition.PROPAGATION_REQUIRES_NEW));
	}

	@Autowired
	public void setLogService(LogService aLogService) {
		logService = aLogService;
	}

	@Autowired
	public void setScanResultDao(ScanResultDao aScanResultDao) {
		scanResultDao = aScanResultDao;
	}

	@Autowired
	public void setFileScanService(FileScanService aFileScanService) {
		fileScanService = aFileScanService;
	}

	@Autowired
	public void setLibraryService(LibraryService aLibraryService) {
		libraryService = aLibraryService;
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
	public void setArtistDao(ArtistDao aArtistDao) {
		artistDao = aArtistDao;
	}

	@Autowired
	public void setAlbumDao(AlbumDao aAlbumDao) {
		albumDao = aAlbumDao;
	}

	@Autowired
	public void setStoredFileService(StoredFileService aStoredFileService) {
		storedFileService = aStoredFileService;
	}

	@PreDestroy
	public void onPreDestroy() {

		ExecutorService executor = executorReference.get();

		if (executor != null) {
			executor.shutdownNow();
		}
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
	public Status getStatus() {

		Status status = statusReference.get();

		return status != null ? new StatusImpl(status) : null;
	}

	@Override
	@Transactional(readOnly = true)
	public ScanResult getLastResult() {

		Page<ScanResult> scanResults = scanResultDao.findAll(new PageRequest(0, 1, Sort.Direction.DESC, "date"));

		return scanResults.getNumberOfElements() > 0 ? scanResults.getContent().get(0) : null;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public ScanResult scan(List<File> aTargetFolders) throws ConcurrentScanException {

		synchronized (statusCheckLock) {

			if (statusReference.get() != null) {
				throw new ConcurrentScanException();
			}

			statusReference.set(new StatusImpl(aTargetFolders, 1, "searchingMediaFiles", -1));
		}

		synchronized (delegatesLock) {
			for (Delegate next : new ArrayList<>(delegates)) {
				try {
					next.onScanStart(new ArrayList<>(aTargetFolders));
				} catch (Exception e) {
					log.error("Exception thrown when delegating onScanStart to " + next, e);
				}
			}
		}

		ScanResult scanResult;

		try {

			executorReference.set(Executors.newFixedThreadPool(NUMBER_OF_THREADS));

			scanResult = doScan(aTargetFolders);

			synchronized (delegatesLock) {
				for (Delegate next : new ArrayList<>(delegates)) {
					try {
						next.onScanFinish(scanResult);
					} catch (Exception e) {
						log.error("Exception thrown when delegating onScanFinish to " + next, e);
					}
				}
			}

		} catch (final Exception scanException) {

			transactionTemplate.execute(new TransactionCallbackWithoutResult() {
				@Override
				protected void doInTransactionWithoutResult(TransactionStatus status) {
					logError("libraryScanService.scanFailed", "Scan failed.", scanException);
				}
			});

			synchronized (delegatesLock) {
				for (Delegate next : new ArrayList<>(delegates)) {
					try {
						next.onScanFail(scanException);
					} catch (Exception e) {
						log.error("Exception thrown when delegating onScanFail to " + next, e);
					}
				}
			}

			throw new RuntimeException(scanException);

		} finally {
			executorReference.set(null);
			statusReference.set(null);
		}

		return scanResult;
	}

	private ScanResult doScan(final List<File> aTargetFolders) throws InterruptedException {

		List<String> targetPaths = new ArrayList<>();
		for (File targetFolder : aTargetFolders) {
			targetPaths.add(targetFolder.getAbsolutePath());
		}

		ScanResult lastScan = getLastResult();

		long songCountBeforeScan = songDao.count();
		long genreCountBeforeScan = genreDao.count();
		long artistCountBeforeScan = artistDao.count();
		long albumCountBeforeScan = albumDao.count();
		long artworkCountBeforeScan = storedFileService.getCountByTag(StoredFile.TAG_ARTWORK_EMBEDDED) + storedFileService.getCountByTag(StoredFile.TAG_ARTWORK_FILE);

		logInfo("libraryScanService.scanStarted", "Scanning library " + aTargetFolders + "...", (String[]) targetPaths.toArray());

		long startTime = System.nanoTime();

		List<LibrarySong> songFiles = performScanSteps(aTargetFolders);

		long endTime = System.nanoTime();

		long songCountAfterScan = songDao.count();
		long songCountCreated = songDao.countByCreationDateGreaterThan(lastScan.getDate());
		long songCountUpdated = songDao.countByUpdateDateGreaterThan(lastScan.getDate());
		long songCountDeleted = Math.max(0, songCountBeforeScan - (songCountAfterScan - songCountCreated));

		long genreCountAfterScan = genreDao.count();
		long genreCountCreated = genreDao.countByCreationDateGreaterThan(lastScan.getDate());
		long genreCountUpdated = genreDao.countByUpdateDateGreaterThan(lastScan.getDate());
		long genreCountDeleted = Math.max(0, genreCountBeforeScan - (genreCountAfterScan - genreCountCreated));

		long artistCountAfterScan = artistDao.count();
		long artistCountCreated = artistDao.countByCreationDateGreaterThan(lastScan.getDate());
		long artistCountUpdated = artistDao.countByUpdateDateGreaterThan(lastScan.getDate());
		long artistCountDeleted = Math.max(0, artistCountBeforeScan - (artistCountAfterScan - artistCountCreated));

		long albumCountAfterScan = albumDao.count();
		long albumCountCreated = albumDao.countByCreationDateGreaterThan(lastScan.getDate());
		long albumCountUpdated = albumDao.countByUpdateDateGreaterThan(lastScan.getDate());
		long albumCountDeleted = Math.max(0, albumCountBeforeScan - (albumCountAfterScan - albumCountCreated));

		long artworkCountAfterScan = storedFileService.getCountByTag(StoredFile.TAG_ARTWORK_EMBEDDED) + storedFileService.getCountByTag(StoredFile.TAG_ARTWORK_FILE);
		long artworkCountCreated = storedFileService.getCountByTagAndCreationDate(StoredFile.TAG_ARTWORK_EMBEDDED, lastScan.getDate()) +
				storedFileService.getCountByTagAndCreationDate(StoredFile.TAG_ARTWORK_FILE, lastScan.getDate());
		long artworkCountUpdated = storedFileService.getCountByTagAndUpdateDate(StoredFile.TAG_ARTWORK_EMBEDDED, lastScan.getDate()) +
				storedFileService.getCountByTagAndUpdateDate(StoredFile.TAG_ARTWORK_FILE, lastScan.getDate());
		long artworkCountDeleted = Math.max(0, artworkCountBeforeScan - (artworkCountAfterScan - artworkCountCreated));

		ScanResult scanResult = new ScanResult();

		scanResult.setFolders(targetPaths);
		scanResult.setDuration(endTime - startTime);

		scanResult.setFoundSongCount(Integer.valueOf(songFiles.size()).longValue());

		scanResult.setCreatedArtistCount(artistCountCreated);
		scanResult.setUpdatedArtistCount(artistCountUpdated);
		scanResult.setDeletedArtistCount(artistCountDeleted);

		scanResult.setCreatedAlbumCount(albumCountCreated);
		scanResult.setUpdatedAlbumCount(albumCountUpdated);
		scanResult.setDeletedAlbumCount(albumCountDeleted);

		scanResult.setCreatedGenreCount(genreCountCreated);
		scanResult.setUpdatedGenreCount(genreCountUpdated);
		scanResult.setDeletedGenreCount(genreCountDeleted);

		scanResult.setCreatedSongCount(songCountCreated);
		scanResult.setUpdatedSongCount(songCountUpdated);
		scanResult.setDeletedSongCount(songCountDeleted);

		scanResult.setCreatedArtworkCount(artworkCountCreated);
		scanResult.setUpdatedArtworkCount(artworkCountUpdated);
		scanResult.setDeletedArtworkCount(artworkCountDeleted);

		return scanResultDao.save(scanResult);
	}

	private List<LibrarySong> performScanSteps(final List<File> aTargetFolders) {

		logInfo("libraryScanService.searchingMediaFiles", "Searching media files...");

		List<LibraryFolder> library = new ArrayList<>();
		for (File targetFolder : aTargetFolders) {
			library.add(fileScanService.scanFolder(targetFolder));
		}

		logInfo("libraryScanService.cleaningSongs", "Cleaning songs...");
		libraryService.cleanSongs(library, new LibraryService.ProgressDelegate() {
			@Override
			public void onProgress(double aProgress) {
				statusReference.set(new StatusImpl(aTargetFolders, 2, "cleaningArtworks", aProgress));
			}
		});

		logInfo("libraryScanService.cleaningArtworks", "Cleaning artworks...");
		libraryService.cleanArtworks(library, new LibraryService.ProgressDelegate() {
			@Override
			public void onProgress(double aProgress) {
				statusReference.set(new StatusImpl(aTargetFolders, 3, "cleaningArtworks", aProgress));
			}
		});

		List<LibrarySong> songFiles = new ArrayList<>();
		for (LibraryFolder folder : library) {
			songFiles.addAll(folder.getChildSongs(true));
		}

		ExecutorService executor = executorReference.get();

		for (LibrarySong file : songFiles) {
			executor.submit(new ImportTask(aTargetFolders, library, file, songFiles.size()));
		}

		executor.shutdown();

		try {
			executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}

		logInfo("libraryScanService.importingArtworks", "Importing artworks...");
		libraryService.importArtworks(library, new LibraryService.ProgressDelegate() {
			@Override
			public void onProgress(double aProgress) {
				statusReference.set(new StatusImpl(aTargetFolders, 5, "importingArtworks", aProgress));
			}
		});

		return songFiles;
	}

	private void logInfo(String aCode, String aMessage, String... aArguments) {
		log.info(aMessage);
		logService.info(aCode, aMessage, Arrays.asList(aArguments));
	}

	private void logWarn(String aCode, String aMessage, Throwable aThrowable, String... aArguments) {
		log.warn(aMessage);
		logService.warn(aCode, aMessage, aThrowable, Arrays.asList(aArguments));
	}

	private void logError(String aCode, String aMessage, Throwable aThrowable, String... aArguments) {
		log.error(aMessage);
		logService.error(aCode, aMessage, aThrowable, Arrays.asList(aArguments));
	}

	private class StatusImpl implements Status {

		private final List<File> targetFolders;
		private final double progress;
		private final int step;
		private final String stepCode;

		public StatusImpl(List<File> aTargetFiles, int aStep, String aStepCode, double aProgress) {
			targetFolders = aTargetFiles != null ? new ArrayList<>(aTargetFiles) : null;
			step = aStep;
			stepCode = aStepCode;
			progress = aProgress;
		}

		public StatusImpl(Status aStatus) {
			this(aStatus.getTargetFolders(), aStatus.getStep(), aStatus.getStepCode(), aStatus.getProgress());
		}

		@Override
		public List<File> getTargetFolders() {
			return targetFolders != null ? new ArrayList<>(targetFolders) : null;
		}

		@Override
		public String getStepCode() {
			return stepCode;
		}

		@Override
		public double getProgress() {
			return progress;
		}

		@Override
		public int getStep() {
			return step;
		}

		@Override
		public int getTotalSteps() {
			return NUMBER_OF_STEPS;
		}
	}

	private class ImportTask implements Callable<Song> {

		private final List<File> targetFolders;

		private final List<LibraryFolder> library;

		private final LibrarySong songFile;

		private final int taskCount;

		private ImportTask(List<File> aTargetFolders, List<LibraryFolder> aLibrary, LibrarySong aSongFile, int aTaskCount) {
			targetFolders = aTargetFolders;
			library = aLibrary;
			songFile = aSongFile;
			taskCount = aTaskCount;
		}

		@Override
		public Song call() throws Exception {

			Song song = null;

			try {
				song = libraryService.importSong(library, songFile);
			} catch (Exception e) {
				logWarn("libraryScanService.songImportFailed", "Could not import song from file [" + songFile.getFile().getAbsolutePath() + "]", e, songFile.getFile().getAbsolutePath());
			}

			double progress = completedImportTaskCount.incrementAndGet() / (double) taskCount;

			statusReference.set(new StatusImpl(targetFolders, 2, "importingSongs", progress));

			synchronized (delegatesLock) {
				for (Delegate next : new ArrayList<>(delegates)) {
					try {
						next.onScanProgress(getStatus());
					} catch (Exception e) {
						log.error("Exception thrown when delegating onScanProgress", e);
					}
				}
			}

			return song;
		}
	}
}
