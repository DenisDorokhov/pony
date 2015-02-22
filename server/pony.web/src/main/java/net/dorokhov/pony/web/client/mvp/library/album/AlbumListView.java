package net.dorokhov.pony.web.client.mvp.library.album;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Widget;
import com.gwtplatform.mvp.client.ViewImpl;
import net.dorokhov.pony.web.client.mvp.common.LoadingState;
import net.dorokhov.pony.web.shared.AlbumSongsDto;
import net.dorokhov.pony.web.shared.ArtistDto;
import net.dorokhov.pony.web.shared.SongDto;

import java.util.List;

public class AlbumListView extends ViewImpl implements AlbumListPresenter.MyView {

	interface MyUiBinder extends UiBinder<Widget, AlbumListView> {}

	private static final MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

	public AlbumListView() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	@Override
	public ArtistDto getArtist() {
		return null;
	}

	@Override
	public void setArtist(ArtistDto aArtist) {

	}

	@Override
	public List<AlbumSongsDto> getAlbums() {
		return null;
	}

	@Override
	public void setAlbums(List<AlbumSongsDto> aAlbums) {

	}

	@Override
	public SongDto getSelectedSong() {
		return null;
	}

	@Override
	public void setSelectedSong(SongDto aSong) {

	}

	@Override
	public SongDto getActiveSong() {
		return null;
	}

	@Override
	public void setActiveSong(SongDto aSong) {

	}

	@Override
	public boolean isPlaying() {
		return false;
	}

	@Override
	public void setPlaying(boolean aPlaying) {

	}

	@Override
	public void scrollToTop() {

	}

	@Override
	public void scrollToSong(SongDto aSong) {

	}

	@Override
	public LoadingState getLoadingState() {
		return null;
	}

	@Override
	public void setLoadingState(LoadingState aLoadingState) {

	}

	@Override
	public void setUiHandlers(AlbumListUiHandlers uiHandlers) {

	}

}
