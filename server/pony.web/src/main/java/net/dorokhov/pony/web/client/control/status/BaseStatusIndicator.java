package net.dorokhov.pony.web.client.control.status;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Widget;

public class BaseStatusIndicator extends Composite implements StatusIndicator {

	interface MyUiBinder extends UiBinder<Widget, BaseStatusIndicator> {}

	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

	@UiField
	Image image;

	@UiField
	InlineLabel textLabel;

	private ImageResource icon;

	private String text;

	public BaseStatusIndicator() {

		initWidget(uiBinder.createAndBindUi(this));

		updateIcon();
		updateText();
	}

	@Override
	public ImageResource getIcon() {
		return icon;
	}

	@Override
	public void setIcon(ImageResource aIcon) {

		icon = aIcon;

		updateIcon();
	}

	@Override
	public String getText() {
		return text;
	}

	@Override
	public void setText(String aText) {

		text = aText;

		updateText();
	}

	private void updateIcon() {
		if (icon == null) {
			image.setVisible(false);
		} else {
			image.setVisible(true);
			image.setResource(icon);
		}
	}

	private void updateText() {
		if (text == null) {
			textLabel.setVisible(false);
		} else {
			textLabel.setVisible(true);
			textLabel.setText(text);
		}
	}

}
