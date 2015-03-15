package net.dorokhov.pony.web.client.mvp.common;

import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.UiHandlers;

public abstract class ModalViewWithUiHandlers<T extends UiHandlers> extends ModalView implements HasUiHandlers<T> {

	private T uiHandlers;

	protected ModalViewWithUiHandlers(EventBus eventBus) {
		super(eventBus);
	}

	protected T getUiHandlers() {
		return uiHandlers;
	}

	@Override
	public void setUiHandlers(T aUiHandlers) {
		this.uiHandlers = aUiHandlers;
	}

}
