package net.dorokhov.pony.web.shared;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class LogMessageDto extends AbstractDto<Long> {

	public static enum Type {
		DEBUG, INFO, WARN, ERROR
	}

	private Date date;

	private Type type;

	private String code;

	private String text;

	private String details;

	private List<String> arguments;

	public Date getDate() {
		return date;
	}

	public void setDate(Date aDate) {
		date = aDate;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type aType) {
		type = aType;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String aCode) {
		code = aCode;
	}

	public String getText() {
		return text;
	}

	public void setText(String aText) {
		text = aText;
	}

	public String getDetails() {
		return details;
	}

	public void setDetails(String aDetails) {
		details = aDetails;
	}

	public List<String> getArguments() {

		if (arguments == null) {
			arguments = new ArrayList<>();
		}

		return arguments;
	}

	public void setArguments(List<String> aArguments) {
		arguments = aArguments;
	}

}
