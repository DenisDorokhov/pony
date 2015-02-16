package net.dorokhov.pony.web.client.event;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import net.dorokhov.pony.web.shared.ArtistDto;

public class ArtistSelectionDoneEvent extends AbstractEvent<ArtistSelectionDoneEvent.Handler> {

	public static interface Handler extends EventHandler {
		public void onArtistSelectionDone(ArtistSelectionDoneEvent aEvent);
	}

	public static final GwtEvent.Type<Handler> TYPE = new GwtEvent.Type<>();

	private final ArtistDto artist;

	public ArtistSelectionDoneEvent(ArtistDto aArtist) {

		super(TYPE);

		artist = aArtist;
	}

	public ArtistDto getArtist() {
		return artist;
	}

	@Override
	protected void dispatch(Handler aHandler) {
		aHandler.onArtistSelectionDone(this);
	}

}
