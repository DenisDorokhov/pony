package net.dorokhov.pony.core.user;

import net.dorokhov.pony.core.installation.InstallationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserServiceScheduler {

	private InstallationService installationService;

	private UserService userService;

	@Autowired
	public void setInstallationService(InstallationService aInstallationService) {
		installationService = aInstallationService;
	}

	@Autowired
	public void setUserService(UserService aUserService) {
		userService = aUserService;
	}

	@Transactional
	@Scheduled(fixedDelay = 24 * 60 * 60 * 1000)
	public void cleanTokens() {
		if (installationService.getInstallation() != null) {
			userService.cleanTokens();
		}
	}

}
