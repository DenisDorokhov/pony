package net.dorokhov.pony.web.server.service;

import net.dorokhov.pony.core.library.exception.LibraryNotDefinedException;
import net.dorokhov.pony.web.server.exception.ArtworkUploadNotFoundException;
import net.dorokhov.pony.web.server.exception.InvalidArgumentException;
import net.dorokhov.pony.web.server.exception.ObjectNotFoundException;
import net.dorokhov.pony.web.shared.ScanJobDto;
import net.dorokhov.pony.web.shared.ScanResultDto;
import net.dorokhov.pony.web.shared.ScanStatusDto;
import net.dorokhov.pony.web.shared.command.ScanEditCommandDto;
import net.dorokhov.pony.web.shared.list.ScanJobListDto;
import net.dorokhov.pony.web.shared.list.ScanResultListDto;

public interface ScanServiceFacade {

	public ScanJobDto startScanJob() throws LibraryNotDefinedException;
	public ScanJobDto startEditJob(ScanEditCommandDto aCommand) throws ArtworkUploadNotFoundException;

	public ScanJobListDto getScanJobs(int aPageNumber, int aPageSize) throws InvalidArgumentException;

	public ScanJobDto getScanJob(Long aId) throws ObjectNotFoundException;

	public ScanResultListDto getScanResults(int aPageNumber, int aPageSize) throws InvalidArgumentException;

	public ScanResultDto getScanResult(Long aId) throws ObjectNotFoundException;

	public ScanStatusDto getScanStatus();

}
