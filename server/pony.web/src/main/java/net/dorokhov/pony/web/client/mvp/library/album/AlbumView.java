package net.dorokhov.pony.web.client.mvp.library.album;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.SingleSelectionModel;
import net.dorokhov.pony.web.client.control.ArtworkLoader;
import net.dorokhov.pony.web.client.resource.Messages;
import net.dorokhov.pony.web.client.util.ObjectUtils;
import net.dorokhov.pony.web.shared.AlbumSongsDto;
import net.dorokhov.pony.web.shared.SongDto;
import org.gwtbootstrap3.client.ui.Heading;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class AlbumView extends Composite {

	interface MyUiBinder extends UiBinder<Widget, AlbumView> {}

	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

	private static final List<SongListView> viewCache = new ArrayList<>();

	@UiField
	ArtworkLoader artworkLoader;

	@UiField
	Heading titleHeader;

	@UiField
	FlowPanel songList;

	private AlbumSongsDto albumSongs;

	private boolean playing;

	private SingleSelectionModel<SongDto> selectionModel;
	private SingleSelectionModel<SongDto> activationModel;

	public AlbumView() {

		initWidget(uiBinder.createAndBindUi(this));

		for (int i = 0; i < 50; i++) {
			viewCache.add(new SongListView());
		}
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

		for (Widget widget : songList) {
			if (widget instanceof SongListView) {
				((SongListView) widget).setPlaying(playing);
			}
		}
	}

	public SingleSelectionModel<SongDto> getSelectionModel() {
		return selectionModel;
	}

	public void setSelectionModel(SingleSelectionModel<SongDto> aSelectionModel) {

		selectionModel = aSelectionModel;

		for (Widget widget : songList) {
			if (widget instanceof SongListView) {
				((SongListView) widget).setSelectionModel(selectionModel);
			}
		}
	}

	public SingleSelectionModel<SongDto> getActivationModel() {
		return activationModel;
	}

	public void setActivationModel(SingleSelectionModel<SongDto> aActivationModel) {

		activationModel = aActivationModel;

		for (Widget widget : songList) {
			if (widget instanceof SongListView) {
				((SongListView) widget).setActivationModel(activationModel);
			}
		}
	}

	public void scrollToSong(SongDto aSong) {
		for (int i = 0; i < songList.getWidgetCount(); i++) {

			SongListView songListView = (SongListView) songList.getWidget(i);

			songListView.scrollToSong(aSong);
		}
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
			artworkLoader.setUrl(artworkValue);
		} else {
			artworkLoader.clear();
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

			viewCache.add(songListView);
		}

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

				songList.add(songListView);
			}

			Integer discNumber = entry.getKey();

			if (discNumber != null && discNumber == 1 && albumDiscs.size() == 1) {
				discNumber = null;
			}

			songListView.setSongs(entry.getValue());

			songListView.setCaption(discNumber != null ? Messages.INSTANCE.albumDisc(discNumber) : null);

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
