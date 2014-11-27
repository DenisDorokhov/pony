package net.dorokhov.pony.core.library;

import net.dorokhov.pony.core.domain.ScanResult;
import net.dorokhov.pony.core.domain.ScanType;
import net.dorokhov.pony.core.library.exception.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.File;
import java.util.List;

public interface ScanService {

	public void addDelegate(Delegate aDelegate);

	public void removeDelegate(Delegate aDelegate);

	public Status getStatus();

	public Page<ScanResult> getAll(Pageable aPageable);

	public ScanResult getById(Long aId);

	public ScanResult scan(List<File> aTargetFolders) throws FileNotFoundException, NotFolderException, ConcurrentScanException;

	public ScanResult edit(List<ScanEditCommand> aCommands) throws SongNotFoundException, FileNotFoundException, NotSongException, ConcurrentScanException;

	public static interface Status {

		public ScanType getScanType();

		public List<File> getFiles();

		public int getStep();

		public int getTotalSteps();

		public String getStepCode();

		public double getProgress();
	}

	public static interface Delegate {

		public void onScanStart(ScanType aType, List<File> aTargetFiles);

		public void onScanProgress(Status aStatus);

		public void onScanFinish(ScanResult aResult);

		public void onScanFail(Throwable aThrowable);
	}

}
