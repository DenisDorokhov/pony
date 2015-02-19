package net.dorokhov.pony.web.server.service;

import net.dorokhov.pony.core.library.exception.LibraryNotDefinedException;
import net.dorokhov.pony.web.server.exception.ArtworkUploadNotFoundException;
import net.dorokhov.pony.web.server.exception.InvalidArgumentException;
import net.dorokhov.pony.web.server.exception.ObjectNotFoundException;
import net.dorokhov.pony.web.shared.PagedListDto;
import net.dorokhov.pony.web.shared.ScanJobDto;
import net.dorokhov.pony.web.shared.ScanResultDto;
import net.dorokhov.pony.web.shared.ScanStatusDto;
import net.dorokhov.pony.web.shared.command.ScanEditCommandDto;

public interface ScanServiceFacade {

	public ScanJobDto startScanJob() throws LibraryNotDefinedException;
	public ScanJobDto startEditJob(ScanEditCommandDto aCommand) throws ArtworkUploadNotFoundException;

	public PagedListDto<ScanJobDto> getScanJobs(int aPageNumber, int aPageSize) throws InvalidArgumentException;

	public ScanJobDto getScanJob(Long aId) throws ObjectNotFoundException;

	public PagedListDto<ScanResultDto> getScanResults(int aPageNumber, int aPageSize) throws InvalidArgumentException;

	public ScanResultDto getScanResult(Long aId) throws ObjectNotFoundException;

	public ScanStatusDto getScanStatus();

}
