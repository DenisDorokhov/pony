package net.dorokhov.pony.web.domain;

import net.dorokhov.pony.core.domain.LogMessage;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class LogMessageDto implements Serializable {

	private Long id;

	private Date date;

	private LogMessage.Type type;

	private String code;

	private String text;

	private String details;

	private List<String> arguments;

	public Long getId() {
		return id;
	}

	public void setId(Long aId) {
		id = aId;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date aDate) {
		date = aDate;
	}

	public LogMessage.Type getType() {
		return type;
	}

	public void setType(LogMessage.Type aType) {
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
