package net.dorokhov.pony.web.shared;

import java.util.Date;

public class LogQueryDto {

	private LogMessageDto.Type type;

	private Date minDate;

	private Date maxDate;

	public LogMessageDto.Type getType() {
		return type;
	}

	public void setType(LogMessageDto.Type aType) {
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
