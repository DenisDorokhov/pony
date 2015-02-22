package net.dorokhov.pony.web.client.event;

import com.google.gwt.event.shared.EventHandler;
import net.dorokhov.pony.web.shared.SongDto;

public class SongStartEvent extends AbstractSongEvent<SongStartEvent.Handler> {

	public static interface Handler extends EventHandler {
		public void onSongStart(SongStartEvent aEvent);
	}

	public static final Type<Handler> TYPE = new Type<>();

	public SongStartEvent(SongDto aSong) {
		super(TYPE, aSong);
	}

	@Override
	protected void dispatch(Handler aHandler) {
		aHandler.onSongStart(this);
	}

}
