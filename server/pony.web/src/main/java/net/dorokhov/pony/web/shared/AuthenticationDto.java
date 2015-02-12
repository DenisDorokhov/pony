package net.dorokhov.pony.web.shared;

import java.util.Date;

public class AuthenticationDto {

	private String accessToken;

	private Date accessTokenExpiration;

	private String refreshToken;

	private Date refreshTokenExpiration;

	private UserDto user;

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String aAccessToken) {
		accessToken = aAccessToken;
	}

	public Date getAccessTokenExpiration() {
		return accessTokenExpiration;
	}

	public void setAccessTokenExpiration(Date aAccessTokenExpiration) {
		accessTokenExpiration = aAccessTokenExpiration;
	}

	public String getRefreshToken() {
		return refreshToken;
	}

	public void setRefreshToken(String aRefreshToken) {
		refreshToken = aRefreshToken;
	}

	public Date getRefreshTokenExpiration() {
		return refreshTokenExpiration;
	}

	public void setRefreshTokenExpiration(Date aRefreshTokenExpiration) {
		refreshTokenExpiration = aRefreshTokenExpiration;
	}

	public UserDto getUser() {
		return user;
	}

	public void setUser(UserDto aUser) {
		user = aUser;
	}

}
