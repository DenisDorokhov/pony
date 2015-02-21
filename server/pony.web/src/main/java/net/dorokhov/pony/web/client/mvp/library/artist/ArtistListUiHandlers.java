package net.dorokhov.pony.web.client.mvp.library.artist;

import com.gwtplatform.mvp.client.UiHandlers;
import net.dorokhov.pony.web.shared.ArtistDto;

public interface ArtistListUiHandlers extends UiHandlers {
	
	public void onArtistSelection(ArtistDto aArtist);
	
}
