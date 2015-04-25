package net.dorokhov.pony.web.client.mvp.common;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import net.dorokhov.pony.web.client.resource.Messages;
import net.dorokhov.pony.web.shared.LogMessageDto;
import org.gwtbootstrap3.client.ui.Modal;

public class LogMessageView implements IsWidget {

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

	@Override
	public Widget asWidget() {
		return logMessageView;
	}

	private void updateLogMessage() {
		dateLabel.setText(getLogMessage() != null ? DATE_FORMAT.format(getLogMessage().getDate()) : null);
		typeLabel.setText(getLogMessage() != null ? typeToString(getLogMessage().getType()) : null);
		textLabel.setText(getLogMessage() != null ? getLogMessage().getText() : null);
		detailsLabel.setText(getLogMessage() != null ? getLogMessage().getDetails() : null);
	}

	private String typeToString(LogMessageDto.Type aType) {

		switch (aType) {
			case DEBUG:
				return Messages.INSTANCE.logTypeDebug();
			case INFO:
				return Messages.INSTANCE.logTypeInfo();
			case WARN:
				return Messages.INSTANCE.logTypeWarn();
			case ERROR:
				return Messages.INSTANCE.logTypeError();
		}

		return String.valueOf(aType);
	}

}
