package net.dorokhov.pony.web.client.event;

import com.google.gwt.event.shared.EventHandler;
import net.dorokhov.pony.web.shared.ArtistDto;

public class ArtistSelectionEvent extends AbstractEvent<ArtistSelectionEvent.Handler> {

	public static interface Handler extends EventHandler {
		public void onArtistSelection(ArtistSelectionEvent aEvent);
	}

	public static final Type<Handler> TYPE = new Type<>();

	private final ArtistDto artist;

	public ArtistSelectionEvent(ArtistDto aArtist) {

		super(TYPE);

		artist = aArtist;
	}

	public ArtistDto getArtist() {
		return artist;
	}

	@Override
	protected void dispatch(Handler aHandler) {
		aHandler.onArtistSelection(this);
	}

}
