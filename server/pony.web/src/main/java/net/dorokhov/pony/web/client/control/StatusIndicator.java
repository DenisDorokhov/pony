package net.dorokhov.pony.web.client.control;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.*;

public class StatusIndicator extends Composite implements HasText {

	interface MyUiBinder extends UiBinder<Widget, StatusIndicator> {}

	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

	@UiField
	Image image;

	@UiField
	InlineLabel textLabel;

	private ImageResource icon;

	private String text;

	private String textColor;

	public StatusIndicator() {

		initWidget(uiBinder.createAndBindUi(this));

		updateIcon();
		updateText();
	}

	public ImageResource getIcon() {
		return icon;
	}

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

	public String getTextColor() {
		return textColor;
	}

	public void setTextColor(String aTextColor) {

		textColor = aTextColor;

		updateTextColor();
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

	private void updateTextColor() {
		if (textColor == null) {
			textLabel.getElement().getStyle().clearColor();
		} else {
			textLabel.getElement().getStyle().setColor(textColor);
		}
	}

}
