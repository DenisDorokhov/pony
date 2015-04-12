package net.dorokhov.pony.web.client.event;

import com.google.gwt.event.shared.EventHandler;
import net.dorokhov.pony.web.shared.UserDto;

public abstract class AbstractUserEvent<T extends EventHandler> extends AbstractEvent<T> {

	private final UserDto user;

	public AbstractUserEvent(Type<T> aType, UserDto aUser) {

		super(aType);

		user = aUser;
	}

	public UserDto getUser() {
		return user;
	}

}
