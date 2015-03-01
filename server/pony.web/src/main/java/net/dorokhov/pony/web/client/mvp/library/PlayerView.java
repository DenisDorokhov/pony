package net.dorokhov.pony.web.client.mvp.library;

import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.URL;
import com.google.gwt.media.client.Audio;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.FlowPanel;
import com.gwtplatform.mvp.client.ViewWithUiHandlers;
import net.dorokhov.pony.web.client.resource.Messages;
import net.dorokhov.pony.web.client.service.SecurityStorage;
import net.dorokhov.pony.web.shared.SongDto;

public class PlayerView extends ViewWithUiHandlers<PlayerUiHandlers> implements PlayerPresenter.MyView {

	interface MyUiBinder extends UiBinder<FlowPanel, PlayerView> {}

	private static final MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

	@UiField
	FlowPanel playerView;

	private final Audio audio;

	private State state;

	private SongDto song;

	private boolean previousSongAvailable;
	private boolean nextSongAvailable;

	public PlayerView() {

		initWidget(uiBinder.createAndBindUi(this));

		audio = Audio.createIfSupported();

		playerView.add(audio);

		initPlayer();
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

		if (song != null) {
			updateSong();
		}

		updateUnityOptions();
		sendUnityState(false);

		state = State.INACTIVE;
	}

	@Override
	public void play() {

	}

	@Override
	public void pause() {

	}

	@Override
	public boolean isPreviousSongAvailable() {
		return previousSongAvailable;
	}

	@Override
	public void setPreviousSongAvailable(boolean aAvailable) {

		previousSongAvailable = aAvailable;

		updateUnityOptions();
	}

	@Override
	public boolean isNextSongAvailable() {
		return nextSongAvailable;
	}

	@Override
	public void setNextSongAvailable(boolean aAvailable) {

		nextSongAvailable = aAvailable;

		updateUnityOptions();
	}

	private void initPlayer() {

		initNativeComponents();

		updateUnityOptions();
		sendUnityState(false);
	}

	private native void initNativeComponents() /*-{
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

		String name = Messages.INSTANCE.playerUnityNoTitle();
		String artist = null;
		String artworkUrl = null;

		if (getSong() != null) {

			name = getSong().getName();
			artist = getSong().getArtistName();
			artworkUrl = getSong().getArtworkUrl();

			if (artworkUrl != null) {
				artworkUrl += "?x_access_token=" + URL.encode(SecurityStorage.INSTANCE.getAccessToken());
			}
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
			if (state == State.PLAYING) {
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
