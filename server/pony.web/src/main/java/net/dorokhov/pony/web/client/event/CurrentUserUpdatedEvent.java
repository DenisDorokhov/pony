package net.dorokhov.pony.web.client.event;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import net.dorokhov.pony.web.shared.UserDto;

public class CurrentUserUpdatedEvent extends GwtEvent<CurrentUserUpdatedEvent.Handler> {

	public static interface Handler extends EventHandler {
		public void onCurrentUserUpdated(CurrentUserUpdatedEvent aEvent);
	}

	public static final GwtEvent.Type<Handler> TYPE = new GwtEvent.Type<>();

	private final UserDto user;

	public CurrentUserUpdatedEvent(UserDto aUser) {
		user = aUser;
	}

	public UserDto getUser() {
		return user;
	}

	@Override
	public GwtEvent.Type<Handler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(Handler aHandler) {
		aHandler.onCurrentUserUpdated(this);
	}

}
