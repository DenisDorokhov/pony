package net.dorokhov.pony.web.client.mvp.library;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.EndedEvent;
import com.google.gwt.event.dom.client.EndedHandler;
import com.google.gwt.media.client.Audio;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.gwtplatform.mvp.client.ViewWithUiHandlers;
import net.dorokhov.pony.web.client.control.ImageLoader;
import net.dorokhov.pony.web.client.resource.Messages;
import net.dorokhov.pony.web.client.util.StringUtils;
import net.dorokhov.pony.web.shared.SongDto;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.Progress;
import org.gwtbootstrap3.client.ui.ProgressBar;
import org.gwtbootstrap3.client.ui.constants.IconType;

public class PlayerView extends ViewWithUiHandlers<PlayerUiHandlers> implements PlayerPresenter.MyView, EndedHandler {

	interface MyUiBinder extends UiBinder<FlowPanel, PlayerView> {}

	private static final MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

	@UiField
	FlowPanel playerView;

	@UiField
	Button buttonBackward;

	@UiField
	Button buttonPlay;

	@UiField
	Button buttonForward;

	@UiField
	FocusPanel titleContainer;

	@UiField
	InlineLabel labelArtist;

	@UiField
	InlineLabel labelTitle;

	@UiField
	Progress progressTime;

	@UiField
	ProgressBar progressBarTime;

	@UiField
	Label labelTime;

	@UiField
	Label labelDuration;

	@UiField
	ImageLoader imageLoader;

	private final Audio audio;

	private State state;

	private SongDto song;

	private boolean previousSongAvailable;
	private boolean nextSongAvailable;

