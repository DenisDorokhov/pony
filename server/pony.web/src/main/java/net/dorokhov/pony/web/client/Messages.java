package net.dorokhov.pony.web.client;

import com.google.gwt.core.client.GWT;

public interface Messages extends com.google.gwt.i18n.client.Messages {

	public static final Messages IMPL = GWT.create(Messages.class);

	public String titlePrefix();

	public String loginViewHeader();
	public String loginViewEmail();
	public String loginViewPassword();
	public String loginViewLoginButton();

}
