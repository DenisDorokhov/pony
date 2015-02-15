package net.dorokhov.pony.web.client.mvp;

import com.gwtplatform.mvp.client.UiHandlers;

public interface ToolbarUiHandlers extends UiHandlers {

	public void onSettingsRequested();

	public void onEditProfileRequested();

	public void onLogoutRequested();

}
