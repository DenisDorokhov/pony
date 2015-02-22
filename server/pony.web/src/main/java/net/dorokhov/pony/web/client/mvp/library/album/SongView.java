package net.dorokhov.pony.web.client.mvp.library.album;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import net.dorokhov.pony.web.shared.SongDto;

public class SongView extends Composite {

	interface MyUiBinder extends UiBinder<Widget, SongView> {}

	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

	private SongDto song;

	private boolean selected;

	private boolean activated;

	private boolean playing;

	public SongView() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	public SongDto getSong() {
		return song;
	}

	public void setSong(SongDto aSong) {
		song = aSong;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean aSelected) {
		selected = aSelected;
	}

	public boolean isActivated() {
		return activated;
	}

	public void setActivated(boolean aActivated) {
		activated = aActivated;
	}

	public boolean isPlaying() {
		return playing;
	}

	public void setPlaying(boolean aPlaying) {
		playing = aPlaying;
	}

}
