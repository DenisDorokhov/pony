package net.dorokhov.pony.web.client.mvp.library.artist;

import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.PresenterWidget;
import com.gwtplatform.mvp.client.View;
import net.dorokhov.pony.web.client.event.*;
import net.dorokhov.pony.web.client.mvp.common.HasLoadingState;
import net.dorokhov.pony.web.client.mvp.common.LoadingState;
import net.dorokhov.pony.web.client.service.BusyIndicator;
import net.dorokhov.pony.web.client.service.ErrorNotifier;
import net.dorokhov.pony.web.client.service.SongService;
import net.dorokhov.pony.web.client.service.common.OperationCallback;
import net.dorokhov.pony.web.client.service.common.OperationRequest;
import net.dorokhov.pony.web.shared.ArtistDto;
import net.dorokhov.pony.web.shared.ErrorDto;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ArtistListPresenter extends PresenterWidget<ArtistListPresenter.MyView> implements ArtistListUiHandlers, RefreshRequestEvent.Handler, ArtistSelectionRequestEvent.Handler, SongSelectionRequestEvent.Handler {

	public interface MyView extends View, HasUiHandlers<ArtistListUiHandlers>, HasLoadingState {

		public List<ArtistDto> getArtists();

		public void setArtists(List<ArtistDto> aArtists);

		public ArtistDto getSelectedArtist();

		public void setSelectedArtist(ArtistDto aArtist, boolean aShouldScroll);

	}

	private final SongService songService;

	private final ErrorNotifier errorNotifier;

	private final HashMap<String, ArtistDto> artistMap = new HashMap<>();

	private String artistToSelect;
	
	private OperationRequest currentRequest;

	@Inject
	public ArtistListPresenter(EventBus aEventBus, MyView aView,
							   SongService aSongService, ErrorNotifier aErrorNotifier) {

		super(aEventBus, aView);

		songService = aSongService;
		errorNotifier = aErrorNotifier;

		getView().setUiHandlers(this);
	}

	@Override
	protected void onBind() {

		super.onBind();

		addRegisteredHandler(RefreshRequestEvent.TYPE, this);
		addRegisteredHandler(ArtistSelectionRequestEvent.TYPE, this);
		addRegisteredHandler(SongSelectionRequestEvent.TYPE, this);
	}

	@Override
	protected void onReveal() {

		super.onReveal();

		loadArtists(true, true);
	}

	@Override
	public void onArtistSelection(ArtistDto aArtist) {
		getEventBus().fireEvent(new ArtistSelectionEvent(aArtist));
	}

	@Override
	public void onRefreshRequest(RefreshRequestEvent aEvent) {
		loadArtists(false, getView().getLoadingState() == LoadingState.ERROR);
	}

	@Override
	public void onArtistSelectionRequest(ArtistSelectionRequestEvent aEvent) {
		
		artistToSelect = aEvent.getArtistIdOrName();
		
		doSelectArtist(artistToSelect, true);
	}

	@Override
	public void onSongSelectionRequest(SongSelectionRequestEvent aEvent) {

		ArtistDto artist = artistMap.get(aEvent.getSong().getAlbum().getArtist().getId().toString());

		if (artist != null) {
			getView().setSelectedArtist(artist, true);
		}
	}

	private void loadArtists(final boolean aShouldShowLoadingState, final boolean aShouldScroll) {

		if (aShouldShowLoadingState || getView().getLoadingState() != LoadingState.LOADED) {
			getView().setLoadingState(LoadingState.LOADING);
		}

		if (currentRequest != null) {

			currentRequest.cancel();

			BusyIndicator.finishTask();
		}

		BusyIndicator.startTask();

		currentRequest = songService.getArtists(new OperationCallback<List<ArtistDto>>() {
			@Override
			public void onSuccess(List<ArtistDto> aArtists) {

				BusyIndicator.finishTask();

				currentRequest = null;

				doUpdateArtists(aArtists);

				if (aArtists.size() == 0) {

					getView().setLoadingState(LoadingState.EMPTY);

					getEventBus().fireEvent(new EmptyLibraryEvent());

				} else {
					getView().setLoadingState(LoadingState.LOADED);
				}

				doSelectArtist(artistToSelect, aShouldScroll);
			}

			@Override
			public void onError(List<ErrorDto> aErrors) {

				BusyIndicator.finishTask();

				currentRequest = null;

				errorNotifier.notifyOfErrors(aErrors);

				if (aShouldShowLoadingState) {

					doUpdateArtists(new ArrayList<ArtistDto>());

					getView().setLoadingState(LoadingState.ERROR);
				}
			}
		});
	}

	private void doUpdateArtists(List<ArtistDto> aArtists) {

		artistMap.clear();

		for (ArtistDto artist : aArtists) {
			if (artist.getName() != null) {
				artistMap.put(artist.getName().trim().toLowerCase(), artist);
			}
		}
		for (ArtistDto artist : aArtists) {
			if (artist.getId() != null) {
				artistMap.put(artist.getId().toString(), artist);
			}
		}

		getView().setArtists(aArtists);
	}

	private void doSelectArtist(String aArtist, boolean aShouldScroll) {

		List<ArtistDto> artists = getView().getArtists();

		if (artists != null && artists.size() > 0) {

			ArtistDto artistToSelect = findArtist(aArtist);
			if (artistToSelect == null) {
				artistToSelect = artists.get(0);
			}

			getView().setSelectedArtist(artistToSelect, aShouldScroll);
		}
	}

	private ArtistDto findArtist(String aArtist) {

		if (aArtist != null) {

			String artistName = aArtist.trim().toLowerCase();

			return artistMap.get(artistName);
		}

		return null;
	}

}
