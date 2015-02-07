package net.dorokhov.pony.web.client.mvp;

import com.gwtplatform.mvp.client.UiHandlers;
import net.dorokhov.pony.web.shared.CredentialsDto;

public interface LoginUiHandlers extends UiHandlers {

	public void onLoginRequested(CredentialsDto aCredentials);

}
