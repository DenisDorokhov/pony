package net.dorokhov.pony.web.client.mvp.library.artist;

import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.PresenterWidget;
import com.gwtplatform.mvp.client.View;
import net.dorokhov.pony.web.client.event.ArtistSelectionRequestedEvent;

import javax.inject.Inject;

public class ArtistsPresenter extends PresenterWidget<ArtistsPresenter.MyView> implements ArtistSelectionRequestedEvent.Handler {

	public interface MyView extends View {}

	@Inject
	public ArtistsPresenter(EventBus aEventBus, MyView aView) {
		super(aEventBus, aView);
	}

	@Override
	protected void onBind() {

		super.onBind();

		addRegisteredHandler(ArtistSelectionRequestedEvent.TYPE, this);
	}

	@Override
	public void onArtistSelectionRequested(ArtistSelectionRequestedEvent aEvent) {
		// TODO: implement
	}

}
