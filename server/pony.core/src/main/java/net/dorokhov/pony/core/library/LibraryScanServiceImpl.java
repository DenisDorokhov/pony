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
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
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
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.PreDestroy;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class LibraryScanServiceImpl implements LibraryScanService {

	private final static int NUMBER_OF_SCAN_THREADS = 10;
	private final static int NUMBER_OF_SCAN_STEPS = 6;

	private final static int STEP_SCAN_PREPARING = 1;
	private final static int STEP_SCAN_SEARCHING_MEDIA_FILES = 2;
	private final static int STEP_SCAN_CLEANING_SONGS = 3;
	private final static int STEP_SCAN_CLEANING_ARTWORKS = 4;
	private final static int STEP_SCAN_IMPORTING_SONGS = 5;
	private final static int STEP_SCAN_NORMALIZING = 6;

	private final static String STEP_CODE_SCAN_PREPARING = "preparing";
	private final static String STEP_CODE_SCAN_SEARCHING_MEDIA_FILES = "searchingMediaFiles";
	private final static String STEP_CODE_SCAN_CLEANING_SONGS = "cleaningSongs";
	private final static String STEP_CODE_SCAN_CLEANING_ARTWORKS = "cleaningArtworks";
	private final static String STEP_CODE_SCAN_IMPORTING_SONGS = "importingSongs";
	private final static String STEP_CODE_SCAN_NORMALIZING = "normalizing";

	private final static int NUMBER_OF_EDIT_THREADS = 10;
	private final static int NUMBER_OF_EDIT_STEPS = 3;

	private final static int STEP_EDIT_PREPARING = 1;
	private final static int STEP_EDIT_WRITING_SONGS = 2;
	private final static int STEP_EDIT_NORMALIZING = 3;

	private final static String STEP_CODE_EDIT_PREPARING = "preparing";
	private final static String STEP_CODE_EDIT_WRITING_SONGS = "writingSongs";
	private final static String STEP_CODE_EDIT_NORMALIZING = "normalizing";

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
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public ScanResult scan(final List<File> aTargetFolders) throws ConcurrentScanException {

		synchronized (statusCheckLock) {

			if (statusReference.get() != null) {
				throw new ConcurrentScanException();
			}

			statusReference.set(StatusImpl.buildScanStatus(aTargetFolders, STEP_SCAN_PREPARING, STEP_CODE_SCAN_PREPARING, -1));
		}

		logService.info(log, "libraryScanService.scanStarted", "Scanning library " + aTargetFolders + "...",
				Arrays.asList(aTargetFolders.toString()));

		synchronized (delegatesLock) {
			for (Delegate next : new ArrayList<>(delegates)) {
				try {
					next.onScanStart(Status.Action.SCAN, new ArrayList<>(aTargetFolders));
				} catch (Exception e) {
					log.error("Exception thrown when delegating onScanStart to " + next, e);
				}
			}
		}

		updateStatus(statusReference.get());

		ScanResult scanResult;

		try {

			executorReference.set(Executors.newFixedThreadPool(NUMBER_OF_SCAN_THREADS));

			scanResult = transactionTemplate.execute(new TransactionCallback<ScanResult>() {
				@Override
				public ScanResult doInTransaction(TransactionStatus status) {
					return doScan(aTargetFolders);
				}
			});

			logService.info(log, "libraryScanService.scanFinished", "Scan of " + scanResult.getPaths() + " has been finished with result " + scanResult.toString() + ".",
					Arrays.asList(StringUtils.join(scanResult.getPaths(), ", "), scanResult.toString()));

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

			logService.error(log, "libraryScanService.scanFailed", "Scan failed.", scanException);

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
			completedImportTaskCount.set(0);
			statusReference.set(null);
		}

		return scanResult;
	}

	@Override
	public ScanResult edit(final LibraryScanEditCommand aCommand) throws ConcurrentScanException {

		List<File> targetFiles = new ArrayList<>();
		for (LibraryScanEditCommand.Task task : aCommand.getTasks()) {
			targetFiles.add(task.getFile());
		}

		synchronized (statusCheckLock) {

			if (statusReference.get() != null) {
				throw new ConcurrentScanException();
			}

			statusReference.set(StatusImpl.buildEditStatus(targetFiles, STEP_EDIT_PREPARING, STEP_CODE_EDIT_PREPARING, -1));
		}

		logService.info(log, "libraryScanService.editStarted", "Editing files " + targetFiles + "...",
				Arrays.asList(targetFiles.toString()));

		synchronized (delegatesLock) {
			for (Delegate next : new ArrayList<>(delegates)) {
				try {
					next.onScanStart(Status.Action.EDIT, new ArrayList<>(targetFiles));
				} catch (Exception e) {
					log.error("Exception thrown when delegating onScanStart to " + next, e);
				}
			}
		}

		updateStatus(statusReference.get());

		ScanResult scanResult;

		try {

			executorReference.set(Executors.newFixedThreadPool(NUMBER_OF_SCAN_THREADS));

			scanResult = transactionTemplate.execute(new TransactionCallback<ScanResult>() {
				@Override
				public ScanResult doInTransaction(TransactionStatus status) {
					return doEdit(aCommand);
				}
			});

			logService.info(log, "libraryScanService.editFinished", "Edit of files " + scanResult.getPaths() + " has been finished with result " + scanResult.toString() + ".",
					Arrays.asList(StringUtils.join(scanResult.getPaths(), ", "), scanResult.toString()));

			synchronized (delegatesLock) {
				for (Delegate next : new ArrayList<>(delegates)) {
					try {
						next.onScanFinish(scanResult);
					} catch (Exception e) {
						log.error("Exception thrown when delegating onScanFinish to " + next, e);
					}
				}
			}

		} catch (final Exception editException) {

			logService.error(log, "libraryScanService.editFailed", "Edit failed.", editException);

			synchronized (delegatesLock) {
				for (Delegate next : new ArrayList<>(delegates)) {
					try {
						next.onScanFail(editException);
					} catch (Exception e) {
						log.error("Exception thrown when delegating onScanFail to " + next, e);
					}
				}
			}

			throw new RuntimeException(editException);

		} finally {
			executorReference.set(null);
			completedImportTaskCount.set(0);
			statusReference.set(null);
		}

		return scanResult;
	}

	private ScanResult doScan(final List<File> aTargetFolders) {

		List<String> targetPaths = new ArrayList<>();
		for (File folder : aTargetFolders) {
			targetPaths.add(folder.getAbsolutePath());
		}

		return calculateScanResult(ScanResult.Type.FULL, targetPaths, new ScanProcessor() {
			@Override
			public List<LibrarySong> process() {
				return performScanSteps(aTargetFolders);
			}
		});
	}

	private ScanResult doEdit(final LibraryScanEditCommand aCommand) {

		List<String> targetPaths = new ArrayList<>();
		for (LibraryScanEditCommand.Task task : aCommand.getTasks()) {
			targetPaths.add(task.getFile().getAbsolutePath());
		}

		return calculateScanResult(ScanResult.Type.EDIT, targetPaths, new ScanProcessor() {
			@Override
			public List<LibrarySong> process() {
				return performEditSteps(aCommand);
			}
		});
	}

	private List<LibrarySong> performScanSteps(final List<File> aTargetFolders) {

		logService.info(log, "libraryScanService.searchingMediaFiles", "Searching media files...");
		updateStatus(StatusImpl.buildScanStatus(aTargetFolders, STEP_SCAN_SEARCHING_MEDIA_FILES, STEP_CODE_SCAN_SEARCHING_MEDIA_FILES, -1.0));
		List<LibraryFolder> library = new ArrayList<>();
		for (File targetFolder : aTargetFolders) {
			library.add(fileScanService.scanFolder(targetFolder));
		}

		logService.info(log, "libraryScanService.cleaningSongs", "Cleaning songs...");
		updateStatus(StatusImpl.buildScanStatus(aTargetFolders, STEP_SCAN_CLEANING_SONGS, STEP_CODE_SCAN_CLEANING_SONGS, 0.0));
		libraryService.cleanSongs(library, new LibraryService.ProgressDelegate() {
			@Override
			public void onProgress(double aProgress) {
				updateStatus(StatusImpl.buildScanStatus(aTargetFolders, STEP_SCAN_CLEANING_SONGS, STEP_CODE_SCAN_CLEANING_SONGS, aProgress));
			}
		});

		logService.info(log, "libraryScanService.cleaningArtworks", "Cleaning artworks...");
		updateStatus(StatusImpl.buildScanStatus(aTargetFolders, STEP_SCAN_CLEANING_ARTWORKS, STEP_CODE_SCAN_CLEANING_ARTWORKS, 0.0));
		libraryService.cleanArtworks(library, new LibraryService.ProgressDelegate() {
			@Override
			public void onProgress(double aProgress) {
				updateStatus(StatusImpl.buildScanStatus(aTargetFolders, STEP_SCAN_CLEANING_ARTWORKS, STEP_CODE_SCAN_CLEANING_ARTWORKS, aProgress));
			}
		});

		logService.info(log, "libraryScanService.importingSongs", "Importing songs...");
		updateStatus(StatusImpl.buildScanStatus(aTargetFolders, STEP_SCAN_IMPORTING_SONGS, STEP_CODE_SCAN_IMPORTING_SONGS, 0.0));

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

		logService.info(log, "libraryScanService.normalizing", "Normalizing...");
		updateStatus(StatusImpl.buildScanStatus(aTargetFolders, STEP_SCAN_NORMALIZING, STEP_CODE_SCAN_NORMALIZING, 0.0));
		libraryService.normalize(library, new LibraryService.ProgressDelegate() {
			@Override
			public void onProgress(double aProgress) {
				updateStatus(StatusImpl.buildScanStatus(aTargetFolders, STEP_SCAN_NORMALIZING, STEP_CODE_SCAN_NORMALIZING, aProgress));
			}
		});

		return songFiles;
	}

	private List<LibrarySong> performEditSteps(LibraryScanEditCommand aCommand) {
		return null; // TODO: implement
	}

	private void updateStatus(StatusImpl aStatus) {

		statusReference.set(aStatus);

		synchronized (delegatesLock) {
			for (Delegate next : new ArrayList<>(delegates)) {
				try {
					next.onScanProgress(aStatus);
				} catch (Exception e) {
					log.error("Exception thrown when delegating onScanProgress to " + next, e);
				}
			}
		}
	}

	private ScanResult calculateScanResult(ScanResult.Type aType, List<String> aPaths, ScanProcessor aProcessor) {

		ScanResult lastScan = getLastResult();
		Date lastScanDate = (lastScan != null ? lastScan.getDate() : new Date(0L));

		long songCountBeforeScan = songDao.count();
		long genreCountBeforeScan = genreDao.count();
		long artistCountBeforeScan = artistDao.count();
		long albumCountBeforeScan = albumDao.count();
		long artworkCountBeforeScan = storedFileService.getCountByTag(StoredFile.TAG_ARTWORK_EMBEDDED) + storedFileService.getCountByTag(StoredFile.TAG_ARTWORK_FILE);

		long startTime = System.nanoTime();

		List<LibrarySong> songFiles = aProcessor.process();

		long endTime = System.nanoTime();

		long songCountAfterScan = songDao.count();
		long songCountCreated = songDao.countByCreationDateGreaterThan(lastScanDate);
		long songCountUpdated = songDao.countByCreationDateLessThanAndUpdateDateGreaterThan(lastScanDate, lastScanDate);
		long songCountDeleted = Math.max(0, songCountBeforeScan - (songCountAfterScan - songCountCreated));

		long genreCountAfterScan = genreDao.count();
		long genreCountCreated = genreDao.countByCreationDateGreaterThan(lastScanDate);
		long genreCountUpdated = genreDao.countByCreationDateLessThanAndUpdateDateGreaterThan(lastScanDate, lastScanDate);
		long genreCountDeleted = Math.max(0, genreCountBeforeScan - (genreCountAfterScan - genreCountCreated));

		long artistCountAfterScan = artistDao.count();
		long artistCountCreated = artistDao.countByCreationDateGreaterThan(lastScanDate);
		long artistCountUpdated = artistDao.countByCreationDateLessThanAndUpdateDateGreaterThan(lastScanDate, lastScanDate);
		long artistCountDeleted = Math.max(0, artistCountBeforeScan - (artistCountAfterScan - artistCountCreated));

		long albumCountAfterScan = albumDao.count();
		long albumCountCreated = albumDao.countByCreationDateGreaterThan(lastScanDate);
		long albumCountUpdated = albumDao.countByCreationDateLessThanAndUpdateDateGreaterThan(lastScanDate, lastScanDate);
		long albumCountDeleted = Math.max(0, albumCountBeforeScan - (albumCountAfterScan - albumCountCreated));

		long artworkCountAfterScan = storedFileService.getCountByTag(StoredFile.TAG_ARTWORK_EMBEDDED) + storedFileService.getCountByTag(StoredFile.TAG_ARTWORK_FILE);
		long artworkCountCreated = storedFileService.getCountByTagAndMinimalDate(StoredFile.TAG_ARTWORK_EMBEDDED, lastScanDate) +
				storedFileService.getCountByTagAndMinimalDate(StoredFile.TAG_ARTWORK_FILE, lastScanDate);
		long artworkCountDeleted = Math.max(0, artworkCountBeforeScan - (artworkCountAfterScan - artworkCountCreated));

		ScanResult scanResult = new ScanResult();

		scanResult.setPaths(aPaths);
		scanResult.setType(aType);
		scanResult.setDuration(endTime - startTime);

		scanResult.setSongSize(ObjectUtils.defaultIfNull(songDao.sumSize(), 0L));
		scanResult.setArtworkSize(storedFileService.getSizeByTag(StoredFile.TAG_ARTWORK_EMBEDDED) + storedFileService.getSizeByTag(StoredFile.TAG_ARTWORK_FILE));

		scanResult.setGenreCount(genreCountAfterScan);
		scanResult.setArtistCount(artistCountAfterScan);
		scanResult.setAlbumCount(albumCountAfterScan);
		scanResult.setSongCount(songCountAfterScan);
		scanResult.setArtworkCount(artworkCountAfterScan);

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
		scanResult.setDeletedArtworkCount(artworkCountDeleted);

		return scanResultDao.save(scanResult);
	}

	private interface ScanProcessor {
		public List<LibrarySong> process();
	}

	private static class StatusImpl implements Status {

		private final Action action;
		private final List<File> files;
		private final int step;
		private final String stepCode;
		private final int totalSteps;
		private final double progress;

		public StatusImpl(Action aAction, List<File> aFiles, int aStep, String aStepCode, int aTotalSteps, double aProgress) {
			action = aAction;
			files = aFiles != null ? new ArrayList<>(aFiles) : null;
			step = aStep;
			stepCode = aStepCode;
			totalSteps = aTotalSteps;
			progress = aProgress;
		}

		public StatusImpl(Status aStatus) {
			this(aStatus.getAction(), aStatus.getFiles(), aStatus.getStep(), aStatus.getStepCode(), aStatus.getTotalSteps(), aStatus.getProgress());
		}

		@Override
		public Action getAction() {
			return action;
		}

		@Override
		public List<File> getFiles() {
			return files != null ? new ArrayList<>(files) : null;
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
			return totalSteps;
		}

		public static StatusImpl buildScanStatus(List<File> aFiles, int aStep, String aStepCode, double aProgress) {
			return new StatusImpl(Action.SCAN, aFiles, aStep, aStepCode, NUMBER_OF_SCAN_STEPS, aProgress);
		}

		public static StatusImpl buildEditStatus(List<File> aFiles, int aStep, String aStepCode, double aProgress) {
			return new StatusImpl(Action.EDIT, aFiles, aStep, aStepCode, NUMBER_OF_EDIT_STEPS, aProgress);
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
				logService.warn(log, "libraryScanService.songImportFailed", "Could not import song from file [" + songFile.getFile().getAbsolutePath() + "].",
						e, Arrays.asList(songFile.getFile().getAbsolutePath()));
			}

			double progress = completedImportTaskCount.incrementAndGet() / (double) taskCount;

			updateStatus(StatusImpl.buildScanStatus(targetFolders, STEP_SCAN_IMPORTING_SONGS, STEP_CODE_SCAN_IMPORTING_SONGS, progress));

			return song;
		}
	}
}
