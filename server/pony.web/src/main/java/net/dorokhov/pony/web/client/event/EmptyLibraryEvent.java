package net.dorokhov.pony.web.client.event;

import com.google.gwt.event.shared.EventHandler;

public class EmptyLibraryEvent extends AbstractEvent<EmptyLibraryEvent.Handler> {

	public static interface Handler extends EventHandler {
		public void onEmptyLibrary(EmptyLibraryEvent aEvent);
	}

	public static final Type<Handler> TYPE = new Type<>();

	public EmptyLibraryEvent() {
		super(TYPE);
	}

	@Override
	protected void dispatch(Handler aHandler) {
		aHandler.onEmptyLibrary(this);
	}

}
