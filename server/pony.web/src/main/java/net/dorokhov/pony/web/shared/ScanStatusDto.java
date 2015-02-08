package net.dorokhov.pony.web.shared;

import java.util.ArrayList;
import java.util.List;

public class ScanStatusDto {

	private ScanTypeDto scanType;

	private List<String> files;

	private int step;

	private int totalSteps;

	private String stepCode;

	private double progress;

	public ScanTypeDto getScanType() {
		return scanType;
	}

	public void setScanType(ScanTypeDto aScanType) {
		scanType = aScanType;
	}

	public List<String> getFiles() {

		if (files == null) {
			files = new ArrayList<>();
		}

		return files;
	}

	public void setFiles(List<String> aFiles) {
		files = aFiles;
	}

	public int getStep() {
		return step;
	}

	public void setStep(int aStep) {
		step = aStep;
	}

	public int getTotalSteps() {
		return totalSteps;
	}

	public void setTotalSteps(int aTotalSteps) {
		totalSteps = aTotalSteps;
	}

	public String getStepCode() {
		return stepCode;
	}

	public void setStepCode(String aStepCode) {
		stepCode = aStepCode;
	}

	public double getProgress() {
		return progress;
	}

	public void setProgress(double aProgress) {
		progress = aProgress;
	}

}
