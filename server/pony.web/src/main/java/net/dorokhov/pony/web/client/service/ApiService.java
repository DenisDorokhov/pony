package net.dorokhov.pony.web.client.service;

import net.dorokhov.pony.web.shared.CredentialsDto;
import net.dorokhov.pony.web.shared.ResponseDto;
import org.fusesource.restygwt.client.MethodCallback;
import org.fusesource.restygwt.client.Options;
import org.fusesource.restygwt.client.RestService;

import javax.ws.rs.POST;
import javax.ws.rs.Path;

@Options(expect = {200, 201, 204, 1223, 400, 401, 404, 500})
public interface ApiService extends RestService {

	@POST
	@Path("/authenticate")
	void authenticate(CredentialsDto aCredentials, MethodCallback<ResponseDto<String>> aCallback);

}
