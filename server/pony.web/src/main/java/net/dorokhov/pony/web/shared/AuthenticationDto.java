package net.dorokhov.pony.web.shared;

public class AuthenticationDto {

	private String token;

	private UserDto user;

	public String getToken() {
		return token;
	}

	public void setToken(String aToken) {
		token = aToken;
	}

	public UserDto getUser() {
		return user;
	}

	public void setUser(UserDto aUser) {
		user = aUser;
	}

}
