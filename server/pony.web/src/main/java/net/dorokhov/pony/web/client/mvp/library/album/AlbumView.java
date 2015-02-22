package net.dorokhov.pony.web.client.mvp.library.album;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.SingleSelectionModel;
import net.dorokhov.pony.web.client.control.ImageLoader;
import net.dorokhov.pony.web.client.resource.Messages;
import net.dorokhov.pony.web.client.util.ObjectUtils;
import net.dorokhov.pony.web.shared.AlbumSongsDto;
import net.dorokhov.pony.web.shared.SongDto;
import org.gwtbootstrap3.client.ui.Heading;

public class AlbumView extends Composite {

	interface MyUiBinder extends UiBinder<Widget, AlbumView> {}

	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

	@UiField
	ImageLoader artworkImage;

	@UiField
	Heading titleHeader;

	private AlbumSongsDto albumSongs;

	private boolean playing;

	private SingleSelectionModel<SongDto> selectionModel;
	private SingleSelectionModel<SongDto> activationModel;

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

		// TODO: update children
	}

	public SingleSelectionModel<SongDto> getSelectionModel() {
		return selectionModel;
	}

	public void setSelectionModel(SingleSelectionModel<SongDto> aSelectionModel) {

		selectionModel = aSelectionModel;

		// TODO: update children
	}

	public SingleSelectionModel<SongDto> getActivationModel() {
		return activationModel;
	}

	public void setActivationModel(SingleSelectionModel<SongDto> aActivationModel) {

		activationModel = aActivationModel;

		// TODO: update children
	}

	public void scrollToSong(SongDto aSong) {
		// TODO: implement
	}

	private void updateAlbum() {

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
			artworkImage.setUrl(artworkValue);
		} else {
			artworkImage.clear();
		}
	}

}
