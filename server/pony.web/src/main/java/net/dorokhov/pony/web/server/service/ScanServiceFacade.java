package net.dorokhov.pony.web.server.service;

import net.dorokhov.pony.core.library.exception.LibraryNotDefinedException;
import net.dorokhov.pony.web.shared.ListDto;
import net.dorokhov.pony.web.shared.ScanJobDto;
import net.dorokhov.pony.web.shared.ScanResultDto;
import net.dorokhov.pony.web.shared.ScanStatusDto;
import net.dorokhov.pony.web.shared.command.ScanEditCommandDto;
import net.dorokhov.pony.web.server.exception.ArtworkUploadNotFoundException;
import net.dorokhov.pony.web.server.exception.InvalidArgumentException;
import net.dorokhov.pony.web.server.exception.ObjectNotFoundException;

public interface ScanServiceFacade {

	public ScanJobDto startScanJob() throws LibraryNotDefinedException;
	public ScanJobDto startEditJob(ScanEditCommandDto aCommand) throws ArtworkUploadNotFoundException;

	public ListDto<ScanJobDto> getScanJobs(int aPageNumber, int aPageSize) throws InvalidArgumentException;

	public ScanJobDto getScanJob(Long aId) throws ObjectNotFoundException;

	public ListDto<ScanResultDto> getScanResults(int aPageNumber, int aPageSize) throws InvalidArgumentException;

	public ScanResultDto getScanResult(Long aId) throws ObjectNotFoundException;

	public ScanStatusDto getScanStatus();

}
