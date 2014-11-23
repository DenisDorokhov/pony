package net.dorokhov.pony.core.logging;

import net.dorokhov.pony.core.dao.LogMessageDao;
import net.dorokhov.pony.core.domain.LogMessage;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class LogServiceImpl implements LogService {

	private LogMessageDao logMessageDao;

	@Autowired
	public void setLogMessageDao(LogMessageDao aLogMessageDao) {
		logMessageDao = aLogMessageDao;
	}

	@Override
	@Transactional(readOnly = true)
	public long getCount() {
		return logMessageDao.count();
	}

	@Override
	@Transactional(readOnly = true)
	public Page<LogMessage> getByType(LogMessage.Type aType, Pageable aPageable) {
		return logMessageDao.findByTypeGreaterThanEqual(aType, aPageable);
	}

	@Override
	@Transactional(readOnly = true)
	public Page<LogMessage> getByTypeAndDate(LogMessage.Type aType, Date aMinDate, Date aMaxDate, Pageable aPageable) {
		return logMessageDao.findByTypeGreaterThanEqualAndDateBetween(aType, aMinDate, aMaxDate, aPageable);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public LogMessage debug(Logger aLogger, String aCode, String aText) {
		return debug(aLogger, aCode, aText, (String)null);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public LogMessage debug(Logger aLogger, String aCode, String aText, List<String> aArguments) {
		return debug(aLogger, aCode, aText, (String)null, aArguments);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public LogMessage debug(Logger aLogger, String aCode, String aText, Throwable aThrowable) {
		return debug(aLogger, aCode, aText, aThrowable, null);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public LogMessage debug(Logger aLogger, String aCode, String aText, Throwable aThrowable, List<String> aArguments) {

		if (aThrowable == null) {
			throw new NullPointerException("Exception must not be null.");
		}

		return debug(aLogger, aCode, aText, ExceptionUtils.getStackTrace(aThrowable).trim(), aArguments);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public LogMessage debug(Logger aLogger, String aCode, String aText, String aDetails) {
		return debug(aLogger, aCode, aText, aDetails, null);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public LogMessage debug(Logger aLogger, String aCode, String aText, String aDetails, List<String> aArguments) {

		if (aCode == null) {
			throw new NullPointerException("Message must not be null.");
		}

		return doLogMessage(aLogger, LogMessage.Type.DEBUG, aCode, aText, aDetails, aArguments);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public LogMessage info(Logger aLogger, String aCode, String aText) {
		return info(aLogger, aCode, aText, (String)null);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public LogMessage info(Logger aLogger, String aCode, String aText, List<String> aArguments) {
		return info(aLogger, aCode, aText, (String)null, aArguments);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public LogMessage info(Logger aLogger, String aCode, String aText, Throwable aThrowable) {
		return info(aLogger, aCode, aText, aThrowable, null);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public LogMessage info(Logger aLogger, String aCode, String aText, Throwable aThrowable, List<String> aArguments) {

		if (aThrowable == null) {
			throw new NullPointerException("Exception must not be null.");
		}

		return info(aLogger, aCode, aText, ExceptionUtils.getStackTrace(aThrowable).trim(), aArguments);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public LogMessage info(Logger aLogger, String aCode, String aText, String aDetails) {
		return info(aLogger, aCode, aText, aDetails, null);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public LogMessage info(Logger aLogger, String aCode, String aText, String aDetails, List<String> aArguments) {

		if (aCode == null) {
			throw new NullPointerException("Message must not be null.");
		}

		return doLogMessage(aLogger, LogMessage.Type.INFO, aCode, aText, aDetails, aArguments);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public LogMessage warn(Logger aLogger, String aCode, String aText) {
		return warn(aLogger, aCode, aText, (String)null);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public LogMessage warn(Logger aLogger, String aCode, String aText, List<String> aArguments) {
		return warn(aLogger, aCode, aText, (String)null, aArguments);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public LogMessage warn(Logger aLogger, String aCode, String aText, Exception aException) {
		return warn(aLogger, aCode, aText, aException, null);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public LogMessage warn(Logger aLogger, String aCode, String aText, Throwable aThrowable, List<String> aArguments) {

		if (aThrowable == null) {
			throw new NullPointerException("Exception must not be null.");
		}

		return warn(aLogger, aCode, aText, ExceptionUtils.getStackTrace(aThrowable).trim(), aArguments);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public LogMessage warn(Logger aLogger, String aCode, String aText, String aDetails) {
		return warn(aLogger, aCode, aText, aDetails, null);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public LogMessage warn(Logger aLogger, String aCode, String aText, String aDetails, List<String> aArguments) {

		if (aCode == null) {
			throw new NullPointerException("Message must not be null.");
		}

		return doLogMessage(aLogger, LogMessage.Type.WARN, aCode, aText, aDetails, aArguments);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public LogMessage error(Logger aLogger, String aCode, String aText) {
		return error(aLogger, aCode, aText, (String)null);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public LogMessage error(Logger aLogger, String aCode, String aText, List<String> aArguments) {
		return error(aLogger, aCode, aText, (String)null, aArguments);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public LogMessage error(Logger aLogger, String aCode, String aText, Throwable aThrowable) {
		return error(aLogger, aCode, aText, aThrowable, null);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public LogMessage error(Logger aLogger, String aCode, String aText, Throwable aThrowable, List<String> aArguments) {

		if (aThrowable == null) {
			throw new NullPointerException("Exception must not be null.");
		}

		return error(aLogger, aCode, aText, ExceptionUtils.getStackTrace(aThrowable).trim(), aArguments);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public LogMessage error(Logger aLogger, String aCode, String aText, String aDetails) {
		return error(aLogger, aCode, aText, aDetails, null);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public LogMessage error(Logger aLogger, String aCode, String aText, String aDetails, List<String> aArguments) {

		if (aCode == null) {
			throw new NullPointerException("Message must not be null.");
		}

		return doLogMessage(aLogger, LogMessage.Type.ERROR, aCode, aText, aDetails, aArguments);
	}

	@Override
	@Transactional
	public void deleteAll() {
		logMessageDao.deleteAll();
	}

	private LogMessage doLogMessage(Logger aLogger, LogMessage.Type aType, String aCode, String aText, String aDetails, List<String> aArguments) {

		if (aLogger != null) {

			String message = aText;
			if (aDetails != null) {
				message += "\n" + aDetails;
			}

			switch (aType) {
				case DEBUG:
					aLogger.debug(message);
					break;
				case INFO:
					aLogger.info(message);
					break;
				case WARN:
					aLogger.warn(message);
					break;
				case ERROR:
					aLogger.error(message);
					break;
			}
		}

		LogMessage message = new LogMessage();

		message.setType(aType);
		message.setCode(aCode);
		message.setText(aText);
		message.setArguments(aArguments);
		message.setDetails(aDetails);

		return logMessageDao.save(message);
	}
}
