package net.dorokhov.pony.web.client.mvp.library.album;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;
import com.gwtplatform.mvp.client.ViewWithUiHandlers;
import net.dorokhov.pony.web.client.mvp.common.LoadingState;
import net.dorokhov.pony.web.client.resource.Messages;
import net.dorokhov.pony.web.shared.AlbumSongsDto;
import net.dorokhov.pony.web.shared.ArtistDto;
import net.dorokhov.pony.web.shared.SongDto;
import org.gwtbootstrap3.client.ui.Heading;
import org.gwtbootstrap3.client.ui.PanelBody;
import org.gwtbootstrap3.client.ui.PanelHeader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AlbumListView extends ViewWithUiHandlers<AlbumListUiHandlers> implements AlbumListPresenter.MyView {

	interface MyUiBinder extends UiBinder<Widget, AlbumListView> {}

	private static final MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

	private final List<AlbumView> viewCache = new ArrayList<>();

	private final Map<Long, AlbumView> albumToView = new HashMap<>();

	private final SingleSelectionModel<SongDto> selectionModel = new SingleSelectionModel<>();
	private final SingleSelectionModel<SongDto> activationModel = new SingleSelectionModel<>();

	@UiField
	PanelHeader artistHeaderContainer;

	@UiField
	Heading artistHeader;

	@UiField
	PanelBody albumList;

	@UiField
	Label loadingLabel;

	@UiField
	Label errorLabel;

	private LoadingState loadingState;

	private ArtistDto artist;

	private List<AlbumSongsDto> albums;

	private boolean playing;

	public AlbumListView() {

		initWidget(uiBinder.createAndBindUi(this));

		selectionModel.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
			@Override
			public void onSelectionChange(SelectionChangeEvent event) {
				getUiHandlers().onSongSelection(selectionModel.getSelectedObject());
			}
		});
		activationModel.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
			@Override
			public void onSelectionChange(SelectionChangeEvent event) {
				getUiHandlers().onSongActivation(activationModel.getSelectedObject());
			}
		});

		for (int i = 0; i < 30; i++) {
			viewCache.add(new AlbumView());
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
	public SongDto getSelectedSong() {
		return selectionModel.getSelectedObject();
	}

	@Override
	public void setSelectedSong(SongDto aSong) {
		if (aSong != null) {
			selectionModel.setSelected(aSong, true);
		} else {
			if (selectionModel.getSelectedObject() != null) {
				selectionModel.setSelected(selectionModel.getSelectedObject(), false);
			}
		}
	}

	@Override
	public SongDto getActiveSong() {
		return activationModel.getSelectedObject();
	}

	@Override
	public void setActiveSong(SongDto aSong) {
		if (aSong != null) {
			activationModel.setSelected(aSong, true);
		} else {
			if (activationModel.getSelectedObject() != null) {
				activationModel.setSelected(activationModel.getSelectedObject(), false);
			}
		}
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
	public void scrollToTop() {
		albumList.getElement().setScrollTop(0);
	}

	@Override
	public void scrollToSong(SongDto aSong) {

		AlbumView view = albumToView.get(aSong.getAlbum());

		if (view != null) {
			view.scrollToSong(aSong);
		}
	}

	private void updateLoadingState() {
		loadingLabel.setVisible(getLoadingState() == LoadingState.LOADING);
		errorLabel.setVisible(getLoadingState() == LoadingState.ERROR);
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
