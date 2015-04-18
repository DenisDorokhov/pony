package net.dorokhov.pony.web.client.mvp.library.album;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.MultiSelectionModel;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;
import com.gwtplatform.mvp.client.ViewWithUiHandlers;
import net.dorokhov.pony.web.client.control.status.EmptyIndicator;
import net.dorokhov.pony.web.client.control.status.ErrorIndicator;
import net.dorokhov.pony.web.client.control.status.LoadingIndicator;
import net.dorokhov.pony.web.client.event.SongSelectionRequestEvent;
import net.dorokhov.pony.web.client.event.SongStartRequestEvent;
import net.dorokhov.pony.web.client.mvp.common.LoadingState;
import net.dorokhov.pony.web.client.mvp.common.SelectionMode;
import net.dorokhov.pony.web.client.resource.Messages;
import net.dorokhov.pony.web.shared.AlbumSongsDto;
import net.dorokhov.pony.web.shared.ArtistDto;
import net.dorokhov.pony.web.shared.SongDto;
import org.gwtbootstrap3.client.ui.Heading;
import org.gwtbootstrap3.client.ui.PanelHeader;

import java.util.*;

public class AlbumListView extends ViewWithUiHandlers<AlbumListUiHandlers> implements AlbumListPresenter.MyView, SongSelectionRequestEvent.Handler, SongStartRequestEvent.Handler {

	interface MyUiBinder extends UiBinder<Widget, AlbumListView> {}

	private static final MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

	private final List<AlbumView> viewCache = new ArrayList<>();

	private final Map<Long, AlbumView> albumToView = new HashMap<>();

	private final MultiSelectionModel<SongDto> selectionModel = new MultiSelectionModel<>();

	private final SingleSelectionModel<SongDto> activationModel = new SingleSelectionModel<>();

	@UiField
	PanelHeader artistHeaderContainer;

	@UiField
	Heading artistHeader;

	@UiField
	FlowPanel albumList;

	@UiField
	LoadingIndicator loadingIndicator;

	@UiField
	ErrorIndicator errorIndicator;

	@UiField
	EmptyIndicator emptyIndicator;

	private LoadingState loadingState;

	private ArtistDto artist;

	private List<AlbumSongsDto> albums;

	private boolean playing;

