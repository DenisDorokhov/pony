package net.dorokhov.pony.web.client.mvp.common;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import net.dorokhov.pony.web.client.resource.Messages;
import net.dorokhov.pony.web.client.util.FormatUtils;
import net.dorokhov.pony.web.shared.LogMessageDto;
import org.gwtbootstrap3.client.ui.Anchor;

public class LogMessageCellView extends Composite {

	private final InlineLabel textLabel = new InlineLabel();

	private final Anchor detailsAnchor = new Anchor();;

	private LogMessageDto logMessage;

	public LogMessageCellView() {

		FlowPanel container = new FlowPanel();

		container.add(textLabel);
		container.add(detailsAnchor);

		detailsAnchor.setText(Messages.INSTANCE.logMessageColumnDetails());
		detailsAnchor.setVisible(false);

		initWidget(container);
	}

	public LogMessageCellView(LogMessageDto aLogMessage) {

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

	private void updateLogMessage() {
		if (getLogMessage() != null) {
			textLabel.setText(FormatUtils.formatLog(getLogMessage()) + " ");
			detailsAnchor.setVisible(getLogMessage().getDetails() != null);
		} else {
			detailsAnchor.setVisible(false);
		}
	}

}