	public PlayerView() {

		initWidget(uiBinder.createAndBindUi(this));

		audio = Audio.createIfSupported();
		audio.addEndedHandler(this);

		playerView.add(audio);

		setUnityCallbacks();

		updateSong();

		updateUnityOptions();
		sendUnityState(false);

		setState(State.INACTIVE);

		progressTime.sinkEvents(Event.ONCLICK);
		progressTime.addHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent aEvent) {
				setProgress((aEvent.getNativeEvent().getClientX() - progressTime.getAbsoluteLeft()) / (double) progressTime.getOffsetWidth());
			}
		}, ClickEvent.getType());

		new Timer() {
			@Override
			public void run() {
				updatePosition();
			}
		}.scheduleRepeating(250);
	}

	@Override
	public State getState() {
		return state;
	}

	@Override
	public double getVolume() {
		return audio.getVolume();
	}

	@Override
	public void setVolume(double aVolume) {
		audio.setVolume(aVolume);
	}

	@Override
	public double getPosition() {
		return audio.getCurrentTime();
	}

	@Override
	public void setPosition(double aPosition) {

		audio.setCurrentTime(aPosition);

		updatePosition();
	}

	@Override
	public double getProgress() {

		double duration = audio.getDuration();

		if (Double.compare(duration, Double.NaN) != 0 && Double.compare(duration, Double.POSITIVE_INFINITY) != 0) {
			return audio.getCurrentTime() / duration;
		} else {
			return 0;
		}
	}

	@Override
	public void setProgress(double aProgress) {

		double duration = audio.getDuration();

		if (Double.compare(duration, Double.NaN) != 0 && Double.compare(duration, Double.POSITIVE_INFINITY) != 0) {
			setPosition(aProgress * duration);
		}
	}

	@Override
	public SongDto getSong() {
		return song;
	}

	@Override
	public void setSong(SongDto aSong, boolean aPlay) {

		song = aSong;

		updateSong();

		updateUnityOptions();

		if (aPlay) {
			play();
		} else {
			pause();
		}
	}

	@Override
	public void play() {

		audio.play();

		sendUnityState(true);

		setState(State.PLAYING);

		getUiHandlers().onPlay();
	}

	@Override
	public void pause() {

		audio.pause();

		sendUnityState(false);

		setState(State.PAUSED);

		getUiHandlers().onPause();
	}

	@Override
	public boolean isPreviousSongAvailable() {
		return previousSongAvailable;
	}

	@Override
	public void setPreviousSongAvailable(boolean aAvailable) {

		previousSongAvailable = aAvailable;

		updateUnityOptions();

		buttonBackward.setEnabled(previousSongAvailable);
	}

	@Override
	public boolean isNextSongAvailable() {
		return nextSongAvailable;
	}

	@Override
	public void setNextSongAvailable(boolean aAvailable) {

		nextSongAvailable = aAvailable;

		updateUnityOptions();

		buttonForward.setEnabled(nextSongAvailable);
	}

	@Override
	public void onEnded(EndedEvent aEvent) {

		setState(State.INACTIVE);

		sendUnityState(false);

		getUiHandlers().onEnd();
	}

	@UiHandler("buttonBackward")
	void onBackwardClick(ClickEvent aEvent) {
		onPreviousRequested();
	}

	@UiHandler("buttonForward")
	void onForwardClick(ClickEvent aEvent) {
		onNextRequested();
	}

	@UiHandler("buttonPlay")
	void onPlayClick(ClickEvent aEvent) {
		onPlayPause();
	}

	@UiHandler("titleContainer")
	void onTitleClick(ClickEvent aEvent) {
		getUiHandlers().onSongSelectionRequested();
	}

	private void updateSong() {

		String songUrl = null;
		String artistName = Messages.INSTANCE.playerTitle();
		String songName = Messages.INSTANCE.playerSubtitle();
		String artworkUrl = null;

		int duration = 0;

		if (song != null) {

			songUrl = song.getUrl();
			songName = song.getName();
			artworkUrl = song.getAlbum().getArtworkUrl();

			artistName = song.getArtistName();
			if (artistName == null) {
				artistName = song.getAlbumArtistName();
			}
			if (artistName == null) {
				artistName = Messages.INSTANCE.artistUnknown();
			}

			duration = song.getDuration() != null ? song.getDuration() : 0;
		}

		labelArtist.setText(artistName);
		labelTitle.setText(songName);
		imageLoader.setUrl(artworkUrl);
		labelDuration.setText(StringUtils.secondsToMinutes(duration));

		labelTime.setText(StringUtils.secondsToMinutes(0));
		progressBarTime.setPercent(0);

		if (songUrl != null) {
			audio.setSrc(songUrl);
		}
	}

	private void updatePosition() {
		labelTime.setText(StringUtils.secondsToMinutes((int) Math.round(getPosition())));
		progressBarTime.setPercent(getProgress() * 100);
	}

	private void setState(State aState) {

		state = aState;

		buttonPlay.setIcon(state == State.PLAYING ? IconType.PAUSE : IconType.PLAY);
	}

	private void onPlayPause() {
		if (getSong() != null) {
			if (getState() == State.PLAYING) {
				pause();
			} else {
				play();
			}
		} else {
			getUiHandlers().onPlaybackRequested();
		}
	}

	private void onPreviousRequested() {
		if (getPosition() >= 3) {
			setPosition(0.0);
		} else {
			getUiHandlers().onPreviousSongRequested();
		}
	}

	private void onNextRequested() {
		getUiHandlers().onNextSongRequested();
	}

	private void updateUnityOptions() {
		doUpdateUnityOptions(true, isPreviousSongAvailable(), isNextSongAvailable());
	}

	private native void doUpdateUnityOptions(boolean aCanPlay, boolean aPreviousSongAvailable, boolean aNextSongAvailable) /*-{
        $wnd.UnityMusicShim().setSupports({
            playpause: aCanPlay,
            next: aNextSongAvailable,
            previous: aPreviousSongAvailable
        });
    }-*/;

	private void sendUnityState(boolean aIsPlaying) {

		String songTitle = Messages.INSTANCE.playerSubtitle();
		String artistName = Messages.INSTANCE.playerTitle();
		String artworkUrl = null;

		if (getSong() != null) {

			songTitle = getSong().getName();
			artworkUrl = getSong().getAlbum().getArtworkUrl();

			artistName = getSong().getArtistName();
			if (artistName == null) {
				artistName = getSong().getAlbumArtistName();
			}
		}

		if (artworkUrl == null) {
			artworkUrl = GWT.getHostPageBaseURL() + "img/artwork-logo.png";
		}

		doSendUnityState(aIsPlaying, songTitle, artistName, artworkUrl);
	}

	private native void doSendUnityState(boolean aIsPlaying, String aName, String aArtist, String aArtwork) /*-{
        $wnd.UnityMusicShim().sendState({
            playing: aIsPlaying,
            title: aName,
            artist: aArtist,
            albumArt: aArtwork
        });
    }-*/;

	private native void setUnityCallbacks() /*-{

		var self = this;

        $wnd.UnityMusicShim().setCallbackObject({
            pause: function() {
                self.@net.dorokhov.pony.web.client.mvp.library.PlayerView::onPlayPause()();
            },
            next: function() {
                self.@net.dorokhov.pony.web.client.mvp.library.PlayerView::onNextRequested()();
            },
            previous: function() {
                self.@net.dorokhov.pony.web.client.mvp.library.PlayerView::onPreviousRequested()();
            }
        });
	}-*/;

}
