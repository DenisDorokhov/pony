package net.dorokhov.pony.web.client.service;

import com.google.gwt.user.client.Timer;
import net.dorokhov.pony.web.client.service.api.ApiService;
import net.dorokhov.pony.web.client.service.api.MethodCallbackAdapter;
import net.dorokhov.pony.web.client.service.common.OperationCallback;
import net.dorokhov.pony.web.shared.*;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class LibraryScanner {

	public static interface Delegate {

		public void onScanStarted(LibraryScanner aLibraryScanner);

		public void onScanProgress(LibraryScanner aLibraryScanner, ScanStatusDto aStatus);

		public void onScanFinished(LibraryScanner aLibraryScanner);

	}

	private static final int STATUS_TIMER_INTERVAL_NOT_SCANNING = 15000;
	private static final int STATUS_TIMER_INTERVAL_SCANNING = 500;
	private static final int STATUS_TIMER_INTERVAL_FIRST = 500;

	private final Logger log = Logger.getLogger(getClass().getName());

	private final ApiService apiService;

	private final ErrorNotifier errorNotifier;

	private final AuthenticationManager authenticationManager;

	private final List<Delegate> delegates = new ArrayList<>();

	private boolean scanning;

	private ScanStatusDto status;

	private Timer statusTimer;

	@Inject
	public LibraryScanner(ApiService aApiService, ErrorNotifier aErrorNotifier, AuthenticationManager aAuthenticationManager) {

		apiService = aApiService;
		errorNotifier = aErrorNotifier;
		authenticationManager = aAuthenticationManager;

		scheduleStatusTimer(STATUS_TIMER_INTERVAL_FIRST);
	}

	public void addDelegate(Delegate aDelegate) {
		if (!delegates.contains(aDelegate)) {
			delegates.add(aDelegate);
		}
	}

	public void removeDelegate(Delegate aDelegate) {
		delegates.remove(aDelegate);
	}

	public boolean isScanning() {
		return scanning;
	}

	public void scan() {
		if (!isScanning()) {
			doGetStatusAndScan();
		}
	}

	public ScanStatusDto getStatus() {
		return status;
	}

	private void scheduleStatusTimer(int aTime) {

		if (statusTimer != null) {

			statusTimer.cancel();

			statusTimer = null;
		}

		statusTimer = new Timer() {
			@Override
			public void run() {

				statusTimer = null;

				updateStatus();
			}
		};

		statusTimer.schedule(aTime);
	}

	private void doGetStatusAndScan() {

		scanning = true;

		propagateScanStarted();

		apiService.getScanStatus(new MethodCallbackAdapter<>(new OperationCallback<ScanStatusDto>() {

			@Override
			public void onSuccess(ScanStatusDto aStatus) {

				if (aStatus == null) {
					doScan();
				} else {

					propagateScanProgress(status);

					scheduleStatusTimer(STATUS_TIMER_INTERVAL_SCANNING);
				}
			}

			@Override
			public void onError(List<ErrorDto> aErrors) {

				scanning = false;

				errorNotifier.notifyOfErrors(aErrors);

				propagateScanFinished();
			}
		}));
	}

	private void doScan() {
		apiService.startScanJob(new MethodCallbackAdapter<>(new OperationCallback<ScanJobDto>() {

			@Override
			public void onSuccess(ScanJobDto aScanJob) {
				scheduleStatusTimer(STATUS_TIMER_INTERVAL_SCANNING);
			}

			@Override
			public void onError(List<ErrorDto> aErrors) {

				scanning = false;

				errorNotifier.notifyOfErrors(aErrors);

				propagateScanFinished();
			}
		}));
	}

	private void updateStatus() {

		UserDto user = authenticationManager.getUser();

		if (user != null) {
			doUpdateStatus();
		} else {
			scheduleStatusTimer(STATUS_TIMER_INTERVAL_NOT_SCANNING);
		}
	}

	private void doUpdateStatus() {
		apiService.getScanStatus(new MethodCallbackAdapter<>(new OperationCallback<ScanStatusDto>() {

			@Override
			public void onSuccess(ScanStatusDto aStatus) {

				status = aStatus;

				if (status != null) {

					if (!isScanning()) {

						scanning = true;

						propagateScanStarted();
					}

					propagateScanProgress(status);

					scheduleStatusTimer(STATUS_TIMER_INTERVAL_SCANNING);

				} else {

					if (isScanning()) {

						scanning = false;

						propagateScanFinished();
					}

					scheduleStatusTimer(STATUS_TIMER_INTERVAL_NOT_SCANNING);
				}
			}

			@Override
			public void onError(List<ErrorDto> aErrors) {
				scheduleStatusTimer(isScanning() ? STATUS_TIMER_INTERVAL_SCANNING : STATUS_TIMER_INTERVAL_NOT_SCANNING);
			}
		}));
	}

	private void propagateScanStarted() {

		log.info("Scanning started.");

		for (Delegate nextDelegate : new ArrayList<>(delegates)) {
			nextDelegate.onScanStarted(this);
		}
	}

	private void propagateScanProgress(ScanStatusDto aStatus) {
		for (Delegate nextDelegate : new ArrayList<>(delegates)) {
			nextDelegate.onScanProgress(this, aStatus);
		}
	}

	private void propagateScanFinished() {

		log.info("Scanning finished.");

		for (Delegate nextDelegate : new ArrayList<>(delegates)) {
			nextDelegate.onScanFinished(this);
		}
	}

}
