package net.dorokhov.pony.web.client.control.status;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;

public class EmptyIndicator extends Composite implements StatusIndicator {

	interface MyUiBinder extends UiBinder<BaseStatusIndicator, EmptyIndicator> {}

	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

	@UiField
	BaseStatusIndicator statusIndicator;

	public EmptyIndicator() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	@Override
	public ImageResource getIcon() {
		return statusIndicator.getIcon();
	}

	@Override
	public void setIcon(ImageResource aIcon) {
		statusIndicator.setIcon(aIcon);
	}

	@Override
	public String getText() {
		return statusIndicator.getText();
	}

	@Override
	public void setText(String aText) {
		statusIndicator.setText(aText);
	}

}
