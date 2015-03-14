package net.dorokhov.pony.web.client.mvp.library;

import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Window;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.PresenterWidget;
import com.gwtplatform.mvp.client.View;
import net.dorokhov.pony.web.client.event.*;
import net.dorokhov.pony.web.client.mvp.common.SelectionMode;
import net.dorokhov.pony.web.client.resource.Messages;
import net.dorokhov.pony.web.client.service.PlayListNavigator;
import net.dorokhov.pony.web.client.service.PlayListNavigatorImpl;
import net.dorokhov.pony.web.shared.SongDto;

import javax.inject.Inject;
import java.util.logging.Logger;

public class PlayerPresenter extends PresenterWidget<PlayerPresenter.MyView> implements PlayerUiHandlers, PlayListChangeEvent.Handler, Window.ClosingHandler {

	public interface MyView extends View, HasUiHandlers<PlayerUiHandlers> {

		public static enum State {
			INACTIVE, PLAYING, PAUSED, ERROR
		}

		public State getState();

		public double getVolume();

		public void setVolume(double aVolume);

		public double getPosition();

		public void setPosition(double aPosition);

		public double getProgress();

		public void setProgress(double aProgress);

		public SongDto getSong();

		public void setSong(SongDto aSong, boolean aPlay);

		public void play();
		public void pause();

		public boolean isPreviousSongAvailable();
		public void setPreviousSongAvailable(boolean aAvailable);

		public boolean isNextSongAvailable();
		public void setNextSongAvailable(boolean aAvailable);

	}

	private final Logger log = Logger.getLogger(getClass().getName());

	private final PlayListNavigator playListNavigator = new PlayListNavigatorImpl(PlayListNavigatorImpl.Mode.NORMAL);

	private HandlerRegistration closingWindowRegistration;

	@Inject
	public PlayerPresenter(EventBus aEventBus, MyView aView) {

		super(aEventBus, aView);

		getView().setUiHandlers(this);

		getView().setPreviousSongAvailable(playListNavigator.hasPrevious());
		getView().setNextSongAvailable(playListNavigator.hasNext());
	}

	@Override
	protected void onBind() {

		super.onBind();

		addRegisteredHandler(PlayListChangeEvent.TYPE, this);
	}

	@Override
	protected void onReveal() {

		super.onReveal();

		closingWindowRegistration = Window.addWindowClosingHandler(this);
	}

	@Override
	protected void onHide() {

		super.onHide();

		closingWindowRegistration.removeHandler();
	}

	@Override
	public void onPlay() {

		log.info("Playback started for song " + getView().getSong() + ".");

		getEventBus().fireEvent(new SongStartEvent(getView().getSong()));
	}

	@Override
	public void onPause() {

		log.fine("Playback paused for song " + getView().getSong() + ".");

		getEventBus().fireEvent(new SongPauseEvent(getView().getSong()));
	}

	@Override
	public void onEnd() {

		log.fine("Playback ended for song " + getView().getSong() + ".");

		getEventBus().fireEvent(new SongEndEvent(getView().getSong()));

		playNextSong();
	}

	@Override
	public void onError() {
		log.fine("Playback failed for song " + getView().getSong() + ".");
	}

	@Override
	public void onPreviousSongRequested() {
		playPreviousSong();
	}

	@Override
	public void onNextSongRequested() {
		playNextSong();
	}

	@Override
	public void onSongSelectionRequested() {
		if (getView().getSong() != null) {
			getEventBus().fireEvent(new SongSelectionRequestEvent(getView().getSong(), SelectionMode.SINGLE));
		}
	}

	@Override
	public void onPlaybackRequested() {
		getEventBus().fireEvent(new PlaybackRequestEvent());
	}

	@Override
	public void onPlayListChange(PlayListChangeEvent aEvent) {

		playListNavigator.setPlayList(aEvent.getPlayList());
		playListNavigator.setCurrentIndex(aEvent.getStartIndex());

		getView().setPreviousSongAvailable(false);
		getView().setNextSongAvailable(false);

		playCurrentSong();
	}

	@Override
	public void onWindowClosing(Window.ClosingEvent aEvent) {
		if (getView().getState() == MyView.State.PLAYING) {
			aEvent.setMessage(Messages.INSTANCE.playerAlertPlaybackWillStop());
		}
	}

	private void playCurrentSong() {

		SongDto song = playListNavigator.getCurrent();

		if (song != null) {
			doPlaySong(song);
		}
	}

	private void playPreviousSong() {

		SongDto song = playListNavigator.switchToPrevious();

		if (song != null) {
			doPlaySong(song);
		}
	}

	private void playNextSong() {

		SongDto song = playListNavigator.switchToNext();

		if (song != null) {
			doPlaySong(song);
		}
	}

	private void doPlaySong(SongDto aSong) {

		getView().setPreviousSongAvailable(playListNavigator.hasPrevious());
		getView().setNextSongAvailable(playListNavigator.hasNext());

		getView().setSong(aSong, true);

		getEventBus().fireEvent(new SongChangeEvent(aSong));
	}

}
