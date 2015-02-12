package net.dorokhov.pony.web.client.event;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import net.dorokhov.pony.web.shared.UserDto;

public class UserLoggedOutEvent extends GwtEvent<UserLoggedOutEvent.Handler> {

	public static interface Handler extends EventHandler {
		public void onUserLoggedOut(UserLoggedOutEvent aEvent);
	}

	public static final Type<Handler> TYPE = new Type<>();

	private final UserDto user;

	public UserLoggedOutEvent(UserDto aUser) {
		user = aUser;
	}

	public UserDto getUser() {
		return user;
	}

	@Override
	public Type<Handler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(Handler aHandler) {
		aHandler.onUserLoggedOut(this);
	}

}
