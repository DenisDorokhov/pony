package net.dorokhov.pony.web.service;

import net.dorokhov.pony.core.library.exception.LibraryNotDefinedException;
import net.dorokhov.pony.web.domain.ListDto;
import net.dorokhov.pony.web.domain.ScanJobDto;
import net.dorokhov.pony.web.domain.ScanResultDto;
import net.dorokhov.pony.web.domain.ScanStatusDto;

public interface ScanServiceFacade {

	public ScanJobDto startScanJob() throws LibraryNotDefinedException;

	public ListDto<ScanJobDto> getScanJobs(int aPageNumber, int aPageSize);

	public ScanJobDto getScanJob(Long aId);

	public ListDto<ScanResultDto> getScanResults(int aPageNumber, int aPageSize);

	public ScanResultDto getScanResult(Long aId);

	public ScanStatusDto getScanStatus();

}
