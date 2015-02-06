package net.dorokhov.pony.web.client.mvp;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Widget;
import com.gwtplatform.mvp.client.ViewImpl;

public class ApplicationView extends ViewImpl implements ApplicationPresenter.MyView {

	interface MyUiBinder extends UiBinder<Widget, ApplicationView> {}

	private static final MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

	public ApplicationView() {
		initWidget(uiBinder.createAndBindUi(this));
	}

}
