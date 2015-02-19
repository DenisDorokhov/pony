package net.dorokhov.pony.web.client.event;

import com.google.gwt.event.shared.EventHandler;

public class ArtistSelectionRequestedEvent extends AbstractEvent<ArtistSelectionRequestedEvent.Handler> {

	public static interface Handler extends EventHandler {
		public void onArtistSelectionRequested(ArtistSelectionRequestedEvent aEvent);
	}

	public static final Type<Handler> TYPE = new Type<>();

	private final String artistIdOrName;

	public ArtistSelectionRequestedEvent(String aArtistIdOrName) {

		super(TYPE);

		artistIdOrName = aArtistIdOrName;
	}

	public String getArtistIdOrName() {
		return artistIdOrName;
	}

	@Override
	protected void dispatch(Handler aHandler) {
		aHandler.onArtistSelectionRequested(this);
	}

}
