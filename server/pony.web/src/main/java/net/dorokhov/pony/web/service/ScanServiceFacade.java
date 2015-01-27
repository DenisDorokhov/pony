package net.dorokhov.pony.web.service;

import net.dorokhov.pony.core.library.exception.LibraryNotDefinedException;
import net.dorokhov.pony.web.domain.ListDto;
import net.dorokhov.pony.web.domain.ScanJobDto;
import net.dorokhov.pony.web.domain.ScanResultDto;
import net.dorokhov.pony.web.domain.ScanStatusDto;
import net.dorokhov.pony.web.domain.command.ScanEditCommandDto;
import net.dorokhov.pony.web.exception.ArtworkUploadNotFoundException;
import net.dorokhov.pony.web.exception.InvalidRequestException;
import net.dorokhov.pony.web.exception.ObjectNotFoundException;

public interface ScanServiceFacade {

	public ScanJobDto startScanJob() throws LibraryNotDefinedException;
	public ScanJobDto startEditJob(ScanEditCommandDto aCommand) throws ArtworkUploadNotFoundException;

	public ListDto<ScanJobDto> getScanJobs(int aPageNumber, int aPageSize) throws InvalidRequestException;

	public ScanJobDto getScanJob(Long aId) throws ObjectNotFoundException;

	public ListDto<ScanResultDto> getScanResults(int aPageNumber, int aPageSize) throws InvalidRequestException;

	public ScanResultDto getScanResult(Long aId) throws ObjectNotFoundException;

	public ScanStatusDto getScanStatus();

}
