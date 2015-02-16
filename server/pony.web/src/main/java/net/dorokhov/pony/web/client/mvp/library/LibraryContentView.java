package net.dorokhov.pony.web.client.mvp.library;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.gwtplatform.mvp.client.ViewImpl;

public class LibraryContentView extends ViewImpl implements LibraryContentPresenter.MyView {

	interface MyUiBinder extends UiBinder<Widget, LibraryContentView> {}

	private static final MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

	@UiField
	SimplePanel artistsContainer;

	@UiField
	SimplePanel albumsContainer;

	public LibraryContentView() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	@Override
	public void setInSlot(Object aSlot, IsWidget aContent) {
		if (aSlot == LibraryContentPresenter.SLOT_ARTISTS) {
			artistsContainer.setWidget(aContent);
		} else if (aSlot == LibraryContentPresenter.SLOT_ALBUMS) {
			albumsContainer.setWidget(aContent);
		}
	}

}
