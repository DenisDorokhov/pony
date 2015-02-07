package net.dorokhov.pony.web.client.service;

import com.gwtplatform.dispatch.rest.shared.RestAction;
import net.dorokhov.pony.web.shared.CredentialsDto;
import net.dorokhov.pony.web.shared.ResponseDto;

import javax.ws.rs.POST;
import javax.ws.rs.Path;

@Path("/authenticate")
public interface AuthenticationResource {

	@POST
	RestAction<ResponseDto<String>> authenticate(CredentialsDto aCredentials);

}
