package net.dorokhov.pony.web.client.mvp;

import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyStandard;
import com.gwtplatform.mvp.client.proxy.ProxyPlace;
import net.dorokhov.pony.web.client.PlaceTokens;

public class ApplicationPresenter extends Presenter<ApplicationPresenter.MyView, ApplicationPresenter.MyProxy> {

	@ProxyStandard
	@NameToken(PlaceTokens.HOME)
	public interface MyProxy extends ProxyPlace<ApplicationPresenter> {}

	public interface MyView extends View {}

	@Inject
	public ApplicationPresenter(EventBus aEventBus, MyView aView, MyProxy aProxy) {
		super(aEventBus, aView, aProxy, RevealType.RootLayout);
	}

}
