package net.dorokhov.pony.web.client.mvp;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Widget;
import com.gwtplatform.mvp.client.ViewWithUiHandlers;

public class ErrorView extends ViewWithUiHandlers<ErrorUiHandlers> implements ErrorPresenter.MyView {

	interface MyUiBinder extends UiBinder<Widget, ErrorView> {}

	private static final MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

	public ErrorView() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	@UiHandler("homeButton")
	void onHomeClick(ClickEvent aEvent) {
		getUiHandlers().onHomeRequested();
	}

}
