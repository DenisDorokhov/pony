package net.dorokhov.pony.core.service;

import net.dorokhov.pony.core.domain.LogMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Date;

public interface LogService {

	public LogMessage getById(Long aId);

	public Page<LogMessage> getByType(LogMessage.Type aMinimalType, Pageable aPageable);
	public Page<LogMessage> getByTypeAndDate(LogMessage.Type aMinimalType, Date aMinDate, Date aMaxDate, Pageable aPageable);

	public LogMessage debug(String aMessage);
	public LogMessage debug(String aMessage, Exception aException);
	public LogMessage debug(String aMessage, String aDetails);

	public LogMessage info(String aMessage);
	public LogMessage info(String aMessage, Exception aException);
	public LogMessage info(String aMessage, String aDetails);

	public LogMessage warn(String aMessage);
	public LogMessage warn(String aMessage, Exception aException);
	public LogMessage warn(String aMessage, String aDetails);

	public LogMessage error(String aMessage);
	public LogMessage error(String aMessage, Exception aException);
	public LogMessage error(String aMessage, String aDetails);
}
