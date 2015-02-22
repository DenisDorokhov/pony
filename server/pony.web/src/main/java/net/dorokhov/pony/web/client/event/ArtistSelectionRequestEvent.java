package net.dorokhov.pony.web.client.event;

import com.google.gwt.event.shared.EventHandler;

public class ArtistSelectionRequestEvent extends AbstractEvent<ArtistSelectionRequestEvent.Handler> {

	public static interface Handler extends EventHandler {
		public void onArtistSelectionRequest(ArtistSelectionRequestEvent aEvent);
	}

	public static final Type<Handler> TYPE = new Type<>();

	private final String artistIdOrName;

	public ArtistSelectionRequestEvent(String aArtistIdOrName) {

		super(TYPE);

		artistIdOrName = aArtistIdOrName;
	}

	public String getArtistIdOrName() {
		return artistIdOrName;
	}

	@Override
	protected void dispatch(Handler aHandler) {
		aHandler.onArtistSelectionRequest(this);
	}

}
