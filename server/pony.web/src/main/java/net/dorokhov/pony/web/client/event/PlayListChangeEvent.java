package net.dorokhov.pony.web.client.event;

import com.google.gwt.event.shared.EventHandler;
import net.dorokhov.pony.web.client.service.PlayList;

public class PlayListChangeEvent extends AbstractEvent<PlayListChangeEvent.Handler> {

	public static interface Handler extends EventHandler {
		public void onPlayListChange(PlayListChangeEvent aEvent);
	}

	public static final Type<Handler> TYPE = new Type<>();

	private final PlayList playList;

	private final int startIndex;

	public PlayListChangeEvent(PlayList aPlayList, int aStartIndex) {

		super(TYPE);

		playList = aPlayList;
		startIndex = aStartIndex;
	}

	public PlayList getPlayList() {
		return playList;
	}

	public int getStartIndex() {
		return startIndex;
	}

	@Override
	protected void dispatch(Handler aHandler) {
		aHandler.onPlayListChange(this);
	}

}
