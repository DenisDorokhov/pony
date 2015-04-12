package net.dorokhov.pony.web.client.event;

import com.google.gwt.event.shared.EventHandler;

public class UserDeletionEvent extends AbstractEvent<UserDeletionEvent.Handler> {

	public static interface Handler extends EventHandler {
		public void onUserDeletion(UserDeletionEvent aEvent);
	}

	public static final Type<Handler> TYPE = new Type<>();

	private final Long userId;

	public UserDeletionEvent(Long aUserId) {

		super(TYPE);

		userId = aUserId;
	}

	public Long getUserId() {
		return userId;
	}

	@Override
	protected void dispatch(Handler aHandler) {
		aHandler.onUserDeletion(this);
	}

}
