package net.dorokhov.pony.web.client.mvp.library.artist;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Widget;
import com.gwtplatform.mvp.client.ViewImpl;
import org.gwtbootstrap3.client.ui.LinkedGroup;

public class ArtistsView extends ViewImpl implements ArtistsPresenter.MyView {

	interface MyUiBinder extends UiBinder<Widget, ArtistsView> {}

	private static final MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

	@UiField
	LinkedGroup artistList;

	public ArtistsView() {
		initWidget(uiBinder.createAndBindUi(this));
	}

}
