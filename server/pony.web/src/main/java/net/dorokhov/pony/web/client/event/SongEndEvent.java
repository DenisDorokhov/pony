package net.dorokhov.pony.web.client.event;

import com.google.gwt.event.shared.EventHandler;
import net.dorokhov.pony.web.shared.SongDto;

public class SongEndEvent extends AbstractSongEvent<SongEndEvent.Handler> {

	public static interface Handler extends EventHandler {
		public void onSongEnd(SongEndEvent aEvent);
	}

	public static final Type<Handler> TYPE = new Type<>();

	public SongEndEvent(SongDto aSong) {
		super(TYPE, aSong);
	}

	@Override
	protected void dispatch(Handler aHandler) {
		aHandler.onSongEnd(this);
	}

}
