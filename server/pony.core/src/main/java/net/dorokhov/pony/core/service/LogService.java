package net.dorokhov.pony.core.service;

import net.dorokhov.pony.core.domain.LogMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Date;
import java.util.List;

public interface LogService {

	public Page<LogMessage> getByType(LogMessage.Type aMinimalType, Pageable aPageable);
	public Page<LogMessage> getByTypeAndDate(LogMessage.Type aMinimalType, Date aMinDate, Date aMaxDate, Pageable aPageable);

	public LogMessage debug(String aMessageCode);
	public LogMessage debug(String aMessageCode, List<String> aMessageArguments);

	public LogMessage debug(String aMessageCode, Throwable aThrowable);
	public LogMessage debug(String aMessageCode, Throwable aThrowable, List<String> aMessageArguments);

	public LogMessage debug(String aMessageCode, String aMessageDetails);
	public LogMessage debug(String aMessageCode, String aMessageDetails, List<String> aMessageArguments);

	public LogMessage info(String aMessageCode);
	public LogMessage info(String aMessageCode, List<String> aMessageArguments);

	public LogMessage info(String aMessageCode, Throwable aThrowable);
	public LogMessage info(String aMessageCode, Throwable aThrowable, List<String> aMessageArguments);

	public LogMessage info(String aMessageCode, String aMessageDetails);
	public LogMessage info(String aMessageCode, String aMessageDetails, List<String> aMessageArguments);

	public LogMessage warn(String aMessageCode);
	public LogMessage warn(String aMessageCode, List<String> aMessageArguments);

	public LogMessage warn(String aMessageCode, Exception aException);
	public LogMessage warn(String aMessageCode, Throwable aThrowable, List<String> aMessageArguments);

	public LogMessage warn(String aMessageCode, String aMessageDetails);
	public LogMessage warn(String aMessageCode, String aMessageDetails, List<String> aMessageArguments);

	public LogMessage error(String aMessageCode);
	public LogMessage error(String aMessageCode, List<String> aArguments);

	public LogMessage error(String aMessageCode, Throwable aThrowable);
	public LogMessage error(String aMessageCode, Throwable aThrowable, List<String> aMessageArguments);

	public LogMessage error(String aMessageCode, String aMessageDetails);
	public LogMessage error(String aMessageCode, String aMessageDetails, List<String> aMessageArguments);
}
