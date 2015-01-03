package net.dorokhov.pony.web.service;

import net.dorokhov.pony.core.library.exception.LibraryNotDefinedException;
import net.dorokhov.pony.web.domain.ListDto;
import net.dorokhov.pony.web.domain.ScanJobDto;
import net.dorokhov.pony.web.domain.ScanResultDto;
import net.dorokhov.pony.web.domain.ScanStatusDto;
import net.dorokhov.pony.web.domain.command.ScanEditCommandDto;
import net.dorokhov.pony.web.exception.ArtworkUploadNotFoundException;

public interface ScanServiceFacade {

	public ScanJobDto startScanJob() throws LibraryNotDefinedException;
	public ScanJobDto startEditJob(ScanEditCommandDto aCommand) throws ArtworkUploadNotFoundException;

	public ListDto<ScanJobDto> getScanJobs(int aPageNumber, int aPageSize);

	public ScanJobDto getScanJob(Long aId);

	public ListDto<ScanResultDto> getScanResults(int aPageNumber, int aPageSize);

	public ScanResultDto getScanResult(Long aId);

	public ScanStatusDto getScanStatus();

}
