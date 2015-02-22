package net.dorokhov.pony.web.shared;

import java.util.Date;

public class ScanJobDto extends AbstractDto<Long> {

	public static enum Status {
		STARTING, STARTED, COMPLETE, FAILED, INTERRUPTED
	}

	private Date creationDate;

	private Date updateDate;

	private ScanTypeDto scanType;

	private Status status;

	private LogMessageDto logMessage;

	private ScanResultDto scanResult;

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date aCreationDate) {
		creationDate = aCreationDate;
	}

	public Date getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(Date aUpdateDate) {
		updateDate = aUpdateDate;
	}

	public ScanTypeDto getScanType() {
		return scanType;
	}

	public void setScanType(ScanTypeDto aScanType) {
		scanType = aScanType;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status aStatus) {
		status = aStatus;
	}

	public LogMessageDto getLogMessage() {
		return logMessage;
	}

	public void setLogMessage(LogMessageDto aLogMessage) {
		logMessage = aLogMessage;
	}

	public ScanResultDto getScanResult() {
		return scanResult;
	}

	public void setScanResult(ScanResultDto aScanResult) {
		scanResult = aScanResult;
	}

}
