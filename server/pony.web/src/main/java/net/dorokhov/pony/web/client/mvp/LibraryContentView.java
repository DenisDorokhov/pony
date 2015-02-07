package net.dorokhov.pony.web.client.mvp;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Widget;
import com.gwtplatform.mvp.client.ViewImpl;

public class LibraryContentView extends ViewImpl implements LibraryContentPresenter.MyView {

	interface MyUiBinder extends UiBinder<Widget, LibraryContentView> {}

	private static final MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

	public LibraryContentView() {
		initWidget(uiBinder.createAndBindUi(this));
	}

}
