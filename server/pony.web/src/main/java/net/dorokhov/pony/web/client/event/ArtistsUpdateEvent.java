package net.dorokhov.pony.web.client.event;

import com.google.gwt.event.shared.EventHandler;
import net.dorokhov.pony.web.shared.ArtistDto;

import java.util.ArrayList;
import java.util.List;

public class ArtistsUpdateEvent extends AbstractEvent<ArtistsUpdateEvent.Handler> {

	public static interface Handler extends EventHandler {
		public void onArtistUpdate(ArtistsUpdateEvent aEvent);
	}

	public static final Type<Handler> TYPE = new Type<>();
	
	private final List<ArtistDto> artists;

	public ArtistsUpdateEvent(List<ArtistDto> aArtists) {
		
		super(TYPE);
		
		artists = aArtists != null ? aArtists : new ArrayList<ArtistDto>();
	}

	public List<ArtistDto> getArtists() {
		return artists;
	}

	@Override
	protected void dispatch(Handler aHandler) {
		aHandler.onArtistUpdate(this);
	}
	
}
