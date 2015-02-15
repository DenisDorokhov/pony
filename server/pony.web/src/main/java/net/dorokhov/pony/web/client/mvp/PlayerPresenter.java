package net.dorokhov.pony.web.client.mvp;

import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.PresenterWidget;
import com.gwtplatform.mvp.client.View;

import javax.inject.Inject;

public class PlayerPresenter extends PresenterWidget<PlayerPresenter.MyView> {

	public interface MyView extends View {}

	@Inject
	public PlayerPresenter(EventBus aEventBus, MyView aView) {
		super(aEventBus, aView);
	}

}
