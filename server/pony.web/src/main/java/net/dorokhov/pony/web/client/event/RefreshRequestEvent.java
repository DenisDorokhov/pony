package net.dorokhov.pony.web.client.event;

import com.google.gwt.event.shared.EventHandler;

public class RefreshRequestEvent extends AbstractEvent<RefreshRequestEvent.Handler> {

	public static interface Handler extends EventHandler {
		public void onRefreshRequest(RefreshRequestEvent aEvent);
	}

	public static final Type<Handler> TYPE = new Type<>();

	public RefreshRequestEvent() {
		super(TYPE);
	}

	@Override
	protected void dispatch(Handler aHandler) {
		aHandler.onRefreshRequest(this);
	}
	
}
