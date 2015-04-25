package net.dorokhov.pony.web.client.mvp.library;

import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;
import net.dorokhov.pony.web.client.control.PagedListView;
import net.dorokhov.pony.web.client.control.WidgetCell;
import net.dorokhov.pony.web.client.mvp.common.ModalViewWithUiHandlers;
import net.dorokhov.pony.web.client.resource.Messages;
import net.dorokhov.pony.web.client.service.common.OperationCallback;
import net.dorokhov.pony.web.client.service.common.OperationRequest;
import net.dorokhov.pony.web.shared.PagedListDto;
import net.dorokhov.pony.web.shared.UserDto;
import org.gwtbootstrap3.client.shared.event.ModalHiddenEvent;
import org.gwtbootstrap3.client.shared.event.ModalShownEvent;
import org.gwtbootstrap3.client.ui.Anchor;
import org.gwtbootstrap3.client.ui.Modal;
import org.gwtbootstrap3.client.ui.constants.ButtonType;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.gwtbootstrap3.client.ui.gwt.ButtonCell;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.List;

public class UserListView extends ModalViewWithUiHandlers<UserListUiHandlers> implements UserListPresenter.MyView {

	interface MyUiBinder extends UiBinder<Modal, UserListView> {}

	@SuppressWarnings("GwtCssResourceErrors")
	interface MyStyle extends CssResource {

		public String userRole();

		public String userRoleUser();
		public String userRoleAdmin();

		public String userNameOther();
		public String userNameCurrent();

	}

	private static final MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

	private static final DateTimeFormat DATE_FORMAT = DateTimeFormat.getFormat(Messages.INSTANCE.dateFormatTechnical());

	@UiField
	MyStyle style;

	@UiField(provided = true)
	PagedListView<UserDto> userPagedView;

	@Inject
	public UserListView(EventBus aEventBus) {

		super(aEventBus);

		initGrid();

		initWidget(uiBinder.createAndBindUi(this));
	}

	@Override
	public void reloadUsers() {
		userPagedView.reload();
	}

	@UiHandler("userListView")
	void onPagedListHidden(ModalHiddenEvent aEvent) {
		userPagedView.clear();
	}

	@UiHandler("userListView")
	void onPagedListShown(ModalShownEvent aEvent) {
		userPagedView.reload();
	}

	@UiHandler("userAddButton")
	void onAddButtonClick(ClickEvent aEvent) {
		getUiHandlers().onUserCreationRequested();
	}

	private void initGrid() {

		Column<UserDto, String> editColumn = new Column<UserDto, String>(new ButtonCell(ButtonType.DEFAULT, IconType.EDIT)) {
			@Override
			public String getValue(UserDto aUser) {
				return Messages.INSTANCE.userListButtonEdit();
			}
		};
		editColumn.setFieldUpdater(new FieldUpdater<UserDto, String>() {
			@Override
			public void update(int aIndex, UserDto aUser, String aValue) {
				getUiHandlers().onUserModificationRequester(aUser);
			}
		});

		final List<String> headers = Arrays.asList(
				Messages.INSTANCE.userListColumnCreationDate(),
				Messages.INSTANCE.userListColumnUpdateDate(),
				Messages.INSTANCE.userListColumnName(),
				Messages.INSTANCE.userListColumnEmail(),
				Messages.INSTANCE.userListColumnRole(),
				Messages.INSTANCE.userListColumnEdit()
		);
		final List<String> widths = Arrays.asList(
				"150px", "150px", null, "250px", "80px", "120px"
		);
		final List<Column<UserDto, ?>> columns = Arrays.asList(
				new TextColumn<UserDto>() {
					@Override
					public String getValue(UserDto aUser) {
						return DATE_FORMAT.format(aUser.getCreationDate());
					}
				},
				new TextColumn<UserDto>() {
					@Override
					public String getValue(UserDto aUser) {
						return aUser.getUpdateDate() != null ? DATE_FORMAT.format(aUser.getUpdateDate()) : "";
					}
				},
				new TextColumn<UserDto>() {
					@Override
					public String getValue(UserDto aUser) {
						return aUser.getName();
					}

					@Override
					public String getCellStyleNames(Cell.Context aContext, UserDto aUser) {
						return getUiHandlers().isCurrentUser(aUser) ? style.userNameCurrent() : style.userNameOther();
					}
				},
				new Column<UserDto, Widget>(new WidgetCell()) {
					@Override
					public Widget getValue(UserDto aUser) {
						return new Anchor(aUser.getEmail(), "mailto:" + aUser.getEmail());
					}
				},
				new TextColumn<UserDto>() {
					@Override
					public String getValue(UserDto aUser) {

						switch (aUser.getRole()) {
							case USER:
								return Messages.INSTANCE.userListRoleUser();
							case ADMIN:
								return Messages.INSTANCE.userListRoleAdmin();
						}

						return String.valueOf(aUser.getRole());
					}

					@Override
					public String getCellStyleNames(Cell.Context aContext, UserDto aUser) {

						String result = style.userRole() + " ";

						switch (aUser.getRole()) {
							case USER:
								result += style.userRoleUser();
								break;
							case ADMIN:
								result += style.userRoleAdmin();
								break;
						}

						return result;
					}
				},
				editColumn
		);

		userPagedView = new PagedListView<>(new PagedListView.DataSource<UserDto>() {

			@Override
			public int getColumnCount() {
				return columns.size();
			}

			@Override
			public Column<UserDto, ?> getColumn(int aIndex) {
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
			public String getPagerLabel(PagedListDto<UserDto> aPagedList) {
				return Messages.INSTANCE.userListPager(aPagedList.getPageNumber() + 1, aPagedList.getTotalPages(), aPagedList.getContent().size(), aPagedList.getTotalElements());
			}

			@Override
			public OperationRequest requestPagedList(int aPageNumber, OperationCallback<PagedListDto<UserDto>> aCallback) {
				return getUiHandlers().onUsersRequested(aPageNumber, aCallback);
			}
		});
	}

}
