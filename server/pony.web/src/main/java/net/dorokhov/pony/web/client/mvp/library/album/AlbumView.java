package net.dorokhov.pony.web.client.mvp.library.album;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.SetSelectionModel;
import net.dorokhov.pony.web.client.control.ImageLoader;
import net.dorokhov.pony.web.client.event.SongSelectionRequestEvent;
import net.dorokhov.pony.web.client.event.SongStartRequestEvent;
import net.dorokhov.pony.web.client.resource.Messages;
import net.dorokhov.pony.web.client.util.ObjectUtils;
import net.dorokhov.pony.web.shared.AlbumSongsDto;
import net.dorokhov.pony.web.shared.SongDto;
import org.gwtbootstrap3.client.ui.Heading;

import java.util.*;

public class AlbumView extends Composite implements SongSelectionRequestEvent.HasHandler, SongSelectionRequestEvent.Handler,
		SongStartRequestEvent.HasHandler, SongStartRequestEvent.Handler {

	interface MyUiBinder extends UiBinder<Widget, AlbumView> {}

	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

	private static final List<SongListView> viewCache = new ArrayList<>();

	static {
		for (int i = 0; i < 50; i++) {
			viewCache.add(new SongListView());
		}
	}

	private final List<SongListView> songListViews = new ArrayList<>();

	private final Map<SongListView, HandlerRegistration> songListViewToSelectionRegistration = new HashMap<>();
	private final Map<SongListView, HandlerRegistration> songListViewToActivationRegistration = new HashMap<>();

	private final HandlerManager handlerManager = new HandlerManager(this);

	@UiField
	ImageLoader imageLoader;

	@UiField
	Heading titleHeader;

	@UiField
	FlowPanel songList;

	private AlbumSongsDto albumSongs;

	private boolean playing;

	private SetSelectionModel<SongDto> selectionModel;
	private SetSelectionModel<SongDto> activationModel;

	public AlbumView() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	public AlbumSongsDto getAlbumSongs() {
		return albumSongs;
	}

	public void setAlbumSongs(AlbumSongsDto aAlbumSongs) {

		albumSongs = aAlbumSongs;

		updateAlbum();
	}

	public boolean isPlaying() {
		return playing;
	}

	public void setPlaying(boolean aPlaying) {

		playing = aPlaying;

		for (SongListView songView : songListViews) {
			songView.setPlaying(playing);
		}
	}

	public SetSelectionModel<SongDto> getSelectionModel() {
		return selectionModel;
	}

	public void setSelectionModel(SetSelectionModel<SongDto> aSelectionModel) {

		selectionModel = aSelectionModel;

		for (SongListView songView : songListViews) {
			songView.setSelectionModel(selectionModel);
		}
	}

	public SetSelectionModel<SongDto> getActivationModel() {
		return activationModel;
	}

	public void setActivationModel(SetSelectionModel<SongDto> aActivationModel) {

		activationModel = aActivationModel;

		for (SongListView songView : songListViews) {
			songView.setActivationModel(activationModel);
		}
	}

	public void scrollToSong(SongDto aSong) {
		for (SongListView songView : songListViews) {
			songView.scrollToSong(aSong);
		}
	}

	@Override
	public HandlerRegistration addSongSelectionRequestHandler(SongSelectionRequestEvent.Handler aHandler) {
		return handlerManager.addHandler(SongSelectionRequestEvent.TYPE, aHandler);
	}

	@Override
	public void onSongSelectionRequest(SongSelectionRequestEvent aEvent) {
		handlerManager.fireEvent(aEvent);
	}

	@Override
	public HandlerRegistration addSongStartRequestHandler(SongStartRequestEvent.Handler aHandler) {
		return handlerManager.addHandler(SongStartRequestEvent.TYPE, aHandler);
	}

	@Override
	public void onSongStartRequest(SongStartRequestEvent aEvent) {
		handlerManager.fireEvent(aEvent);
	}

	private void updateAlbum() {

		updateSongLists();

		String nameValue = null;
		String yearValue = null;
		String artworkValue = null;

		if (getAlbumSongs() != null) {

			nameValue = getAlbumSongs().getAlbum().getName();

			if (nameValue == null) {
				nameValue = Messages.INSTANCE.albumUnknown();
			}

			yearValue = ObjectUtils.nullSafeToString(getAlbumSongs().getAlbum().getYear());

			if (yearValue == null) {
				yearValue = "";
			}

			artworkValue = getAlbumSongs().getAlbum().getArtworkUrl();
		}

		titleHeader.setText(nameValue);
		titleHeader.setSubText(yearValue);

		if (artworkValue != null) {
			imageLoader.setUrl(artworkValue);
		} else {
			imageLoader.clear();
		}
	}

	private void updateSongLists() {

		Map<Integer, List<SongDto>> albumDiscs = splitIntoDiscs(getAlbumSongs());

		while (songList.getWidgetCount() > albumDiscs.size()) {

			int i = songList.getWidgetCount() - 1;

			SongListView songListView = (SongListView) songList.getWidget(i);

			songList.remove(i);

			songListView.setSelectionModel(null);
			songListView.setActivationModel(null);
			songListView.setPlaying(false);

			songListView.setSongs(null);

			songListViewToSelectionRegistration.get(songListView).removeHandler();
			songListViewToSelectionRegistration.remove(songListView);

			songListViewToActivationRegistration.get(songListView).removeHandler();
			songListViewToActivationRegistration.remove(songListView);

			viewCache.add(songListView);
		}

		songListViews.clear();

		int i = 0;

		for (Map.Entry<Integer, List<SongDto>> entry : albumDiscs.entrySet()) {

			SongListView songListView;

			if (i < songList.getWidgetCount()) {
				songListView = (SongListView) songList.getWidget(i);
			} else {

				songListView = viewCache.size() > 0 ? viewCache.remove(0) : null;

				if (songListView == null) {
					songListView = new SongListView();
				}

				songListView.setSelectionModel(getSelectionModel());
				songListView.setActivationModel(getActivationModel());
				songListView.setPlaying(isPlaying());

				songListViewToSelectionRegistration.put(songListView, songListView.addSongSelectionRequestHandler(this));
				songListViewToActivationRegistration.put(songListView, songListView.addSongStartRequestHandler(this));

				songList.add(songListView);
			}

			Integer discNumber = entry.getKey();

			if (discNumber != null && discNumber == 1 && albumDiscs.size() == 1) {
				discNumber = null;
			}

			songListView.setSongs(entry.getValue());
			songListView.setCaption(discNumber != null ? Messages.INSTANCE.albumDisc(discNumber) : null);

			songListViews.add(songListView);

			i++;
		}
	}

	private Map<Integer, List<SongDto>> splitIntoDiscs(AlbumSongsDto aAlbum) {

		List<SongDto> songs = aAlbum != null ? aAlbum.getSongs() : new ArrayList<SongDto>();

		Map<Integer, List<SongDto>> result = new LinkedHashMap<>();

		for (SongDto song : songs) {

			Integer discNumber = song.getDiscNumber();

			if (discNumber == null) {
				discNumber = 1;
			}

			List<SongDto> discSongs = result.get(discNumber);

			if (discSongs == null) {

				discSongs = new ArrayList<>();

				result.put(discNumber, discSongs);
			}

			discSongs.add(song);
		}

		return result;
	}

}
