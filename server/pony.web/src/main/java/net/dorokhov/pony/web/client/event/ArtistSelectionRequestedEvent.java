package net.dorokhov.pony.web.client.event;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

public class ArtistSelectionRequestedEvent extends AbstractEvent<ArtistSelectionRequestedEvent.Handler> {

	public static interface Handler extends EventHandler {
		public void onArtistSelectionRequested(ArtistSelectionRequestedEvent aEvent);
	}

	public static final GwtEvent.Type<Handler> TYPE = new GwtEvent.Type<>();

	private final String artistId;

	public ArtistSelectionRequestedEvent(String aArtistId) {

		super(TYPE);

		artistId = aArtistId;
	}

	public String getArtistId() {
		return artistId;
	}

	@Override
	protected void dispatch(Handler aHandler) {
		aHandler.onArtistSelectionRequested(this);
	}

}
