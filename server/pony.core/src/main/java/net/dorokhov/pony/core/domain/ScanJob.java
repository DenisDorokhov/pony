package net.dorokhov.pony.core.domain;

import net.dorokhov.pony.core.domain.common.BaseEntity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "scan_job")
public class ScanJob extends BaseEntity<Long> {

	public static enum Status {
		STARTING, STARTED, COMPLETE, FAILED, INTERRUPTED
	}

	private ScanType type;

	private Status status;

	private LogMessage logMessage;

	private ScanResult scanResult;

	@Column(name = "type")
	@Enumerated(EnumType.STRING)
	@NotNull
	public ScanType getType() {
		return type;
	}

	public void setType(ScanType aType) {
		type = aType;
	}

	@Column(name = "status")
	@Enumerated(EnumType.STRING)
	@NotNull
	public Status getStatus() {
		return status;
	}

	public void setStatus(Status aStatus) {
		status = aStatus;
	}

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "log_message_id", unique = true)
	public LogMessage getLogMessage() {
		return logMessage;
	}

	public void setLogMessage(LogMessage aLogMessage) {
		logMessage = aLogMessage;
	}

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "scan_result_id", unique = true)
	public ScanResult getScanResult() {
		return scanResult;
	}

	public void setScanResult(ScanResult aScanResult) {
		scanResult = aScanResult;
	}
}
