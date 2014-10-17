package net.dorokhov.pony.core.service;

import net.dorokhov.pony.core.domain.LogMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Date;
import java.util.List;

public interface LogService {

	public long getCount();

	public Page<LogMessage> getByType(LogMessage.Type aMinimalType, Pageable aPageable);
	public Page<LogMessage> getByTypeAndDate(LogMessage.Type aMinimalType, Date aMinDate, Date aMaxDate, Pageable aPageable);

	public LogMessage debug(String aCode, String aText);
	public LogMessage debug(String aCode, String aText, List<String> aArguments);

	public LogMessage debug(String aCode, String aText, Throwable aThrowable);
	public LogMessage debug(String aCode, String aText, Throwable aThrowable, List<String> aArguments);

	public LogMessage debug(String aCode, String aText, String aDetails);
	public LogMessage debug(String aCode, String aText, String aDetails, List<String> aArguments);

	public LogMessage info(String aCode, String aText);
	public LogMessage info(String aCode, String aText, List<String> aArguments);

	public LogMessage info(String aCode, String aText, Throwable aThrowable);
	public LogMessage info(String aCode, String aText, Throwable aThrowable, List<String> aArguments);

	public LogMessage info(String aCode, String aText, String aDetails);
	public LogMessage info(String aCode, String aText, String aDetails, List<String> aArguments);

	public LogMessage warn(String aCode, String aText);
	public LogMessage warn(String aCode, String aText, List<String> aArguments);

	public LogMessage warn(String aCode, String aText, Exception aException);
	public LogMessage warn(String aCode, String aText, Throwable aThrowable, List<String> aArguments);

	public LogMessage warn(String aCode, String aText, String aDetails);
	public LogMessage warn(String aCode, String aText, String aDetails, List<String> aArguments);

	public LogMessage error(String aCode, String aText);
	public LogMessage error(String aCode, String aText, List<String> aArguments);

	public LogMessage error(String aCode, String aText, Throwable aThrowable);
	public LogMessage error(String aCode, String aText, Throwable aThrowable, List<String> aArguments);

	public LogMessage error(String aCode, String aText, String aDetails);
	public LogMessage error(String aCode, String aText, String aDetails, List<String> aArguments);

	public void deleteAll();
}
