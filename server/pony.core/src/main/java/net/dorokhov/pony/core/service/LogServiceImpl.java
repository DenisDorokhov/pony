package net.dorokhov.pony.core.service;

import net.dorokhov.pony.core.dao.LogMessageDao;
import net.dorokhov.pony.core.domain.LogMessage;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class LogServiceImpl implements LogService {

	private final Logger log = LoggerFactory.getLogger(getClass());

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
	@Transactional
	public LogMessage debug(String aCode, String aText) {
		return debug(aCode, aText, (String)null);
	}

	@Override
	@Transactional
	public LogMessage debug(String aCode, String aText, List<String> aArguments) {
		return debug(aCode, aText, (String)null, aArguments);
	}

	@Override
	@Transactional
	public LogMessage debug(String aCode, String aText, Throwable aThrowable) {
		return debug(aCode, aText, aThrowable, null);
	}

	@Override
	@Transactional
	public LogMessage debug(String aCode, String aText, Throwable aThrowable, List<String> aArguments) {

		if (aThrowable == null) {
			throw new NullPointerException("Exception must not be null.");
		}

		return debug(aCode, aText, ExceptionUtils.getStackTrace(aThrowable).trim(), aArguments);
	}

	@Override
	@Transactional
	public LogMessage debug(String aCode, String aText, String aDetails) {
		return debug(aCode, aText, aDetails, null);
	}

	@Override
	@Transactional
	public LogMessage debug(String aCode, String aText, String aDetails, List<String> aArguments) {

		if (aCode == null) {
			throw new NullPointerException("Message must not be null.");
		}

		return doLogMessage(LogMessage.Type.DEBUG, aCode, aText, aArguments, aDetails);
	}

	@Override
	@Transactional
	public LogMessage info(String aCode, String aText) {
		return info(aCode, aText, (String)null);
	}

	@Override
	@Transactional
	public LogMessage info(String aCode, String aText, List<String> aArguments) {
		return info(aCode, aText, (String)null, aArguments);
	}

	@Override
	@Transactional
	public LogMessage info(String aCode, String aText, Throwable aThrowable) {
		return info(aCode, aText, aThrowable, null);
	}

	@Override
	@Transactional
	public LogMessage info(String aCode, String aText, Throwable aThrowable, List<String> aArguments) {

		if (aThrowable == null) {
			throw new NullPointerException("Exception must not be null.");
		}

		return info(aCode, aText, ExceptionUtils.getStackTrace(aThrowable).trim(), aArguments);
	}

	@Override
	@Transactional
	public LogMessage info(String aCode, String aText, String aDetails) {
		return info(aCode, aText, aDetails, null);
	}

	@Override
	@Transactional
	public LogMessage info(String aCode, String aText, String aDetails, List<String> aArguments) {

		if (aCode == null) {
			throw new NullPointerException("Message must not be null.");
		}

		return doLogMessage(LogMessage.Type.INFO, aCode, aText, aArguments, aDetails);
	}

	@Override
	@Transactional
	public LogMessage warn(String aCode, String aText) {
		return warn(aCode, aText, (String)null);
	}

	@Override
	@Transactional
	public LogMessage warn(String aCode, String aText, List<String> aArguments) {
		return warn(aCode, aText, (String)null, aArguments);
	}

	@Override
	@Transactional
	public LogMessage warn(String aCode, String aText, Exception aException) {
		return warn(aCode, aText, aException, null);
	}

	@Override
	@Transactional
	public LogMessage warn(String aCode, String aText, Throwable aThrowable, List<String> aArguments) {

		if (aThrowable == null) {
			throw new NullPointerException("Exception must not be null.");
		}

		return warn(aCode, aText, ExceptionUtils.getStackTrace(aThrowable).trim(), aArguments);
	}

	@Override
	@Transactional
	public LogMessage warn(String aCode, String aText, String aDetails) {
		return warn(aCode, aText, aDetails, null);
	}

	@Override
	@Transactional
	public LogMessage warn(String aCode, String aText, String aDetails, List<String> aArguments) {

		if (aCode == null) {
			throw new NullPointerException("Message must not be null.");
		}

		return doLogMessage(LogMessage.Type.WARN, aCode, aText, aArguments, aDetails);
	}

	@Override
	@Transactional
	public LogMessage error(String aCode, String aText) {
		return error(aCode, aText, (String)null);
	}

	@Override
	@Transactional
	public LogMessage error(String aCode, String aText, List<String> aArguments) {
		return error(aCode, aText, (String)null, aArguments);
	}

	@Override
	@Transactional
	public LogMessage error(String aCode, String aText, Throwable aThrowable) {
		return error(aCode, aText, aThrowable, null);
	}

	@Override
	@Transactional
	public LogMessage error(String aCode, String aText, Throwable aThrowable, List<String> aArguments) {

		if (aThrowable == null) {
			throw new NullPointerException("Exception must not be null.");
		}

		return error(aCode, aText, ExceptionUtils.getStackTrace(aThrowable).trim(), aArguments);
	}

	@Override
	@Transactional
	public LogMessage error(String aCode, String aText, String aDetails) {
		return error(aCode, aText, aDetails, null);
	}

	@Override
	@Transactional
	public LogMessage error(String aCode, String aText, String aDetails, List<String> aArguments) {

		if (aCode == null) {
			throw new NullPointerException("Message must not be null.");
		}

		return doLogMessage(LogMessage.Type.ERROR, aCode, aText, aArguments, aDetails);
	}

	@Override
	@Transactional
	public void deleteAll() {
		logMessageDao.deleteAll();
	}

	private LogMessage doLogMessage(LogMessage.Type aType, String aCode, String aText, List<String> aArguments, String aDetails) {

		String logEntry = (aText != null ? aText : aCode).trim();
		if (aDetails != null) {
			logEntry += "\n" + aDetails.trim();
		}

		switch (aType) {

			case DEBUG:
				log.debug(logEntry);
				break;

			case INFO:
				log.info(logEntry);
				break;

			case WARN:
				log.warn(logEntry);
				break;

			case ERROR:
				log.error(logEntry);
				break;
		}

		LogMessage message = new LogMessage();

		message.setDate(new Date());
		message.setType(aType);
		message.setCode(aCode);
		message.setText(aText);
		message.setArguments(aArguments);
		message.setDetails(aDetails);

		return logMessageDao.save(message);
	}
}
