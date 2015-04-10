package net.dorokhov.pony.web.client.mvp.library;

import com.gwtplatform.mvp.client.UiHandlers;

public interface ToolbarUiHandlers extends UiHandlers {

	public void onRefreshRequested();
	
	public void onSettingsRequested();
	public void onScanningRequested();
	public void onLogRequested();
	public void onUsersRequested();

	public void onLogoutRequested();

}
