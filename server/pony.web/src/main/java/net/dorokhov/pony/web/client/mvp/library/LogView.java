package net.dorokhov.pony.web.client.mvp.library;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.web.bindery.event.shared.EventBus;
import net.dorokhov.pony.web.client.control.PagedListView;
import net.dorokhov.pony.web.client.mvp.common.ModalViewWithUiHandlers;
import net.dorokhov.pony.web.client.resource.Messages;
import net.dorokhov.pony.web.client.util.FormatUtils;
import net.dorokhov.pony.web.shared.LogMessageDto;
import net.dorokhov.pony.web.shared.ScanJobDto;
import org.gwtbootstrap3.client.ui.Modal;

import java.util.Arrays;
import java.util.List;

public class LogView extends ModalViewWithUiHandlers<LogUiHandlers> implements LogPresenter.MyView {

	interface MyUiBinder extends UiBinder<Modal, LogView> {}

	private static final MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

	private static final DateTimeFormat DATE_FORMAT = DateTimeFormat.getFormat(DateTimeFormat.PredefinedFormat.DATE_TIME_SHORT);

	@UiField(provided = true)
	PagedListView<LogMessageDto> messageView;

	public LogView(EventBus eventBus) {

		super(eventBus);

		final List<String> headers = Arrays.asList(
				Messages.INSTANCE.logColumnDate(),
				Messages.INSTANCE.logColumnType(),
				Messages.INSTANCE.logColumnText()
		);
		final List<String> widths = Arrays.asList(
				"150px", "150px", null
		);
		final List<TextColumn<LogMessageDto>> columns = Arrays.asList(
				new TextColumn<LogMessageDto>() {
					@Override
					public String getValue(LogMessageDto aMessage) {
						return DATE_FORMAT.format(aMessage.getDate());
					}
				},
				new TextColumn<LogMessageDto>() {
					@Override
					public String getValue(LogMessageDto aMessage) {
						return String.valueOf(aMessage.getType());
					}
				},
				new TextColumn<LogMessageDto>() {
					@Override
					public String getValue(LogMessageDto aMessage) {
						return FormatUtils.formatLog(aMessage);
					}
				}
		);

		// TODO: implement

		initWidget(uiBinder.createAndBindUi(this));
	}

	@Override
	public void reloadLogMessages(boolean aClearData) {
		messageView.reload(aClearData);
	}

}
