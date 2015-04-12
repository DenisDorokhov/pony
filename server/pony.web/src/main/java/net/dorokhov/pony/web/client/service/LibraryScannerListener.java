package net.dorokhov.pony.web.client.service;

import com.google.gwt.user.client.Timer;
import com.google.web.bindery.event.shared.EventBus;
import net.dorokhov.pony.web.client.event.RefreshRequestEvent;
import net.dorokhov.pony.web.shared.ScanStatusDto;

import javax.inject.Inject;

public class LibraryScannerListener implements LibraryScanner.Delegate {

	private static final int REFRESH_INTERVAL = 30000;

	private final EventBus eventBus;

	private final AuthenticationManager authenticationManager;

	private Timer refreshTimer;

	@Inject
	public LibraryScannerListener(EventBus aEventBus, LibraryScanner aLibraryScanner, AuthenticationManager aAuthenticationManager) {

		eventBus = aEventBus;
		authenticationManager = aAuthenticationManager;

		aLibraryScanner.addDelegate(this);
	}

	@Override
	public void onScanStarted(LibraryScanner aLibraryScanner) {

		if (refreshTimer != null) {
			refreshTimer.cancel();
		}

		refreshTimer = new Timer() {
			@Override
			public void run() {
				if (authenticationManager.isAuthenticated()) {
					requestRefresh();
				}
			}
		};
		refreshTimer.scheduleRepeating(REFRESH_INTERVAL);

		requestRefresh();
	}

	@Override
	public void onScanProgress(LibraryScanner aLibraryScanner, ScanStatusDto aStatus) {}

	@Override
	public void onScanFinished(LibraryScanner aLibraryScanner) {

		refreshTimer.cancel();

		refreshTimer = null;

		requestRefresh();
	}

	private void requestRefresh() {
		eventBus.fireEvent(new RefreshRequestEvent());
	}

}
