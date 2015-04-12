package net.dorokhov.pony.web.client.mvp.library;

import com.gwtplatform.mvp.client.UiHandlers;
import net.dorokhov.pony.web.shared.command.CreateUserCommandDto;
import net.dorokhov.pony.web.shared.command.UpdateUserCommandDto;

public interface UserEditUiHandlers extends UiHandlers {

	public void onCreationRequested(CreateUserCommandDto aCommand);
	public void onModificationRequested(UpdateUserCommandDto aCommand);

	public void onDeletionRequested();

}
