package net.dorokhov.pony.core.library;

import net.dorokhov.pony.core.installation.InstallationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;

@Service
public class ScanJobScheduler {

	private InstallationService installationService;

	private ScanJobService scanJobService;

	@Autowired
	public void setInstallationService(InstallationService aInstallationService) {
		installationService = aInstallationService;
	}

	@Autowired
	public void setScanJobService(ScanJobService aScanJobService) {
		scanJobService = aScanJobService;
	}

	@PostConstruct
	@Transactional
	public void interruptCurrentJobs() {
		if (installationService.getInstallation() != null) {
			scanJobService.interruptCurrentJobs();
		}
	}

	@Transactional
	@Scheduled(fixedDelay = 60 * 60 * 1000, initialDelay = 5 * 60 * 1000)
	synchronized public void startAutoScanJob() {
		if (installationService.getInstallation() != null) {
			scanJobService.startAutoScanJob();
		}
	}

}
