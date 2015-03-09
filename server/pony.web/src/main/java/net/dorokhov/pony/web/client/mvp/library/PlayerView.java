package net.dorokhov.pony.web.client.mvp.library;

import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.URL;
import com.google.gwt.media.client.Audio;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.gwtplatform.mvp.client.ViewWithUiHandlers;
import net.dorokhov.pony.web.client.control.ArtworkLoader;
import net.dorokhov.pony.web.client.resource.Messages;
import net.dorokhov.pony.web.client.service.SecurityStorage;
import net.dorokhov.pony.web.client.util.StringUtils;
import net.dorokhov.pony.web.shared.SongDto;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.ProgressBar;
import org.gwtbootstrap3.client.ui.constants.IconType;

public class PlayerView extends ViewWithUiHandlers<PlayerUiHandlers> implements PlayerPresenter.MyView {

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
	InlineLabel labelArtist;

	@UiField
	InlineLabel labelTitle;

	@UiField
	ProgressBar progressTime;

	@UiField
	Label labelTime;

	@UiField
	Label labelDuration;

	@UiField
	ArtworkLoader artworkLoader;

	private final Audio audio;

	private State state;

	private SongDto song;

	private boolean previousSongAvailable;
	private boolean nextSongAvailable;

	public PlayerView() {

		initWidget(uiBinder.createAndBindUi(this));

		audio = Audio.createIfSupported();

		playerView.add(audio);

		setUnityCallbacks();

		setSong(null);
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

		double duration = audio.getDuration();

		if (duration != Double.NaN && duration != Double.POSITIVE_INFINITY) {
			return audio.getCurrentTime() / duration;
		} else {
			return 0;
		}
	}

	@Override
	public void setPosition(double aPosition) {

		double duration = audio.getDuration();

		if (duration != Double.NaN && duration != Double.POSITIVE_INFINITY) {
			audio.setCurrentTime(aPosition * duration);
		}
	}

	@Override
	public SongDto getSong() {
		return song;
	}

	@Override
	public void setSong(SongDto aSong) {

		song = aSong;

		updateSong();

		updateUnityOptions();
		sendUnityState(false);

		setState(State.INACTIVE);
	}

	@Override
	public void play() {

		audio.play();

		setState(State.PLAYING);
	}

	@Override
	public void pause() {

		audio.pause();

		setState(State.PAUSED);
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

	private native void setUnityCallbacks() /*-{
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

	private void updateSong() {

		String songUrl = null;
		String artistName = Messages.INSTANCE.playerTitle();
		String songTitle = Messages.INSTANCE.playerSubtitle();
		String artworkUrl = null;

		int duration = 0;

		if (song != null) {
			songUrl = song.getUrl();
			artistName = song.getArtistName() != null ? song.getArtistName() : Messages.INSTANCE.artistUnknown();
			songTitle = song.getName() != null ? song.getName() : Messages.INSTANCE.songUnknown();
			artworkUrl = song.getArtworkUrl();
			duration = song.getDuration() != null ? song.getDuration() : 0;
		}

		labelArtist.setText(artistName);
		labelTitle.setText(songTitle);
		artworkLoader.setUrl(artworkUrl);
		labelDuration.setText(StringUtils.secondsToMinutes(duration));

		labelTime.setText(StringUtils.secondsToMinutes(0));
		progressTime.setPercent(0);

		audio.setSrc(addAccessTokenToUrl(songUrl));
	}

	private void setState(State aState) {

		state = aState;

		buttonPlay.setIcon(state == State.PLAYING ? IconType.PAUSE : IconType.PLAY);
	}

	private String addAccessTokenToUrl(String aUrl) {
		return aUrl != null ? aUrl + "?x_access_token=" + URL.encode(SecurityStorage.INSTANCE.getAccessToken()) : null;
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

		String name = Messages.INSTANCE.playerSubtitle();
		String artist = Messages.INSTANCE.playerTitle();
		String artworkUrl = null;

		if (getSong() != null) {
			name = getSong().getName();
			artist = getSong().getArtistName();
			artworkUrl = addAccessTokenToUrl(getSong().getArtworkUrl());
		}

		if (artworkUrl == null) {
			artworkUrl = GWT.getHostPageBaseURL() + "img/artwork-logo.png";
		}

		doSendUnityState(aIsPlaying, name, artist, artworkUrl);
	}

	private native void doSendUnityState(boolean aIsPlaying, String aName, String aArtist, String aArtwork) /*-{
        $wnd.UnityMusicShim().sendState({
            playing: aIsPlaying,
            title: aName,
            artist: aArtist,
            albumArt: aArtwork
        });
    }-*/;

	private void onPlayPause() {
		if (getSong() != null) {
			if (getState() == State.PLAYING) {
				pause();
			} else {
				play();
			}
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

}
