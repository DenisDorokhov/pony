package net.dorokhov.pony.web.client.mvp.library.album;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Widget;
import com.gwtplatform.mvp.client.ViewImpl;

public class AlbumListView extends ViewImpl implements AlbumListPresenter.MyView {

	interface MyUiBinder extends UiBinder<Widget, AlbumListView> {}

	private static final MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

	public AlbumListView() {
		initWidget(uiBinder.createAndBindUi(this));
	}

}
