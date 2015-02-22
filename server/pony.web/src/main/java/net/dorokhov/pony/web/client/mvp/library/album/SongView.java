package net.dorokhov.pony.web.client.mvp.library.album;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import net.dorokhov.pony.web.client.util.ObjectUtils;
import net.dorokhov.pony.web.client.util.StringUtils;
import net.dorokhov.pony.web.shared.SongDto;

public class SongView extends Composite {

	interface MyUiBinder extends UiBinder<Widget, SongView> {}

	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

	@UiField
	Label trackNumberLabel;

	@UiField
	Label nameLabel;

	@UiField
	Label durationLabel;

	private SongDto song;

	private boolean selected;

	private boolean activated;

	private boolean playing;

	public SongView() {

		initWidget(uiBinder.createAndBindUi(this));

		setSelected(false);
		setActivated(false);
		setPlaying(false);
	}

	public SongDto getSong() {
		return song;
	}

	public void setSong(SongDto aSong) {

		song = aSong;

		updateSong();
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean aSelected) {

		selected = aSelected;

		updateStyle();
	}

	public boolean isActivated() {
		return activated;
	}

	public void setActivated(boolean aActivated) {

		activated = aActivated;

		updateStyle();
	}

	public boolean isPlaying() {
		return playing;
	}

	public void setPlaying(boolean aPlaying) {

		playing = aPlaying;

		updateStyle();
	}

	private void updateSong() {
		trackNumberLabel.setText(song != null ? ObjectUtils.nullSafeToString(song.getTrackNumber()) : null);
		nameLabel.setText(song != null ? song.getName() : null);
		durationLabel.setText(song != null ? StringUtils.secondsToMinutes(song.getDuration()) : null);
	}

	private void updateStyle() {
		// TODO: implement
	}

}
