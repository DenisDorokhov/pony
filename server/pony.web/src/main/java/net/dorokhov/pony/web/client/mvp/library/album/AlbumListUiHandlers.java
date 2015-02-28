package net.dorokhov.pony.web.client.mvp.library.album;

import com.gwtplatform.mvp.client.UiHandlers;
import net.dorokhov.pony.web.shared.SongDto;

import java.util.Set;

public interface AlbumListUiHandlers extends UiHandlers {

	public void onSongSelection(Set<SongDto> aSongs);

	public void onSongActivation(SongDto aSong);

}
