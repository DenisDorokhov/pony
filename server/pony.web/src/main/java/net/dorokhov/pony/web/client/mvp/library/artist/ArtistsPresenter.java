package net.dorokhov.pony.web.client.mvp.library.artist;

import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.PresenterWidget;
import com.gwtplatform.mvp.client.View;
import net.dorokhov.pony.web.client.service.ErrorNotifier;
import net.dorokhov.pony.web.client.service.SongService;
import net.dorokhov.pony.web.client.service.common.OperationCallback;
import net.dorokhov.pony.web.shared.ErrorDto;
import net.dorokhov.pony.web.shared.list.ArtistListDto;

import javax.inject.Inject;
import java.util.List;

public class ArtistsPresenter extends PresenterWidget<ArtistsPresenter.MyView> {

	public interface MyView extends View {

		public ArtistListDto getArtists();

		public void setArtists(ArtistListDto aArtists);

	}

	private final SongService songService;

	private final ErrorNotifier errorNotifier;

	@Inject
	public ArtistsPresenter(EventBus aEventBus, MyView aView,
							SongService aSongService, ErrorNotifier aErrorNotifier) {

		super(aEventBus, aView);

		songService = aSongService;
		errorNotifier = aErrorNotifier;
	}

	@Override
	protected void onReveal() {

		super.onReveal();

		loadArtists();
	}

	private void loadArtists() {
		songService.getArtists(new OperationCallback<ArtistListDto>() {
			@Override
			public void onSuccess(ArtistListDto aArtists) {
				getView().setArtists(aArtists);
			}

			@Override
			public void onError(List<ErrorDto> aErrors) {
				errorNotifier.notifyOfErrors(aErrors);
			}
		});
	}

}
