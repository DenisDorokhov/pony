package net.dorokhov.pony.web.client.service;

import java.util.Date;

public interface SecurityStorage {

	public String getAccessToken();

	public void setAccessToken(String aAccessToken);

	public Date getAccessTokenExpiration();

	public void setAccessTokenExpiration(Date aAccessTokenExpiration);

	public String getRefreshToken();

	public void setRefreshToken(String aRefreshToken);

}
