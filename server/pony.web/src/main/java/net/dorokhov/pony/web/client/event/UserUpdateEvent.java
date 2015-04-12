package net.dorokhov.pony.web.client.event;

import com.google.gwt.event.shared.EventHandler;
import net.dorokhov.pony.web.shared.UserDto;

public class UserUpdateEvent extends AbstractUserEvent<UserUpdateEvent.Handler> {

	public static interface Handler extends EventHandler {
		public void onUserUpdate(UserUpdateEvent aEvent);
	}

	public static final Type<Handler> TYPE = new Type<>();

	public UserUpdateEvent(UserDto aUser) {
		super(TYPE, aUser);
	}

	@Override
	protected void dispatch(Handler aHandler) {
		aHandler.onUserUpdate(this);
	}

}
