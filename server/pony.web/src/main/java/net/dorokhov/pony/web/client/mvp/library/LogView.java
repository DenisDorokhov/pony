package net.dorokhov.pony.web.client.mvp.library;

import com.google.gwt.cell.client.Cell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.web.bindery.event.shared.EventBus;
import net.dorokhov.pony.web.client.control.PagedListView;
import net.dorokhov.pony.web.client.mvp.common.ModalViewWithUiHandlers;
import net.dorokhov.pony.web.client.resource.Messages;
import net.dorokhov.pony.web.client.service.common.OperationCallback;
import net.dorokhov.pony.web.client.service.common.OperationRequest;
import net.dorokhov.pony.web.client.util.FormatUtils;
import net.dorokhov.pony.web.shared.LogMessageDto;
import net.dorokhov.pony.web.shared.PagedListDto;
import org.gwtbootstrap3.client.shared.event.ModalHiddenEvent;
import org.gwtbootstrap3.client.shared.event.ModalShownEvent;
import org.gwtbootstrap3.client.ui.ListBox;
import org.gwtbootstrap3.client.ui.Modal;
import org.gwtbootstrap3.extras.datetimepicker.client.ui.DateTimePicker;
import org.gwtbootstrap3.extras.datetimepicker.client.ui.base.events.ChangeDateEvent;

import javax.inject.Inject;
import java.util.*;

public class LogView extends ModalViewWithUiHandlers<LogUiHandlers> implements LogPresenter.MyView {

	interface MyUiBinder extends UiBinder<Modal, LogView> {}

	@SuppressWarnings("GwtCssResourceErrors")
	interface MyStyle extends CssResource {

		String messageType();

		String messageTypeDebug();
		String messageTypeInfo();
		String messageTypeWarn();
		String messageTypeError();

	}

	private static final MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

	private static final DateTimeFormat DATE_FORMAT = DateTimeFormat.getFormat(Messages.INSTANCE.dateFormatTechnical());

	@UiField
	MyStyle style;

	@UiField(provided = true)
	PagedListView<LogMessageDto> logPagedView;

	@UiField
	ListBox typeFilter;

	@UiField
	DateTimePicker minDateFilter;

	@UiField
	DateTimePicker maxDateFilter;

	private final Map<Integer, LogMessageDto.Type> indexToType = new HashMap<>();

	@Inject
	public LogView(EventBus aEventBus) {

		super(aEventBus);

		initGrid();

		initWidget(uiBinder.createAndBindUi(this));

		typeFilter.addItem(Messages.INSTANCE.logTypeDebug());
		typeFilter.addItem(Messages.INSTANCE.logTypeInfo());
		typeFilter.addItem(Messages.INSTANCE.logTypeWarn());
		typeFilter.addItem(Messages.INSTANCE.logTypeError());

		indexToType.put(0, LogMessageDto.Type.DEBUG);
		indexToType.put(1, LogMessageDto.Type.INFO);
		indexToType.put(2, LogMessageDto.Type.WARN);
		indexToType.put(3, LogMessageDto.Type.ERROR);
	}

	@Override
	public LogMessageDto.Type getType() {
		return indexToType.get(typeFilter.getSelectedIndex());
	}

	@Override
	public Date getMinDate() {
		return minDateFilter.getValue();
	}

	@Override
	public Date getMaxDate() {
		return maxDateFilter.getValue();
	}

	@UiHandler("logView")
	void onPagedListHidden(ModalHiddenEvent aEvent) {
		logPagedView.clear();
	}

	@UiHandler("logView")
	void onPagedListShown(ModalShownEvent aEvent) {
		logPagedView.reload();
	}

	@UiHandler("typeFilter")
	void onTypeChange(ChangeEvent aEvent) {
		logPagedView.reload();
	}

	@UiHandler({"minDateFilter", "maxDateFilter"})
	void onDateChange(ChangeDateEvent aEvent) {
		logPagedView.reload();
	}

	private void initGrid() {

		final List<String> headers = Arrays.asList(
				Messages.INSTANCE.logColumnDate(),
				Messages.INSTANCE.logColumnType(),
				Messages.INSTANCE.logColumnText()
		);
		final List<String> widths = Arrays.asList(
				"150px", "80px", null
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

						switch (aMessage.getType()) {
							case DEBUG:
								return Messages.INSTANCE.logTypeDebug();
							case INFO:
								return Messages.INSTANCE.logTypeInfo();
							case WARN:
								return Messages.INSTANCE.logTypeWarn();
							case ERROR:
								return Messages.INSTANCE.logTypeError();
						}

						return String.valueOf(aMessage.getType());
					}

					@Override
					public String getCellStyleNames(Cell.Context aContext, LogMessageDto aMessage) {

						String result = style.messageType() + " ";

						switch (aMessage.getType()) {
							case DEBUG:
								result += style.messageTypeDebug();
								break;
							case INFO:
								result += style.messageTypeInfo();
								break;
							case WARN:
								result += style.messageTypeWarn();
								break;
							case ERROR:
								result += style.messageTypeError();
								break;
						}

						return result;
					}
				},
				new TextColumn<LogMessageDto>() {
					@Override
					public String getValue(LogMessageDto aMessage) {
						return FormatUtils.formatLog(aMessage);
					}
				}
		);

		logPagedView = new PagedListView<>(new PagedListView.DataSource<LogMessageDto>() {

			@Override
			public int getColumnCount() {
				return columns.size();
			}

			@Override
			public Column<LogMessageDto, String> getColumn(int aIndex) {
				return columns.get(aIndex);
			}

			@Override
			public String getColumnWidth(int aIndex) {
				return widths.get(aIndex);
			}

			@Override
			public String getHeader(int aIndex) {
				return headers.get(aIndex);
			}

			@Override
			public String getPagerLabel(PagedListDto<LogMessageDto> aPagedList) {
				return Messages.INSTANCE.logPager(aPagedList.getPageNumber() + 1, aPagedList.getTotalPages(), aPagedList.getContent().size(), aPagedList.getTotalElements());
			}

			@Override
			public OperationRequest requestPagedList(int aPageNumber, OperationCallback<PagedListDto<LogMessageDto>> aCallback) {
				return getUiHandlers().onLogMessagesRequested(aPageNumber, aCallback);
			}
		});
	}

}
