package net.dorokhov.pony.web.client.event;

import com.google.gwt.event.shared.EventHandler;
import net.dorokhov.pony.web.shared.SongDto;

public class SongChangeEvent extends AbstractSongEvent<SongChangeEvent.Handler> {

	public static interface Handler extends EventHandler {
		public void onSongChange(SongChangeEvent aEvent);
	}

	public static final Type<Handler> TYPE = new Type<>();

	public SongChangeEvent(SongDto aSong) {
		super(TYPE, aSong);
	}

	@Override
	protected void dispatch(Handler aHandler) {
		aHandler.onSongChange(this);
	}

}
