package net.dorokhov.pony.web.domain;

import net.dorokhov.pony.core.domain.ScanJob;
import net.dorokhov.pony.core.domain.ScanType;

import java.io.Serializable;
import java.util.Date;

public class ScanJobDto implements Serializable {

	private Long id;

	private Date creationDate;

	private Date updateDate;

	private ScanType scanType;

	private ScanJob.Status status;

	private LogMessageDto logMessage;

	private ScanResultDto scanResult;

	public Long getId() {
		return id;
	}

	public void setId(Long aId) {
		id = aId;
	}

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

	public ScanType getScanType() {
		return scanType;
	}

	public void setScanType(ScanType aScanType) {
		scanType = aScanType;
	}

	public ScanJob.Status getStatus() {
		return status;
	}

	public void setStatus(ScanJob.Status aStatus) {
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
