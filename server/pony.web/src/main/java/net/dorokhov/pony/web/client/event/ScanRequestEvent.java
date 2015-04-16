package net.dorokhov.pony.web.client.event;

import com.google.gwt.event.shared.EventHandler;

public class ScanRequestEvent extends AbstractEvent<ScanRequestEvent.Handler> {

	public static interface Handler extends EventHandler {
		public void onScanRequest(ScanRequestEvent aEvent);
	}

	public static final Type<Handler> TYPE = new Type<>();

	public ScanRequestEvent() {
		super(TYPE);
	}

	@Override
	protected void dispatch(Handler aHandler) {
		aHandler.onScanRequest(this);
	}

}
