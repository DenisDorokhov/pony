package net.dorokhov.pony.web.client.service.api;

import com.google.gwt.http.client.Request;
import net.dorokhov.pony.web.shared.*;
import org.fusesource.restygwt.client.MethodCallback;
import org.fusesource.restygwt.client.Options;
import org.fusesource.restygwt.client.RestService;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import java.util.List;

@Options(expect = {200, 201, 204, 1223, 400, 401, 404, 500})
public interface ApiService extends RestService {

	@POST
	@Path("/authenticate")
	Request authenticate(CredentialsDto aCredentials, MethodCallback<ResponseDto<AuthenticationDto>> aCallback);

	@POST
	@Path("/logout")
	Request logout(MethodCallback<ResponseDto<UserDto>> aCallback);

	@GET
	@Path("/currentUser")
	Request getCurrentUser(MethodCallback<ResponseDto<UserDto>> aCallback);

	@POST
	@Path("/refreshToken")
	Request refreshToken(@HeaderParam("X-Refresh-Token") String aRefreshToken, MethodCallback<ResponseDto<AuthenticationDto>> aCallback);

	@GET
	@Path("/artists")
	Request getArtists(MethodCallback<ResponseDto<List<ArtistDto>>> aCallback);

}
