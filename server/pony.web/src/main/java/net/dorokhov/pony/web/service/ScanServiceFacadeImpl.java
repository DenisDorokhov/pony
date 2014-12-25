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

	@Autowired
	public void setScanJobService(ScanJobService aScanJobService) {
		scanJobService = aScanJobService;
	}

	@Autowired
	public void setScanService(ScanService aScanService) {
		scanService = aScanService;
	}

	@Override
	@Transactional(readOnly = true)
	public ScanJobDto startScanJob() throws LibraryNotDefinedException {
		return ScanJobDto.valueOf(scanJobService.startScanJob());
	}

	@Override
	@Transactional(readOnly = true)
	public ListDto<ScanJobDto> getScanJobs(int aPageNumber, int aPageSize) {

		aPageNumber = Math.max(aPageNumber, 0);
		aPageSize = Math.min(aPageSize, MAX_PAGE_SIZE);

		Page<ScanJob> page = scanJobService.getAll(new PageRequest(aPageNumber, aPageSize, Sort.Direction.DESC, "updateDate"));

		return ListDto.valueOf(page, new ListDto.ContentConverter<ScanJob, ScanJobDto>() {
			@Override
			public ScanJobDto convert(ScanJob aItem) {
				return ScanJobDto.valueOf(aItem);
			}
		});
	}

	@Override
	@Transactional(readOnly = true)
	public ScanJobDto getScanJob(Long aId) {

		ScanJob job = scanJobService.getById(aId);

		return job != null ? ScanJobDto.valueOf(job) : null;
	}

	@Override
	@Transactional(readOnly = true)
	public ListDto<ScanResultDto> getScanResults(int aPageNumber, int aPageSize) {

		aPageNumber = Math.max(aPageNumber, 0);
		aPageSize = Math.min(aPageSize, MAX_PAGE_SIZE);

		Page<ScanResult> page = scanService.getAll(new PageRequest(aPageNumber, aPageSize, Sort.Direction.DESC, "date"));

		return ListDto.valueOf(page, new ListDto.ContentConverter<ScanResult, ScanResultDto>() {
			@Override
			public ScanResultDto convert(ScanResult aItem) {
				return ScanResultDto.valueOf(aItem);
			}
		});
	}

	@Override
	@Transactional(readOnly = true)
	public ScanResultDto getScanResult(Long aId) {

		ScanResult result = scanService.getById(aId);

		return result != null ? ScanResultDto.valueOf(result) : null;
	}

	@Override
	@Transactional(readOnly = true)
	public ScanStatusDto getScanStatus() {

		ScanService.Status status = scanService.getStatus();

		return status != null ? ScanStatusDto.valueOf(status) : null;
	}

}
