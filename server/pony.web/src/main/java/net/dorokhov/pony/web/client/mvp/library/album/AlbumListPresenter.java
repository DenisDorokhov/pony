package net.dorokhov.pony.web.client.mvp.library.album;

import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.PresenterWidget;
import com.gwtplatform.mvp.client.View;
import net.dorokhov.pony.web.client.event.*;
import net.dorokhov.pony.web.client.mvp.common.HasLoadingState;
import net.dorokhov.pony.web.client.mvp.common.LoadingState;
import net.dorokhov.pony.web.client.mvp.common.SelectionMode;
import net.dorokhov.pony.web.client.service.BusyIndicator;
import net.dorokhov.pony.web.client.service.PlayListImpl;
import net.dorokhov.pony.web.client.service.SongService;
import net.dorokhov.pony.web.client.service.common.OperationCallback;
import net.dorokhov.pony.web.client.service.common.OperationRequest;
import net.dorokhov.pony.web.client.util.ObjectUtils;
import net.dorokhov.pony.web.shared.*;

import javax.inject.Inject;
import java.util.*;

public class AlbumListPresenter extends PresenterWidget<AlbumListPresenter.MyView> implements AlbumListUiHandlers,
		ArtistSelectionEvent.Handler, RefreshRequestEvent.Handler, EmptyLibraryEvent.Handler,
		SongSelectionRequestEvent.Handler, SongChangeEvent.Handler, SongStartEvent.Handler, SongPauseEvent.Handler {

	public interface MyView extends View, HasUiHandlers<AlbumListUiHandlers>, HasLoadingState {

		public ArtistDto getArtist();

		public void setArtist(ArtistDto aArtist);

		public List<AlbumSongsDto> getAlbums();

		public void setAlbums(List<AlbumSongsDto> aAlbums);

		public Set<SongDto> getSelectedSongs();

		public void setSelectedSongs(Set<SongDto> aSongs);

		public SongDto getActiveSong();

		public void setActiveSong(SongDto aSong);

		public boolean isPlaying();

		public void setPlaying(boolean aPlaying);

		public void selectSong(SongDto aSong, SelectionMode aSelectionMode);

		public void scrollToTop();

		public void scrollToSong(SongDto aSong);

	}

	private final SongService songService;

	private OperationRequest currentRequest;

	private boolean shouldHandleSongActivation = true;
	private boolean shouldScrollToSong = false;

	@Inject
	public AlbumListPresenter(EventBus aEventBus, MyView aView,
							  SongService aSongService) {

		super(aEventBus, aView);

		songService = aSongService;

		getView().setUiHandlers(this);
		getView().setLoadingState(LoadingState.LOADING);
	}

	@Override
	protected void onBind() {

		super.onBind();

		addRegisteredHandler(ArtistSelectionEvent.TYPE, this);
		addRegisteredHandler(RefreshRequestEvent.TYPE, this);
		addRegisteredHandler(EmptyLibraryEvent.TYPE, this);

		addRegisteredHandler(SongSelectionRequestEvent.TYPE, this);
		addRegisteredHandler(SongChangeEvent.TYPE, this);
		addRegisteredHandler(SongStartEvent.TYPE, this);
		addRegisteredHandler(SongPauseEvent.TYPE, this);
	}

	@Override
	public void onSongSelection(Set<SongDto> aSongs) {
		getEventBus().fireEvent(new SongSelectionEvent(aSongs));
	}

	@Override
	public void onSongActivation(SongDto aSong) {

		if (shouldHandleSongActivation) {

			List<SongDto> songs = new ArrayList<>();

			for (AlbumSongsDto album : getView().getAlbums()) {
				songs.addAll(album.getSongs());
			}

			getEventBus().fireEvent(new PlayListChangeEvent(new PlayListImpl(songs), songs.indexOf(aSong)));
		}

		shouldHandleSongActivation = true;
	}

	@Override
	public void onArtistSelection(ArtistSelectionEvent aEvent) {
		doUpdateAlbums(aEvent.getArtist(), true);
	}

	@Override
	public void onRefreshRequest(RefreshRequestEvent aEvent) {
		if (getView().getArtist() != null) {
			doUpdateAlbums(getView().getArtist(), false);
		}
	}

	@Override
	public void onEmptyLibrary(EmptyLibraryEvent aEvent) {

		if (currentRequest != null) {

			currentRequest.cancel();

			BusyIndicator.finishTask();
		}

		currentRequest = null;

		getView().setArtist(null);
		getView().setAlbums(null);

		getView().setLoadingState(LoadingState.EMPTY);
	}

	@Override
	public void onSongSelectionRequest(SongSelectionRequestEvent aEvent) {
		getView().selectSong(aEvent.getSong(), aEvent.getSelectionMode());
		getView().scrollToSong(aEvent.getSong());
	}

	@Override
	public void onSongChange(SongChangeEvent aEvent) {
		handleSongStart(aEvent.getSong());
	}

	@Override
	public void onSongStart(SongStartEvent aEvent) {
		handleSongStart(aEvent.getSong());
	}

	@Override
	public void onSongPause(SongPauseEvent aEvent) {
		getView().setPlaying(false);
	}

	private void handleSongStart(SongDto aSong) {

		if (!aSong.equals(getView().getActiveSong())) {

			shouldHandleSongActivation = false;

			getView().setActiveSong(aSong);
		}

		getView().setPlaying(true);
	}

	private void doUpdateAlbums(ArtistDto aArtist, boolean aShouldShowLoadingState) {

		ArtistDto oldArtist = getView().getArtist();

		getView().setArtist(aArtist);

		if (!ObjectUtils.nullSafeEquals(aArtist, oldArtist)) {

			getView().scrollToTop();

			shouldScrollToSong = true;
		}

		if (aShouldShowLoadingState || getView().getLoadingState() != LoadingState.LOADED) {
			getView().setLoadingState(LoadingState.LOADING);
		}

		if (currentRequest != null) {

			currentRequest.cancel();

			BusyIndicator.finishTask();
		}

		if (aArtist != null && aArtist.getId() != null) {

			BusyIndicator.startTask();

			currentRequest = songService.getArtistSongs(aArtist.getId(), new OperationCallback<ArtistAlbumsDto>() {
				@Override
				public void onSuccess(ArtistAlbumsDto aArtistAlbums) {

					BusyIndicator.finishTask();

					currentRequest = null;

					getView().setArtist(aArtistAlbums.getArtist());
					getView().setAlbums(aArtistAlbums.getAlbums());

					getView().setLoadingState(LoadingState.LOADED);

					if (shouldScrollToSong) {

						if (getView().getSelectedSongs().size() > 0) {
							scrollToSongs(getView().getSelectedSongs());
						} else if (getView().getActiveSong() != null) {
							getView().scrollToSong(getView().getActiveSong());
						}

						shouldScrollToSong = false;
					}
				}

				@Override
				public void onError(List<ErrorDto> aErrors) {

					BusyIndicator.finishTask();

					currentRequest = null;

					getView().setLoadingState(LoadingState.ERROR);
				}
			});

		} else {
			getView().setLoadingState(LoadingState.LOADED);
		}
	}

	private void scrollToSongs(Collection<SongDto> aSongs) {

		List<SongDto> songList = new ArrayList<>(aSongs);

		Collections.sort(songList);

		getView().scrollToSong(songList.get(0));
	}

}
