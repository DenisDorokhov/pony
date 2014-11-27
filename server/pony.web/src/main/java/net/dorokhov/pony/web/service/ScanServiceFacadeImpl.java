package net.dorokhov.pony.web.service;

import net.dorokhov.pony.core.domain.ScanJob;
import net.dorokhov.pony.core.domain.ScanResult;
import net.dorokhov.pony.core.library.ScanJobService;
import net.dorokhov.pony.core.library.ScanService;
import net.dorokhov.pony.core.library.exception.LibraryNotDefinedException;
import net.dorokhov.pony.web.domain.ListDto;
import net.dorokhov.pony.web.domain.ScanJobDto;
import net.dorokhov.pony.web.domain.ScanResultDto;
import net.dorokhov.pony.web.domain.ScanStatusDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ScanServiceFacadeImpl implements ScanServiceFacade {

	private static final int MAX_PAGE_SIZE = 100;

	private ScanJobService scanJobService;

	private ScanService scanService;

	private DtoConverter dtoConverter;

	@Autowired
	public void setScanJobService(ScanJobService aScanJobService) {
		scanJobService = aScanJobService;
	}

	@Autowired
	public void setScanService(ScanService aScanService) {
		scanService = aScanService;
	}

	@Autowired
	public void setDtoConverter(DtoConverter aDtoConverter) {
		dtoConverter = aDtoConverter;
	}

	@Override
	@Transactional(readOnly = true)
	public ScanJobDto startScanJob() throws LibraryNotDefinedException {
		return dtoConverter.scanJobToDto(scanJobService.startScanJob());
	}

	@Override
	@Transactional(readOnly = true)
	public ListDto<ScanJobDto> getScanJobs(int aPageNumber, int aPageSize) {

		aPageNumber = Math.max(aPageNumber, 0);
		aPageSize = Math.min(aPageSize, MAX_PAGE_SIZE);

		Page<ScanJob> page = scanJobService.getAll(new PageRequest(aPageNumber, aPageSize, Sort.Direction.DESC, "updateDate"));

		return dtoConverter.pageToListDto(page, new DtoConverter.ItemConverter<ScanJob, ScanJobDto>() {
			@Override
			public ScanJobDto convert(ScanJob aItem) {
				return dtoConverter.scanJobToDto(aItem);
			}
		});
	}

	@Override
	@Transactional(readOnly = true)
	public ScanJobDto getScanJobById(Long aId) {

		ScanJob job = scanJobService.getById(aId);

		return job != null ? dtoConverter.scanJobToDto(job) : null;
	}

	@Override
	@Transactional(readOnly = true)
	public ListDto<ScanResultDto> getScanResults(int aPageNumber, int aPageSize) {

		aPageNumber = Math.max(aPageNumber, 0);
		aPageSize = Math.min(aPageSize, MAX_PAGE_SIZE);

		Page<ScanResult> page = scanService.getAll(new PageRequest(aPageNumber, aPageSize, Sort.Direction.DESC, "date"));

		return dtoConverter.pageToListDto(page, new DtoConverter.ItemConverter<ScanResult, ScanResultDto>() {
			@Override
			public ScanResultDto convert(ScanResult aItem) {
				return dtoConverter.scanResultToDto(aItem);
			}
		});
	}

	@Override
	@Transactional(readOnly = true)
	public ScanResultDto getScanResultById(Long aId) {

		ScanResult result = scanService.getById(aId);

		return result != null ? dtoConverter.scanResultToDto(result) : null;
	}

	@Override
	@Transactional(readOnly = true)
	public ScanStatusDto getScanStatus() {

		ScanService.Status status = scanService.getStatus();

		return status != null ? dtoConverter.scanStatusToDto(status) : null;
	}
}
