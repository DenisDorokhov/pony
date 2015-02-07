package net.dorokhov.pony.web.client.mvp;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import com.gwtplatform.mvp.client.ViewImpl;

public class LibraryView extends ViewImpl implements LibraryPresenter.MyView {

	interface MyUiBinder extends UiBinder<Widget, LibraryView> {}

	private static final MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

	@UiField
	FlowPanel playerContainer;

	@UiField
	SimpleLayoutPanel contentContainer;

	public LibraryView() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	@Override
	public void setInSlot(Object aSlot, IsWidget aContent) {
		if (aSlot == LibraryPresenter.SLOT_PLAYER) {
			playerContainer.add(aContent);
		} else if (aSlot == LibraryPresenter.SLOT_CONTENT) {
			contentContainer.setWidget(aContent);
		}
	}

}