	public AlbumListView() {

		initWidget(uiBinder.createAndBindUi(this));

		selectionModel.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
			@Override
			public void onSelectionChange(SelectionChangeEvent event) {
				getUiHandlers().onSongSelection(selectionModel.getSelectedSet());
			}
		});
		activationModel.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
			@Override
			public void onSelectionChange(SelectionChangeEvent event) {
				getUiHandlers().onSongActivation(activationModel.getSelectedObject());
			}
		});

		for (int i = 0; i < 30; i++) {

			AlbumView albumView = new AlbumView();

			albumView.addSongSelectionRequestHandler(this);
			albumView.addSongStartRequestHandler(this);

			viewCache.add(albumView);
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

	@Override
	public ArtistDto getArtist() {
		return artist;
	}

	@Override
	public void setArtist(ArtistDto aArtist) {

		artist = aArtist;

		updateArtist();
	}

	@Override
	public List<AlbumSongsDto> getAlbums() {

		if (albums == null) {
			albums = new ArrayList<>();
		}

		return albums;
	}

	@Override
	public void setAlbums(List<AlbumSongsDto> aAlbums) {

		albums = aAlbums;

		updateAlbums();
	}

	@Override
	public Set<SongDto> getSelectedSongs() {
		return selectionModel.getSelectedSet();
	}

	@Override
	public void setSelectedSongs(Set<SongDto> aSongs) {

		for (SongDto song : selectionModel.getSelectedSet()) {
			selectionModel.setSelected(song, aSongs.contains(song));
		}

		for (SongDto song : aSongs) {
			selectionModel.setSelected(song, true);
		}
	}

	@Override
	public SongDto getActiveSong() {
		return activationModel.getSelectedObject();
	}

	@Override
	public void setActiveSong(SongDto aSong) {

		for (SongDto song : activationModel.getSelectedSet()) {
			activationModel.setSelected(song, song.equals(aSong));
		}

		activationModel.setSelected(aSong, true);
	}

	@Override
	public boolean isPlaying() {
		return playing;
	}

	@Override
	public void setPlaying(boolean aPlaying) {

		playing = aPlaying;

		for (Map.Entry<Long, AlbumView> entry : albumToView.entrySet()) {
			entry.getValue().setPlaying(playing);
		}
	}

	@Override
	public void selectSong(SongDto aSong, SelectionMode aSelectionMode) {

		Set<SongDto> songsToSelect = new HashSet<>();

		switch (aSelectionMode) {

			// TODO: support different selection modes

			default:
				songsToSelect.add(aSong);
				break;
		}

		setSelectedSongs(songsToSelect);
	}

	@Override
	public void scrollToTop() {
		// Delay scrolling, otherwise in IE scrolling will happen before showing loading spinner
		Scheduler.get().scheduleFinally(new Command() {
			@Override
			public void execute() {
				albumList.getElement().setScrollTop(0);
			}
		});
	}

	@Override
	public void scrollToSong(SongDto aSong) {

		AlbumView view = albumToView.get(aSong.getAlbum().getId());

		if (view != null) {
			view.scrollToSong(aSong);
		}
	}

	@Override
	public void onSongSelectionRequest(SongSelectionRequestEvent aEvent) {
		selectSong(aEvent.getSong(), aEvent.getSelectionMode());
	}

	@Override
	public void onSongStartRequest(SongStartRequestEvent aEvent) {
		if (!aEvent.getSong().equals(activationModel.getSelectedObject())) {
			activationModel.setSelected(aEvent.getSong(), true);
		} else {
			getUiHandlers().onSongActivation(aEvent.getSong());
		}
	}

	private void updateLoadingState() {
		emptyIndicator.setVisible(getLoadingState() == LoadingState.EMPTY);
		loadingIndicator.setVisible(getLoadingState() == LoadingState.LOADING);
		errorIndicator.setVisible(getLoadingState() == LoadingState.ERROR);
		albumList.setVisible(getLoadingState() == LoadingState.LOADED);
		artistHeaderContainer.setVisible(getLoadingState() == LoadingState.LOADED);
	}

	private void updateArtist() {

		String nameValue = null;

		if (getArtist() != null) {

			nameValue = getArtist().getName();

			if (nameValue == null) {
				nameValue = Messages.INSTANCE.artistUnknown();
			}
		}

		artistHeader.setText(nameValue);
	}

	private void updateAlbums() {

		while (albumList.getWidgetCount() > getAlbums().size()) {

			int i = albumList.getWidgetCount() - 1;

			AlbumView albumView = (AlbumView) albumList.getWidget(i);

			albumList.remove(i);

			albumView.setSelectionModel(null);
			albumView.setActivationModel(null);
			albumView.setPlaying(false);

			albumView.setAlbumSongs(null);

			viewCache.add(albumView);
		}

		albumToView.clear();

		for (int i = 0; i < getAlbums().size(); i++) {

			AlbumSongsDto albumSongs = getAlbums().get(i);

			AlbumView albumView;

			if (i < albumList.getWidgetCount()) {
				albumView = (AlbumView) albumList.getWidget(i);
			} else {

				albumView = viewCache.size() > 0 ? viewCache.remove(0) : null;

				if (albumView == null) {

					albumView = new AlbumView();

					albumView.addSongSelectionRequestHandler(this);
					albumView.addSongStartRequestHandler(this);
				}

				albumView.setSelectionModel(selectionModel);
				albumView.setActivationModel(activationModel);
				albumView.setPlaying(isPlaying());

				albumList.add(albumView);
			}

			albumView.setAlbumSongs(albumSongs);

			albumToView.put(albumSongs.getAlbum().getId(), albumView);
		}
	}

}
