package net.dorokhov.pony.web.client.mvp.common;

import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.HandlerRegistration;
import com.gwtplatform.mvp.client.PopupView;
import com.gwtplatform.mvp.client.PopupViewCloseHandler;
import com.gwtplatform.mvp.client.ViewImpl;
import com.gwtplatform.mvp.client.proxy.NavigationEvent;
import com.gwtplatform.mvp.client.proxy.NavigationHandler;
import org.gwtbootstrap3.client.shared.event.ModalHiddenEvent;
import org.gwtbootstrap3.client.shared.event.ModalHiddenHandler;
import org.gwtbootstrap3.client.ui.Modal;

public abstract class ModalView extends ViewImpl implements PopupView {

	private final EventBus eventBus;

	private HandlerRegistration autoHideHandler;
	private HandlerRegistration closeHandlerRegistration;

	protected ModalView(EventBus aEventBus) {
		eventBus = aEventBus;
	}

	@Override
	public void show() {
		((Modal) asWidget()).show();
	}

	@Override
	public void hide() {
		((Modal) asWidget()).hide();
	}

	@Override
	public void center() {}

	@Override
	public void setAutoHideOnNavigationEventEnabled(boolean aAutoHide) {
		if (aAutoHide) {
			if (autoHideHandler != null) {
				return;
			}
			autoHideHandler = eventBus.addHandler(NavigationEvent.getType(),
					new NavigationHandler() {
						@Override
						public void onNavigation(NavigationEvent aEvent) {
							hide();
						}
					});
		} else {
			if (autoHideHandler != null) {
				autoHideHandler.removeHandler();
			}
		}
	}

	@Override
	public void setCloseHandler(final PopupViewCloseHandler aPopupViewCloseHandler) {
		if (closeHandlerRegistration != null) {
			closeHandlerRegistration.removeHandler();
		}
		if (aPopupViewCloseHandler == null) {
			closeHandlerRegistration = null;
		} else {
			closeHandlerRegistration = ((Modal) asWidget()).addHiddenHandler(new ModalHiddenHandler() {
				@Override
				public void onHidden(ModalHiddenEvent aEvent) {
					aPopupViewCloseHandler.onClose();
				}
			});
		}
	}

	@Override
	public void setPosition(int aLeft, int aTop) {}

}
