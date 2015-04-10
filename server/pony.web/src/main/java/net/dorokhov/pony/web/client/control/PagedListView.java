package net.dorokhov.pony.web.client.control;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.ui.*;
import net.dorokhov.pony.web.client.service.common.OperationCallback;
import net.dorokhov.pony.web.client.service.common.OperationRequest;
import net.dorokhov.pony.web.shared.ErrorDto;
import net.dorokhov.pony.web.shared.PagedListDto;
import org.gwtbootstrap3.client.ui.AnchorListItem;
import org.gwtbootstrap3.client.ui.Pager;
import org.gwtbootstrap3.client.ui.gwt.DataGrid;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class PagedListView<T> extends Composite implements HasWidgets {

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

	// Scrolling workaround from https://code.google.com/p/google-web-toolkit/issues/detail?id=6865
	private class MyDataGrid extends DataGrid<T> {
		public ScrollPanel getScrollPanel() {
			HeaderPanel header = (HeaderPanel) getWidget();
			return (ScrollPanel) header.getContentWidget();
		}
	}

	interface MyUiBinder extends UiBinder<Widget, PagedListView> {}

	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

	@UiField
	Pager pager;

	@UiField
	Label pagerLabel;

	@UiField(provided = true)
	MyDataGrid grid;

	@UiField
	FlowPanel loadingOverlay;

	@UiField
	FlowPanel bottomContainer;

	private final DataSource<T> dataSource;

	private State state;

	private PagedListDto<T> data;

	private OperationRequest currentRequest;

	public PagedListView(DataSource<T> aDataSource) {

		grid = new MyDataGrid();

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

		setState(State.LOADING);
	}

	public State getState() {
		return state;
	}

	public PagedListDto<T> getData() {
		return data;
	}

	public void clear() {

		setData(null);

		setState(State.LOADING);

		if (currentRequest != null) {
			currentRequest.cancel();
		}
	}

	public void reload() {
		reload(0);
	}

	public void reload(int aPageNumber) {
		requestPagedList(aPageNumber);
	}

	@Override
	public void add(Widget aWidget) {
		bottomContainer.add(aWidget);
	}

	@Override
	public Iterator<Widget> iterator() {
		return bottomContainer.iterator();
	}

	@Override
	public boolean remove(Widget aWidget) {
		return bottomContainer.remove(aWidget);
	}

	@UiHandler("refreshButton")
	void onRefreshClick(ClickEvent aEvent) {
		requestPagedList(getData() != null ? getData().getPageNumber() : 0);
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

			grid.getScrollPanel().scrollToTop();

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
