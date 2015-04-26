package net.dorokhov.pony.web.client.mvp.common;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Label;
import net.dorokhov.pony.web.client.resource.Messages;
import net.dorokhov.pony.web.client.resource.Styles;
import net.dorokhov.pony.web.shared.LogMessageDto;
import org.gwtbootstrap3.client.ui.Modal;

public class LogMessageView {

	interface MyUiBinder extends UiBinder<Modal, LogMessageView> {}

	private static final MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

	private static final DateTimeFormat DATE_FORMAT = DateTimeFormat.getFormat(Messages.INSTANCE.dateFormatTechnical());

	@UiField
	Modal logMessageView;

	@UiField
	Label dateLabel;

	@UiField
	Label typeLabel;

	@UiField
	Label textLabel;

	@UiField
	Label detailsLabel;

	private LogMessageDto logMessage;

	public LogMessageView() {
		uiBinder.createAndBindUi(this);
	}

	public LogMessageView(LogMessageDto aLogMessage) {

		this();

		setLogMessage(aLogMessage);
	}

	public LogMessageDto getLogMessage() {
		return logMessage;
	}

	public void setLogMessage(LogMessageDto aLogMessage) {

		logMessage = aLogMessage;

		updateLogMessage();
	}

	public void show() {
		logMessageView.show();
	}

	public void hide() {
		logMessageView.hide();
	}

	private void updateLogMessage() {

		dateLabel.setText(getLogMessage() != null ? DATE_FORMAT.format(getLogMessage().getDate()) : null);
		textLabel.setText(getLogMessage() != null ? getLogMessage().getText() : null);
		detailsLabel.setText(getLogMessage() != null ? getLogMessage().getDetails() : null);

		String typeStyle = Styles.INSTANCE.commonStyle().propertyValue() + " " + Styles.INSTANCE.commonStyle().logMessageType() + " ";

		if (getLogMessage() != null) {
			switch (getLogMessage().getType()) {
				case DEBUG:
					typeLabel.setText(Messages.INSTANCE.logTypeDebug());
					typeStyle += Styles.INSTANCE.commonStyle().logMessageTypeDebug();
					break;
				case INFO:
					typeLabel.setText(Messages.INSTANCE.logTypeInfo());
					typeStyle += Styles.INSTANCE.commonStyle().logMessageTypeInfo();
					break;
				case WARN:
					typeLabel.setText(Messages.INSTANCE.logTypeWarn());
					typeStyle += Styles.INSTANCE.commonStyle().logMessageTypeWarn();
					break;
				case ERROR:
					typeLabel.setText(Messages.INSTANCE.logTypeError());
					typeStyle += Styles.INSTANCE.commonStyle().logMessageTypeError();
					break;
			}
		}

		typeLabel.setStyleName(typeStyle);
	}

}
