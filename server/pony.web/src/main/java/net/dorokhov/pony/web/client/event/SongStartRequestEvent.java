package net.dorokhov.pony.web.client.event;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import net.dorokhov.pony.web.shared.SongDto;

public class SongStartRequestEvent extends AbstractSongEvent<SongStartRequestEvent.Handler> {

	public static interface Handler extends EventHandler {
		public void onSongStartRequest(SongStartRequestEvent aEvent);
	}

	public interface HasHandler {
		public HandlerRegistration addSongStartRequestHandler(SongStartRequestEvent.Handler aHandler);
	}

	public static final Type<Handler> TYPE = new Type<>();

	public SongStartRequestEvent(SongDto aSong) {
		super(TYPE, aSong);
	}

	@Override
	protected void dispatch(Handler aHandler) {
		aHandler.onSongStartRequest(this);
	}

}
