package net.dorokhov.pony.core.library;

import net.dorokhov.pony.core.entity.ScanResult;
import net.dorokhov.pony.core.library.exception.ConcurrentScanException;

import java.io.File;
import java.util.List;

public interface LibraryScanService {

	public void addDelegate(Delegate aDelegate);

	public void removeDelegate(Delegate aDelegate);

	public Status getStatus();

	public ScanResult getLastResult();

	public ScanResult scan(List<File> aTargetFolders) throws ConcurrentScanException;

	public static interface Status {

		public List<File> getTargetFolders();

		public int getStep();

		public int getTotalSteps();

		public String getStepCode();

		public double getProgress();
	}

	public static interface Delegate {

		public void onScanStart(List<File> aTargetFolders);

		public void onScanProgress(Status aStatus);

		public void onScanFinish(ScanResult aResult);

		public void onScanFail(Throwable aThrowable);
	}

}
