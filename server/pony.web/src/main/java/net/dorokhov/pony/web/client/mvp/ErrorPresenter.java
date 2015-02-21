package net.dorokhov.pony.web.client.mvp;

import com.google.gwt.user.client.Window;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.NoGatekeeper;
import com.gwtplatform.mvp.client.annotations.ProxyStandard;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.client.proxy.ProxyPlace;
import com.gwtplatform.mvp.shared.proxy.PlaceRequest;
import net.dorokhov.pony.web.client.resource.Messages;
import net.dorokhov.pony.web.client.PlaceTokens;

import javax.inject.Inject;

public class ErrorPresenter extends Presenter<ErrorPresenter.MyView, ErrorPresenter.MyProxy> implements ErrorUiHandlers {

	@ProxyStandard
	@NoGatekeeper
	@NameToken(PlaceTokens.ERROR)
	public interface MyProxy extends ProxyPlace<ErrorPresenter> {}

	public interface MyView extends View, HasUiHandlers<ErrorUiHandlers> {}

	private final PlaceManager placeManager;

	@Inject
	public ErrorPresenter(EventBus aEventBus, MyView aView, MyProxy aProxy, PlaceManager aPlaceManager) {

		super(aEventBus, aView, aProxy, RevealType.Root);

		placeManager = aPlaceManager;

		getView().setUiHandlers(this);
	}

	@Override
	protected void onReveal() {

		super.onReveal();

		Window.setTitle(Messages.INSTANCE.errorTitle());
	}

	@Override
	public void onHomeRequested() {
		placeManager.revealPlace(new PlaceRequest.Builder().nameToken(PlaceTokens.DEFAULT).build());
	}

}
