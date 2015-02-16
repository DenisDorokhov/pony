package net.dorokhov.pony.web.client.service;

import com.google.gwt.user.client.ui.RootPanel;

public class BusyIndicator {

	private static int taskCount = 0;

	private static boolean busy = false;

	public static void startTask() {

		taskCount++;

		if (taskCount > 0) {
			setBusy(true);
		}
	}

	public static void finishTask() {

		if (taskCount > 0) {
			taskCount--;
		}

		if (taskCount == 0) {
			setBusy(false);
		}
	}

	public static boolean isBusy() {
		return busy;
	}

	private static void setBusy(boolean aIsBusy) {

		if (busy != aIsBusy) {

			busy = aIsBusy;

			if (busy) {
				showWaitCursor();
			} else {
				showDefaultCursor();
			}
		}
	}

	private static void showWaitCursor() {
		RootPanel.get().getElement().getStyle().setProperty("cursor", "wait");
	}

	private static void showDefaultCursor() {
		RootPanel.get().getElement().getStyle().setProperty("cursor", "default");
	}

}
