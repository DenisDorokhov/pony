package net.dorokhov.pony.web.client.event;

import com.google.gwt.event.shared.EventHandler;

public class PlaybackRequestEvent extends AbstractEvent<PlaybackRequestEvent.Handler> {

	public static interface Handler extends EventHandler {
		public void onPlaybackRequest(PlaybackRequestEvent aEvent);
	}

	public static final Type<Handler> TYPE = new Type<>();

	public PlaybackRequestEvent() {
		super(TYPE);
	}

	@Override
	protected void dispatch(Handler aHandler) {
		aHandler.onPlaybackRequest(this);
	}

}
