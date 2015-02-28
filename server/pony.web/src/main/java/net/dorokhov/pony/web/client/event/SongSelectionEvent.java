package net.dorokhov.pony.web.client.event;

import com.google.gwt.event.shared.EventHandler;
import net.dorokhov.pony.web.shared.SongDto;

import java.util.Set;

public class SongSelectionEvent extends AbstractEvent<SongSelectionEvent.Handler> {

	public interface Handler extends EventHandler {
		void onSongSelection(SongSelectionEvent event);
	}

	public static final Type<Handler> TYPE = new Type<>();

	private final Set<SongDto> songs;

	public SongSelectionEvent(Set<SongDto> aSongs) {

		super(TYPE);

		songs = aSongs;
	}

	public Set<SongDto> getSongs() {
		return songs;
	}

	@Override
	protected void dispatch(Handler aHandler) {
		aHandler.onSongSelection(this);
	}

}
