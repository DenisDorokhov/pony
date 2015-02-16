package net.dorokhov.pony.web.client.event;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

public abstract class AbstractEvent<T extends EventHandler> extends GwtEvent<T> {

	private Type<T> type;

	protected AbstractEvent(Type<T> aType) {
		type = aType;
	}

	@Override
	public Type<T> getAssociatedType() {
		return type;
	}

}
