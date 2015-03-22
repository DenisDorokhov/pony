package net.dorokhov.pony.web.client.mvp.library.artist;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;
import com.gwtplatform.mvp.client.ViewWithUiHandlers;
import net.dorokhov.pony.web.client.control.status.EmptyIndicator;
import net.dorokhov.pony.web.client.control.status.ErrorIndicator;
import net.dorokhov.pony.web.client.control.status.LoadingIndicator;
import net.dorokhov.pony.web.client.mvp.common.LoadingState;
import net.dorokhov.pony.web.client.service.LinkBuilder;
import net.dorokhov.pony.web.shared.ArtistDto;
import org.gwtbootstrap3.client.ui.LinkedGroup;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ArtistListView extends ViewWithUiHandlers<ArtistListUiHandlers> implements ArtistListPresenter.MyView {

	interface MyUiBinder extends UiBinder<Widget, ArtistListView> {}

	private static final MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

	private final List<ArtistView> viewCache = new ArrayList<>();
	
	private final Map<ArtistDto, ArtistView> artistToView = new HashMap<>();

	private final SingleSelectionModel<ArtistDto> selectionModel = new SingleSelectionModel<>();

	private final LinkBuilder linkBuilder;
	
	@UiField
	LinkedGroup artistList;
	
	@UiField
	LoadingIndicator loadingIndicator;
	
	@UiField
	ErrorIndicator errorIndicator;

	@UiField
	EmptyIndicator emptyIndicator;

	private List<ArtistDto> artists;

	private LoadingState loadingState;

	@Inject
	public ArtistListView(LinkBuilder aLinkBuilder) {

		linkBuilder = aLinkBuilder;
		
		initWidget(uiBinder.createAndBindUi(this));
		
		selectionModel.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
			@Override
			public void onSelectionChange(SelectionChangeEvent event) {

				updateArtistViews();

				ArtistDto artist = selectionModel.getSelectedObject();

				getUiHandlers().onArtistSelection(artist);
			}
		});

		for (int i = 0; i < 200; i++) {
			viewCache.add(createArtistView());
		}

		setLoadingState(LoadingState.LOADING);
	}

	@Override
	public List<ArtistDto> getArtists() {
		
		if (artists == null) {
			artists = new ArrayList<>();
		}
		
		return artists;
	}

	@Override
	public void setArtists(List<ArtistDto> aArtists) {

		artists = aArtists;

		updateArtists();
	}

	@Override
	public ArtistDto getSelectedArtist() {
		return selectionModel.getSelectedObject();
	}

	@Override
	public void setSelectedArtist(ArtistDto aArtist, boolean aShouldScroll) {

		if (aArtist != null) {
			selectionModel.setSelected(aArtist, true);
		} else {
			selectionModel.clear();
		}

		if (aShouldScroll && aArtist != null) {

			final ArtistView artistView = artistToView.get(aArtist);

			Scheduler.get().scheduleFinally(new Command() {
				@Override
				public void execute() {
					artistView.getElement().scrollIntoView();
				}
			});
		}
	}

	@Override
	public LoadingState getLoadingState() {
		return loadingState;
	}

	@Override
	public void setLoadingState(LoadingState aLoadingState) {

		loadingState = aLoadingState;
		
		updateLoadingState();
	}

	private void updateArtists() {

		while (artistList.getWidgetCount() > getArtists().size()) {

			int i = artistList.getWidgetCount() - 1;

			ArtistView artistView = (ArtistView) artistList.getWidget(i);
			
			artistList.remove(i);
			
			artistView.setArtist(null);

			viewCache.add(artistView);
		}
		
		artistToView.clear();

		for (int i = 0; i < getArtists().size(); i++) {

			ArtistDto artist = getArtists().get(i);

			ArtistView artistView;
			if (i < artistList.getWidgetCount()) {
				artistView = (ArtistView) artistList.getWidget(i);
			} else {

				artistView = viewCache.size() > 0 ? viewCache.remove(0) : null;

				if (artistView == null) {
					artistView = createArtistView();
				}

				artistList.add(artistView);
			}

			artistView.setArtist(artist);
			artistView.setLink("#" + linkBuilder.buildLinkToArtist(artist));

			artistToView.put(artist, artistView);
		}

		updateArtistViews();
	}

	private void updateArtistViews() {
		for (Map.Entry<ArtistDto, ArtistView> entry : artistToView.entrySet()) {
			entry.getValue().setActive(selectionModel.isSelected(entry.getKey()));
		}
	}

	private void updateLoadingState() {
		emptyIndicator.setVisible(getLoadingState() == LoadingState.EMPTY);
		loadingIndicator.setVisible(getLoadingState() == LoadingState.LOADING);
		errorIndicator.setVisible(getLoadingState() == LoadingState.ERROR);
		artistList.setVisible(getLoadingState() == LoadingState.LOADED);
	}

	private ArtistView createArtistView() {

		final ArtistView artistView = new ArtistView();

		artistView.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent aEvent) {

				aEvent.preventDefault();

				if (aEvent.getNativeButton() != NativeEvent.BUTTON_MIDDLE) {
					selectionModel.setSelected(artistView.getArtist(), true);
				} else {
					Window.open(artistView.getLink(), "_blank", "");
				}
			}
		});

		return artistView;
	}

}
