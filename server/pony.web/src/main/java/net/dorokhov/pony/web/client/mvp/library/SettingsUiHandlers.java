package net.dorokhov.pony.web.client.mvp.library;

import com.gwtplatform.mvp.client.UiHandlers;
import net.dorokhov.pony.web.shared.ConfigDto;

public interface SettingsUiHandlers extends UiHandlers {

	public void onSaveRequested(ConfigDto aConfig);

}
