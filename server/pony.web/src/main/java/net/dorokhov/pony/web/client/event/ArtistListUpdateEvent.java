package net.dorokhov.pony.web.client.event;

import com.google.gwt.event.shared.EventHandler;
import net.dorokhov.pony.web.shared.ArtistDto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ArtistListUpdateEvent extends AbstractEvent<ArtistListUpdateEvent.Handler> {

	public static interface Handler extends EventHandler {
		public void onArtistListUpdate(ArtistListUpdateEvent aEvent);
	}

	public static final Type<Handler> TYPE = new Type<>();

	private final Map<Long, ArtistDto> idToArtist = new HashMap<>();
	
	private final List<ArtistDto> artists;

	public ArtistListUpdateEvent(List<ArtistDto> aArtists) {
		
		super(TYPE);
		
		artists = aArtists != null ? aArtists : new ArrayList<ArtistDto>();

		for (ArtistDto artist : getArtists()) {
			idToArtist.put(artist.getId(), artist);
		}
	}

	public List<ArtistDto> getArtists() {
		return artists;
	}

	public ArtistDto getArtist(Long aId) {
		return idToArtist.get(aId);
	}

	public boolean hasArtist(ArtistDto aArtist) {
		return getArtist(aArtist.getId()) != null;
	}

	@Override
	protected void dispatch(Handler aHandler) {
		aHandler.onArtistListUpdate(this);
	}
	
}
