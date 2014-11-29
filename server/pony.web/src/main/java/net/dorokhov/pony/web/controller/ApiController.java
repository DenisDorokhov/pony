package net.dorokhov.pony.web.controller;

import net.dorokhov.pony.web.domain.*;
import net.dorokhov.pony.web.exception.ObjectNotFoundException;
import net.dorokhov.pony.web.service.InstallationServiceFacade;
import net.dorokhov.pony.web.service.ResponseBuilder;
import net.dorokhov.pony.web.service.ScanServiceFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api", produces = "application/json")
public class ApiController {

	private ResponseBuilder responseBuilder;

	private InstallationServiceFacade installationServiceFacade;

	private ScanServiceFacade scanServiceFacade;

	@Autowired
	public void setResponseBuilder(ResponseBuilder aResponseBuilder) {
		responseBuilder = aResponseBuilder;
	}

	@Autowired
	public void setInstallationServiceFacade(InstallationServiceFacade aInstallationServiceFacade) {
		installationServiceFacade = aInstallationServiceFacade;
	}

	@Autowired
	public void setScanServiceFacade(ScanServiceFacade aScanServiceFacade) {
		scanServiceFacade = aScanServiceFacade;
	}

	@RequestMapping(value = "/installation", method = RequestMethod.GET)
	public ResponseDto<InstallationDto> getInstallation() {
		return responseBuilder.build(installationServiceFacade.getInstallation());
	}

	@RequestMapping(value = "/scanJobs", method = RequestMethod.GET)
	public ResponseDto<ListDto<ScanJobDto>> getScanJobs(@RequestParam(value = "pageNumber", defaultValue = "0") int aPageNumber,
															   @RequestParam(value = "pageSize", defaultValue = "10") int aPageSize) {
		return responseBuilder.build(scanServiceFacade.getScanJobs(aPageNumber, aPageSize));
	}

	@RequestMapping(value = "/scanJobs/{id}", method = RequestMethod.GET)
	public ResponseDto<ScanJobDto> getScanJob(@PathVariable("id") Long aId) {

		ScanJobDto job = scanServiceFacade.getScanJobById(aId);

		if (job == null) {
			throw new ObjectNotFoundException(aId, "scanJobNotFound", "Scan job [" + aId + "] could not be found.");
		}

		return responseBuilder.build(job);
	}

	@RequestMapping(value = "/scanJobs", method = RequestMethod.POST)
	public ResponseDto<ScanJobDto> startScanJob() {
		return responseBuilder.build(scanServiceFacade.startScanJob());
	}

	@RequestMapping(value = "/scanResults", method = RequestMethod.GET)
	public ResponseDto<ListDto<ScanResultDto>> getScanResults(@RequestParam(value = "pageNumber", defaultValue = "0") int aPageNumber,
																	 @RequestParam(value = "pageSize", defaultValue = "10") int aPageSize) {
		return responseBuilder.build(scanServiceFacade.getScanResults(aPageNumber, aPageSize));
	}

	@RequestMapping(value = "/scanResults/{id}", method = RequestMethod.GET)
	public ResponseDto<ScanResultDto> getScanResult(@PathVariable("id") Long aId) {

		ScanResultDto result = scanServiceFacade.getScanResultById(aId);

		if (result == null) {
			throw new ObjectNotFoundException(aId, "scanResultNotFound", "Scan result [" + aId + "] could not be found.");
		}

		return responseBuilder.build(result);
	}

	@RequestMapping(value = "/scanStatus", method = RequestMethod.GET)
	public ResponseDto<ScanStatusDto> getScanStatus() {
		return responseBuilder.build(scanServiceFacade.getScanStatus());
	}

}
