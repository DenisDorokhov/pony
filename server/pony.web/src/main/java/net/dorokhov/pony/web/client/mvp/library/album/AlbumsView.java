package net.dorokhov.pony.web.client.mvp.library.album;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Widget;
import com.gwtplatform.mvp.client.ViewImpl;

public class AlbumsView extends ViewImpl implements AlbumsPresenter.MyView {

	interface MyUiBinder extends UiBinder<Widget, AlbumsView> {}

	private static final MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

	public AlbumsView() {
		initWidget(uiBinder.createAndBindUi(this));
	}

}
