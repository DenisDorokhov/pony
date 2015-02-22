package net.dorokhov.pony.web.client.event;

import com.google.gwt.event.shared.EventHandler;
import net.dorokhov.pony.web.shared.SongDto;

public class SongPauseEvent extends AbstractSongEvent<SongPauseEvent.Handler> {

	public static interface Handler extends EventHandler {
		public void onSongPause(SongPauseEvent aEvent);
	}

	public static final Type<Handler> TYPE = new Type<>();

	public SongPauseEvent(SongDto aSong) {
		super(TYPE, aSong);
	}

	@Override
	protected void dispatch(Handler aHandler) {
		aHandler.onSongPause(this);
	}

}
