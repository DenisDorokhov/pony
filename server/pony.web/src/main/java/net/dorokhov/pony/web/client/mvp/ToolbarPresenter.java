package net.dorokhov.pony.web.client.mvp;

import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.PresenterWidget;
import com.gwtplatform.mvp.client.View;

public class ToolbarPresenter extends PresenterWidget<ToolbarPresenter.MyView> {

	public interface MyView extends View {}

	@Inject
	public ToolbarPresenter(EventBus aEventBus, MyView aView) {
		super(aEventBus, aView);
	}

}
