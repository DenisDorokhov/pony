package net.dorokhov.pony.web.client.event;

import com.google.gwt.event.shared.EventHandler;
import net.dorokhov.pony.web.shared.ConfigDto;

public class ConfigUpdateEvent extends AbstractEvent<ConfigUpdateEvent.Handler> {

	public static interface Handler extends EventHandler {
		public void onConfigUpdate(ConfigUpdateEvent aEvent);
	}

	public static final Type<Handler> TYPE = new Type<>();

	private final ConfigDto oldConfig;

	private final ConfigDto newConfig;

	public ConfigUpdateEvent(ConfigDto aOldConfig, ConfigDto aNewConfig) {

		super(TYPE);

		oldConfig = aOldConfig;
		newConfig = aNewConfig;
	}

	public ConfigDto getOldConfig() {
		return oldConfig;
	}

	public ConfigDto getNewConfig() {
		return newConfig;
	}

	@Override
	protected void dispatch(Handler aHandler) {
		aHandler.onConfigUpdate(this);
	}

}
