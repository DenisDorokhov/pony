package net.dorokhov.pony.web.client.mvp.library;

import com.gwtplatform.mvp.client.UiHandlers;

public interface PlayerUiHandlers extends UiHandlers {

	public void onPlay();
	public void onPause();
	public void onEnd();
	public void onError();

	public void onPreviousSongRequested();
	public void onNextSongRequested();
	public void onSongSelectionRequested();

}
