package net.dorokhov.pony.web.client.service;

import net.dorokhov.pony.web.shared.SongDto;

import java.util.ArrayList;
import java.util.List;

public class PlayListNavigatorImpl implements PlayListNavigator, PlayList.Delegate {

	public static enum Mode {
		NORMAL, REPEAT_ALL, REPEAT_ONE
	}

	private static final int QUEUE_SIZE = 20;

	private Mode mode;

	private PlayList playList;

	private int currentIndex = 0;

	public PlayListNavigatorImpl() {
		this((Mode)null);
	}

	public PlayListNavigatorImpl(Mode aMode) {
		this(aMode, null);
	}

	public PlayListNavigatorImpl(PlayList aPlayList) {
		this(null, aPlayList);
	}

	public PlayListNavigatorImpl(Mode aMode, PlayList aPlayList) {
		setMode(aMode);
		setPlayList(aPlayList);
	}

	public Mode getMode() {

		if (mode == null) {
			mode = Mode.NORMAL;
		}

		return mode;
	}

	public void setMode(Mode aMode) {
		mode = aMode;
	}

	@Override
	public PlayList getPlayList() {
		return playList;
	}

	@Override
	public void setPlayList(PlayList aPlayList) {

		if (playList != aPlayList) {

			if (playList != null) {
				playList.removeDelegate(this);
			}

			playList = aPlayList;

			if (playList != null) {
				playList.addDelegate(this);
			}

			currentIndex = playList != null && playList.size() > 0 ? 0 : -1;
		}
	}

	@Override
	public List<SongDto> getQueue() {

		List<SongDto> queue = new ArrayList<>();

		if (playList != null) {

			int lastIndex = currentIndex;

			for (int i = 0; i < QUEUE_SIZE; i++) {

				Integer nextIndex = getNextIndex(lastIndex);

				if (nextIndex != null) {

					queue.add(playList.get(nextIndex));

					lastIndex = nextIndex;

				} else {
					break;
				}
			}
		}

		return queue;
	}

	@Override
	public SongDto getCurrent() {

		if (playList != null && currentIndex >= 0 && currentIndex < playList.size()) {
			return playList.get(currentIndex);
		}

		return null;
	}

	@Override
	public SongDto setCurrentIndex(int aIndex) {

		if (playList == null || aIndex < 0 || aIndex >= playList.size()) {
			throw new IndexOutOfBoundsException();
		}

		currentIndex = aIndex;

		return getCurrent();
	}

	@Override
	public boolean hasPrevious() {

		if (playList != null && playList.size() > 0) {
			if (getMode() == Mode.NORMAL) {
				return currentIndex > 0 && playList.size() > 1;
			} else {
				return true;
			}
		}

		return false;
	}

	@Override
	public boolean hasNext() {

		if (playList != null && playList.size() > 0) {
			if (getMode() == Mode.NORMAL) {
				return (currentIndex + 1) < playList.size();
			} else {
				return true;
			}
		}

		return false;
	}

	@Override
	public SongDto switchToPrevious() {

		Integer switchToIndex = getPreviousIndex(currentIndex);

		return switchToIndex != null ? setCurrentIndex(switchToIndex) : null;
	}

	@Override
	public SongDto switchToNext() {

		Integer switchToIndex = getNextIndex(currentIndex);

		return switchToIndex != null ? setCurrentIndex(switchToIndex) : null;
	}

	@Override
	public void onPlayListSongAdded(PlayList aPlayList, SongDto aSong, int aIndex) {
		if (playList == aPlayList) {

			if (currentIndex >= 0) {
				if (aIndex <= currentIndex) {
					currentIndex++;
				}
			} else {
				currentIndex = 0;
			}
		}
	}

	@Override
	public void onPlayListSongRemoved(PlayList aPlayList, SongDto aSong, int aIndex) {
		if (playList == aPlayList) {

			if (aIndex < currentIndex || (aIndex == currentIndex && currentIndex == playList.size())) {
				currentIndex--;
			}
		}
	}

	@Override
	public void onPlayListSongMoved(PlayList aPlayList, int aOldIndex, int aNewIndex) {
		if (playList == aPlayList) {

			if (currentIndex == aOldIndex) {
				currentIndex = aNewIndex;
			} else if (currentIndex == aNewIndex) {
				currentIndex = aOldIndex;
			}
		}
	}

	private Integer getPreviousIndex(int aIndex) {

		Integer index = null;

		if (playList != null && playList.size() > 0) {

			if (getMode() == Mode.NORMAL) {

				if (aIndex > 0 && playList.size() > 1) {
					index = aIndex - 1;
				}

			} else if (getMode() == Mode.REPEAT_ALL) {

				if (aIndex <= 0) {
					index = playList.size() - 1;
				} else {
					index = aIndex - 1;
				}

			} else if (getMode() == Mode.REPEAT_ONE) {
				index = aIndex;
			}
		}

		return index;
	}

	private Integer getNextIndex(int aIndex) {

		Integer index = null;

		if (playList != null && playList.size() > 0) {

			if (getMode() == Mode.NORMAL) {

				if ((aIndex + 1) < playList.size()) {
					index = aIndex + 1;
				}

			} else if (getMode() == Mode.REPEAT_ALL) {

				if ((aIndex + 1) >= playList.size()) {
					index = 0;
				} else {
					index = aIndex + 1;
				}

			} else if (getMode() == Mode.REPEAT_ONE) {
				index = aIndex;
			}
		}

		return index;
	}
}
