package net.dorokhov.pony.web.client.event;

import com.google.gwt.event.shared.EventHandler;
import net.dorokhov.pony.web.shared.SongDto;

public class SongSelectionRequestEvent extends AbstractSongEvent<SongSelectionRequestEvent.Handler> {

	public interface Handler extends EventHandler {
		void onSongSelectionRequest(SongSelectionRequestEvent aEvent);
	}

	public static final Type<Handler> TYPE = new Type<>();

	public SongSelectionRequestEvent(SongDto aSong) {
		super(TYPE, aSong);
	}

	@Override
	protected void dispatch(Handler aHandler) {
		aHandler.onSongSelectionRequest(this);
	}

}
