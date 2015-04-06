package net.dorokhov.pony.web.client.control;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
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

		public String getHeader(int aIndex);

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
	DataGrid<T> grid;

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
				requestPagedList(data.getPageNumber() - 1);
			}
		});
		pager.addNextClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				requestPagedList(data.getPageNumber() + 1);
			}
		});

		for (int i = 0; i < dataSource.getColumnCount(); i++) {
			grid.addColumn(dataSource.getColumn(i), dataSource.getHeader(i));
		}
	}

	public State getState() {
		return state;
	}

	public PagedListDto<T> getData() {
		return data;
	}

	public void reset() {
		setData(null);
		requestPagedList(0);
	}

	@Override
	protected void onAttach() {

		super.onAttach();

		if (getData() == null) {
			requestPagedList(0);
		}
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

		// TODO: update loading state
	}

	public void setData(PagedListDto<T> aData) {

		data = aData;

		if (data != null) {

			grid.setRowData(data.getContent());

			getPagerPrevious(pager).setEnabled(data.getPageNumber() > 0);
			getPagerNext(pager).setEnabled(data.getPageNumber() < data.getTotalPages() - 1);

		} else {

			grid.setRowData(new ArrayList<T>());

			getPagerPrevious(pager).setEnabled(false);
			getPagerNext(pager).setEnabled(false);
		}
	}

	private AnchorListItem getPagerPrevious(Pager aPager) {
		return (AnchorListItem)aPager.getWidget(aPager.getWidgetCount() - 2);
	}

	private AnchorListItem getPagerNext(Pager aPager) {
		return (AnchorListItem)aPager.getWidget(aPager.getWidgetCount() - 1);
	}

}
