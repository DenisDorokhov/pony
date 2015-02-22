package net.dorokhov.pony.web.client.resource;

import com.google.gwt.core.client.GWT;

public interface Messages extends com.google.gwt.i18n.client.Messages {

	public static final Messages INSTANCE = GWT.create(Messages.class);

	public String errorTitle();
	public String errorText();
	public String errorHomeButton();

	public String loginTitle();

	public String libraryTitle();
	public String libraryTitleSongPrefix();
	public String libraryTitleSongBody(String aArtist, String aSong);

	public String errorsHeader();

	public String loginViewHeader();
	public String loginViewEmail();
	public String loginViewPassword();
	public String loginViewLoginButton();

	public String toolbarRefresh();
	public String toolbarSettings();

	public String libraryHeader();

	public String artistNameUnknown();
	
	public String statusLoading();
	public String statusError();

}
