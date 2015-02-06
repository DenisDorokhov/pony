package net.dorokhov.pony.web.shared;

import net.dorokhov.pony.core.domain.LogMessage;

import java.util.Date;

public class LogQueryDto {

	private LogMessage.Type type;

	private Date minDate;

	private Date maxDate;

	public LogMessage.Type getType() {
		return type;
	}

	public void setType(LogMessage.Type aType) {
		type = aType;
	}

	public Date getMinDate() {
		return minDate;
	}

	public void setMinDate(Date aMinDate) {
		minDate = aMinDate;
	}

	public Date getMaxDate() {
		return maxDate;
	}

	public void setMaxDate(Date aMaxDate) {
		maxDate = aMaxDate;
	}

}
