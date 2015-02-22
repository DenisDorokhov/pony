package net.dorokhov.pony.web.client.event;

import com.google.gwt.event.shared.EventHandler;
import net.dorokhov.pony.web.shared.SongDto;

public class SongSelectionEvent extends AbstractSongEvent<SongSelectionEvent.Handler> {

	public interface Handler extends EventHandler {
		void onSongSelection(SongSelectionEvent event);
	}

	public static final Type<Handler> TYPE = new Type<>();

	public SongSelectionEvent(SongDto aSong) {
		super(TYPE, aSong);
	}

	@Override
	protected void dispatch(Handler aHandler) {
		aHandler.onSongSelection(this);
	}

}
