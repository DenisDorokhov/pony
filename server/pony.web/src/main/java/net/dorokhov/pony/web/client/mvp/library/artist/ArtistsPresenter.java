package net.dorokhov.pony.web.client.mvp.library.artist;

import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.PresenterWidget;
import com.gwtplatform.mvp.client.View;
import net.dorokhov.pony.web.client.event.ArtistSelectionDoneEvent;
import net.dorokhov.pony.web.client.event.ArtistSelectionRequestedEvent;
import net.dorokhov.pony.web.client.event.ArtistsUpdateEvent;
import net.dorokhov.pony.web.client.event.RefreshRequestEvent;
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
import java.util.logging.Logger;

public class ArtistsPresenter extends PresenterWidget<ArtistsPresenter.MyView> implements ArtistsUiHandlers, RefreshRequestEvent.Handler, ArtistSelectionRequestedEvent.Handler {

	public interface MyView extends View, HasUiHandlers<ArtistsUiHandlers>, HasLoadingState {

		public List<ArtistDto> getArtists();

		public void setArtists(List<ArtistDto> aArtists);

		public ArtistDto getSelectedArtist();

		public void setSelectedArtist(ArtistDto aArtist, boolean aShouldScroll);

	}

	private final Logger log = Logger.getLogger(getClass().getName());

	private final SongService songService;

	private final ErrorNotifier errorNotifier;

	private final HashMap<String, ArtistDto> artistMap = new HashMap<>();

	private String artistToSelect;
	
	private OperationRequest currentRequest;

	@Inject
	public ArtistsPresenter(EventBus aEventBus, MyView aView,
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
		addRegisteredHandler(ArtistSelectionRequestedEvent.TYPE, this);
	}

	@Override
	protected void onReveal() {

		super.onReveal();

		loadArtists(true, true);
	}

	@Override
	public void onArtistSelection(ArtistDto aArtist) {
		getEventBus().fireEvent(new ArtistSelectionDoneEvent(aArtist));
	}

	@Override
	public void onRefreshRequest(RefreshRequestEvent aEvent) {
		loadArtists(false, false);
	}

	@Override
	public void onArtistSelectionRequested(ArtistSelectionRequestedEvent aEvent) {
		
		artistToSelect = aEvent.getArtistIdOrName();
		
		doSelectArtist(artistToSelect, true);
	}

	private void loadArtists(boolean aShouldShowLoadingState, final boolean aShouldScroll) {

		if (aShouldShowLoadingState || getView().getLoadingState() != LoadingState.LOADED) {
			getView().setLoadingState(LoadingState.LOADING);
		}

		if (currentRequest != null) {

			currentRequest.cancel();

			BusyIndicator.finishTask();
		}

		currentRequest = songService.getArtists(new OperationCallback<List<ArtistDto>>() {
			@Override
			public void onSuccess(List<ArtistDto> aArtists) {

				BusyIndicator.finishTask();

				currentRequest = null;

				doUpdateArtists(aArtists);

				getView().setLoadingState(LoadingState.LOADED);

				getEventBus().fireEvent(new ArtistsUpdateEvent(aArtists));

				doSelectArtist(artistToSelect, aShouldScroll);
				
				getView().setArtists(aArtists);
			}

			@Override
			public void onError(List<ErrorDto> aErrors) {

				BusyIndicator.finishTask();

				currentRequest = null;

				doUpdateArtists(new ArrayList<ArtistDto>());

				getView().setLoadingState(LoadingState.ERROR);

				errorNotifier.notifyOfErrors(aErrors);
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

			if (artistToSelect != null) {
				getView().setSelectedArtist(artistToSelect, aShouldScroll);
			} else if (aArtist != null && !aArtist.trim().equals("")) {
				log.warning("Could not find artist [" + aArtist + "].");
			}

			if (getView().getSelectedArtist() == null) {
				getView().setSelectedArtist(artists.get(0), aShouldScroll);
			}
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
