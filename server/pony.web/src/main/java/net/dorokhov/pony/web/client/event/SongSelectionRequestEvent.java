package net.dorokhov.pony.web.client.event;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import net.dorokhov.pony.web.client.mvp.common.SelectionMode;
import net.dorokhov.pony.web.shared.SongDto;

public class SongSelectionRequestEvent extends AbstractSongEvent<SongSelectionRequestEvent.Handler> {

	public interface Handler extends EventHandler {
		void onSongSelectionRequest(SongSelectionRequestEvent aEvent);
	}

	public interface HasHandler {
		public HandlerRegistration addSongSelectionRequestHandler(SongSelectionRequestEvent.Handler aHandler);
	}

	public static final Type<Handler> TYPE = new Type<>();

	private final SelectionMode selectionMode;

	public SongSelectionRequestEvent(SongDto aSong, SelectionMode aSelectionMode) {

		super(TYPE, aSong);

		selectionMode = aSelectionMode;
	}

	public SelectionMode getSelectionMode() {
		return selectionMode;
	}

	@Override
	protected void dispatch(Handler aHandler) {
		aHandler.onSongSelectionRequest(this);
	}

}
