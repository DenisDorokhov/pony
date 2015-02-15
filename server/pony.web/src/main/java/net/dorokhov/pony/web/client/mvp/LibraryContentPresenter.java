package net.dorokhov.pony.web.client.mvp;

import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.PresenterWidget;
import com.gwtplatform.mvp.client.View;

import javax.inject.Inject;

public class LibraryContentPresenter extends PresenterWidget<LibraryContentPresenter.MyView> {

	public interface MyView extends View {}

	@Inject
	public LibraryContentPresenter(EventBus aEventBus, MyView aView) {
		super(aEventBus, aView);
	}

}
