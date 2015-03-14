package net.dorokhov.pony.web.client.service;

import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import net.dorokhov.pony.web.client.resource.Messages;
import net.dorokhov.pony.web.client.util.ObjectUtils;
import net.dorokhov.pony.web.shared.SongDto;

public class TitleManager {

	private static final int START_DELAY = 4000;
	private static final int STEP_DELAY = 1000;

	private StringScroller scroller;

	private Timer timer;

	private SongDto song;

	public TitleManager() {
		setSong(null);
	}

	public SongDto getSong() {
		return song;
	}

	public void setSong(SongDto aSong) {

		SongDto oldSong = song;

		song = aSong;

		updateSong(oldSong);
	}

	private void updateSong(SongDto aOldSong) {

		if (song == null) {

			clearScrolling();

			Window.setTitle(Messages.INSTANCE.libraryTitle());

		} else if (!ObjectUtils.nullSafeEquals(song, aOldSong)) {

			clearScrolling();

			String artistName = song.getAlbum().getArtist().getName();
			if (artistName == null) {
				artistName = Messages.INSTANCE.artistUnknown();
			}

			String songTitle = song.getTitle();
			if (songTitle == null) {
				songTitle = Messages.INSTANCE.songUnknown();
			}

			scroller = new StringScroller(Messages.INSTANCE.libraryTitleSongBody(artistName, songTitle));

			Window.setTitle(Messages.INSTANCE.libraryTitleSongPrefix() + scroller.getResult());

			timer = new Timer() {
				@Override
				public void run() {

					int stepsCount = 1;

					String prefix = Messages.INSTANCE.libraryTitleSongPrefix();
					String body = scroller.getResult();

					int numberOfStartingSpaces = getNumberOfStartingChars(body, ' ');

					// spaces are automatically trimmed and united by the browser, here we avoid animation pause of repeating spaces
					if (prefix.endsWith(" ") && numberOfStartingSpaces > 0) {

						body = body.trim();

						stepsCount += numberOfStartingSpaces;
					}

					Window.setTitle(prefix + body);

					double oldOffset = scroller.getNormalizedOffset();

					scroller.setOffset(oldOffset + stepsCount * (1.0 / scroller.getTarget().length()));

					timer.schedule(scroller.getNormalizedOffset() > oldOffset ? STEP_DELAY : START_DELAY);
				}
			};

			timer.schedule(START_DELAY);
		}
	}

	private void clearScrolling() {

		if (timer != null) {

			timer.cancel();

			timer = null;
		}

		scroller = null;
	}

	private int getNumberOfStartingChars(String aSource, char aChar) {

		int result = 0;

		for (int i = 0; i < aSource.length(); i++) {
			if (aSource.charAt(i) == aChar) {
				result++;
			} else {
				return result;
			}
		}

		return result;
	}

}
