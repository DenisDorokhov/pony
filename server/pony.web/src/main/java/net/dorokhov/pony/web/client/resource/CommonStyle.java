package net.dorokhov.pony.web.client.resource;

import com.google.gwt.resources.client.CssResource;

public interface CommonStyle extends CssResource {

	public String propertySection();
	public String propertyLabel();
	public String propertyValue();

	public String scanJobStatus();

	public String scanJobStatusStarting();
	public String scanJobStatusStarted();
	public String scanJobStatusComplete();
	public String scanJobStatusFailed();
	public String scanJobStatusInterrupted();

	public String logMessageType();

	public String logMessageTypeDebug();
	public String logMessageTypeInfo();
	public String logMessageTypeWarn();
	public String logMessageTypeError();

}
