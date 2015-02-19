package net.dorokhov.pony.web.client.mvp.library.album;

import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.PresenterWidget;
import com.gwtplatform.mvp.client.View;

import javax.inject.Inject;

public class AlbumsPresenter extends PresenterWidget<AlbumsPresenter.MyView> {

	public interface MyView extends View {}

	@Inject
	public AlbumsPresenter(EventBus aEventBus, MyView aView) {
		super(aEventBus, aView);
	}

}