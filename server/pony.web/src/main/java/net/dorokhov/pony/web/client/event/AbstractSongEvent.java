package net.dorokhov.pony.web.client.event;

import com.google.gwt.event.shared.EventHandler;
import net.dorokhov.pony.web.shared.SongDto;

public abstract class AbstractSongEvent<T extends EventHandler> extends AbstractEvent<T> {

	private final SongDto song;

	public AbstractSongEvent(Type<T> aType, SongDto aSong) {

		super(aType);

		song = aSong;
	}

	public SongDto getSong() {
		return song;
	}

}
