package net.dorokhov.pony.web.client.event;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import net.dorokhov.pony.web.shared.UserDto;

public class UserCreationEvent extends AbstractUserEvent<UserCreationEvent.Handler> {

	public static interface Handler extends EventHandler {
		public void onUserCreation(UserCreationEvent aEvent);
	}

	public static final GwtEvent.Type<Handler> TYPE = new GwtEvent.Type<>();

	public UserCreationEvent(UserDto aUser) {
		super(TYPE, aUser);
	}

	@Override
	protected void dispatch(Handler aHandler) {
		aHandler.onUserCreation(this);
	}

}
