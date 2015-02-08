package net.dorokhov.pony.web.client.service;

import net.dorokhov.pony.web.shared.AuthenticationDto;
import net.dorokhov.pony.web.shared.CredentialsDto;
import net.dorokhov.pony.web.shared.ResponseDto;
import net.dorokhov.pony.web.shared.UserDto;
import org.fusesource.restygwt.client.MethodCallback;
import org.fusesource.restygwt.client.Options;
import org.fusesource.restygwt.client.RestService;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

@Options(expect = {200, 201, 204, 1223, 400, 401, 404, 500})
public interface ApiService extends RestService {

	@POST
	@Path("/authenticate")
	void authenticate(CredentialsDto aCredentials, MethodCallback<ResponseDto<AuthenticationDto>> aCallback);

	@GET
	@Path("/currentUser")
	void getCurrentUser(MethodCallback<ResponseDto<UserDto>> aCallback);

}
