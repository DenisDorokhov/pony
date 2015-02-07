package net.dorokhov.pony.web.client.mvp;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Widget;
import com.gwtplatform.mvp.client.ViewImpl;

public class LoginView extends ViewImpl implements LoginPresenter.MyView {

	interface MyUiBinder extends UiBinder<Widget, LoginView> {}

	private static final MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

	public LoginView() {
		initWidget(uiBinder.createAndBindUi(this));
	}

}
