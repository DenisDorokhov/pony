package net.dorokhov.pony.web.client.service;

import java.util.ArrayList;
import java.util.Set;
import java.util.TreeSet;

public class BusyModeManager {

	public interface Delegate {

		public void onBusyModeStarted();

		public void onBusyModeEnded();

	}

	private int taskCount = 0;

	private boolean busy = false;

	private final Set<Delegate> delegates = new TreeSet<>();

	public void addDelegate(Delegate aDelegate) {
		delegates.add(aDelegate);
	}

	public void removeDelegate(Delegate aDelegate) {
		delegates.remove(aDelegate);
	}

	public void startTask() {

		taskCount++;

		if (taskCount > 0) {
			setBusy(true);
		}
	}

	public void finishTask() {

		if (taskCount > 0) {
			taskCount--;
		}

		if (taskCount == 0) {
			setBusy(false);
		}
	}

	public boolean isBusy() {
		return busy;
	}

	private void setBusy(boolean aIsBusy) {

		if (busy != aIsBusy) {

			busy = aIsBusy;

			if (busy) {
				for (Delegate delegate : new ArrayList<>(delegates)) {
					delegate.onBusyModeStarted();
				}
			} else {
				for (Delegate delegate : new ArrayList<>(delegates)) {
					delegate.onBusyModeEnded();
				}
			}
		}
	}

}
