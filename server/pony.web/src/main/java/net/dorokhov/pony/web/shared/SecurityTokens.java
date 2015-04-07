package net.dorokhov.pony.web.shared;

public class SecurityTokens {

	public static final String ACCESS_TOKEN_HEADER = "X-Pony-Access-Token";
	public static final String REFRESH_TOKEN_HEADER = "X-Pony-Refresh-Token";

	public static final String ACCESS_TOKEN_PARAM = "pony_access_token";
	public static final String ACCESS_TOKEN_COOKIE = "PONY_ACCESS_TOKEN";

	private SecurityTokens() {}

}
