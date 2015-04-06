package net.dorokhov.pony.web.client.control;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import net.dorokhov.pony.web.client.resource.Messages;
import net.dorokhov.pony.web.client.service.common.OperationCallback;
import net.dorokhov.pony.web.client.service.common.OperationRequest;
import net.dorokhov.pony.web.shared.ErrorDto;
import net.dorokhov.pony.web.shared.PagedListDto;
import org.gwtbootstrap3.client.ui.AnchorListItem;
import org.gwtbootstrap3.client.ui.Pager;
import org.gwtbootstrap3.client.ui.gwt.DataGrid;

import java.util.ArrayList;
import java.util.List;

public class PagedListView<T> extends Composite {

	public interface DataSource<S> {

		public int getColumnCount();

		public Column<S, ?> getColumn(int aIndex);

		public String getColumnWidth(int aIndex);

		public String getHeader(int aIndex);

		public String getPagerLabel(PagedListDto<S> aPagedList);

		public OperationRequest requestPagedList(int aPageNumber, OperationCallback<PagedListDto<S>> aCallback);

	}

	public enum State {
		LOADING, ERROR, LOADED
	}

	interface MyUiBinder extends UiBinder<Widget, PagedListView> {}

	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

	@UiField
	Pager pager;

	@UiField
	Label pagerLabel;

	@UiField
	DataGrid<T> grid;

	@UiField
	FlowPanel loadingOverlay;

	private final DataSource<T> dataSource;

	private State state;

	private PagedListDto<T> data;

	private OperationRequest currentRequest;

	public PagedListView(DataSource<T> aDataSource) {

		initWidget(uiBinder.createAndBindUi(this));

		dataSource = aDataSource;

		pager.addPreviousClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if (getState() != State.LOADING) {
					requestPagedList(data.getPageNumber() - 1);
				}
			}
		});
		pager.addNextClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if (getState() != State.LOADING) {
					requestPagedList(data.getPageNumber() + 1);
				}
			}
		});

		for (int i = 0; i < dataSource.getColumnCount(); i++) {

			grid.addColumn(dataSource.getColumn(i), dataSource.getHeader(i));

			String width = dataSource.getColumnWidth(i);
			if (width != null) {
				grid.setColumnWidth(i, width);
			}
		}
	}

	public State getState() {
		return state;
	}

	public PagedListDto<T> getData() {
		return data;
	}

	public void reload() {
		reload(0, true);
	}

	public void reload(int aPageNumber) {
		reload(aPageNumber, true);
	}

	public void reload(boolean aClearData) {
		reload(0, aClearData);
	}

	public void reload(int aPageNumber, boolean aClearData) {

		if (aClearData) {
			setData(null);
		}

		requestPagedList(aPageNumber);
	}

	private void requestPagedList(int aPageNumber) {

		setState(State.LOADING);

		if (currentRequest != null) {
			currentRequest.cancel();
		}

		currentRequest = dataSource.requestPagedList(aPageNumber, new OperationCallback<PagedListDto<T>>() {

			@Override
			public void onSuccess(PagedListDto<T> aPage) {

				currentRequest = null;

				setData(aPage);

				setState(State.LOADED);
			}

			@Override
			public void onError(List<ErrorDto> aErrors) {

				currentRequest = null;

				setState(State.ERROR);
			}
		});
	}

	private void setState(State aState) {

		state = aState;

		loadingOverlay.setVisible(getState() == State.LOADING);
	}

	public void setData(PagedListDto<T> aData) {

		data = aData;

		if (data != null) {

			grid.setRowData(data.getContent());

			getPagerPrevious(pager).setEnabled(data.getPageNumber() > 0);
			getPagerNext(pager).setEnabled(data.getPageNumber() < data.getTotalPages() - 1);

			pagerLabel.setText(dataSource.getPagerLabel(data));

		} else {

			grid.setRowData(new ArrayList<T>());

			getPagerPrevious(pager).setEnabled(false);
			getPagerNext(pager).setEnabled(false);

			pagerLabel.setText(null);
		}
	}

	private AnchorListItem getPagerPrevious(Pager aPager) {
		return getPagerButtons(aPager).get(0);
	}

	private AnchorListItem getPagerNext(Pager aPager) {
		return getPagerButtons(aPager).get(1);
	}

	private List<AnchorListItem> getPagerButtons(Pager aPager) {

		List<AnchorListItem> buttons = new ArrayList<>();

		for (int i = 0; i < aPager.getWidgetCount(); i++) {

			Widget widget = aPager.getWidget(i);

			if (widget instanceof AnchorListItem) {
				buttons.add((AnchorListItem)widget);
			}
		}

		return buttons;
	}

}
