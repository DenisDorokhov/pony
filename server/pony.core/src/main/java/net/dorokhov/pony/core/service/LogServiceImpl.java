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
	public Page<LogMessage> getByType(LogMessage.Type aType, Pageable aPageable) {
		return logMessageDao.findByTypeGreaterThan(aType, aPageable);
	}

	@Override
	@Transactional(readOnly = true)
	public Page<LogMessage> getByTypeAndDate(LogMessage.Type aType, Date aMinDate, Date aMaxDate, Pageable aPageable) {
		return logMessageDao.findByTypeGreaterThanAndDateBetween(aType, aMinDate, aMaxDate, aPageable);
	}

	@Override
	@Transactional
	public LogMessage debug(String aMessageCode) {
		return debug(aMessageCode, (String)null);
	}

	@Override
	@Transactional
	public LogMessage debug(String aMessageCode, List<String> aMessageArguments) {
		return debug(aMessageCode, aMessageArguments, (String)null);
	}

	@Override
	@Transactional
	public LogMessage debug(String aMessageCode, Throwable aThrowable) {
		return debug(aMessageCode, null, aThrowable);
	}

	@Override
	@Transactional
	public LogMessage debug(String aMessageCode, List<String> aMessageArguments, Throwable aThrowable) {

		if (aThrowable == null) {
			throw new NullPointerException("Exception must not be null.");
		}

		return debug(aMessageCode, aMessageArguments, ExceptionUtils.getStackTrace(aThrowable));
	}

	@Override
	@Transactional
	public LogMessage debug(String aMessageCode, String aMessageDetails) {
		return debug(aMessageCode, null, aMessageDetails);
	}

	@Override
	@Transactional
	public LogMessage debug(String aMessageCode, List<String> aMessageArguments, String aMessageDetails) {

		if (aMessageCode == null) {
			throw new NullPointerException("Message must not be null.");
		}

		log.debug(aMessageCode + "\n" + aMessageDetails);

		return saveLogMessage(LogMessage.Type.DEBUG, aMessageCode, aMessageArguments, aMessageDetails);
	}

	@Override
	@Transactional
	public LogMessage info(String aMessageCode) {
		return info(aMessageCode, (String)null);
	}

	@Override
	@Transactional
	public LogMessage info(String aMessageCode, List<String> aMessageArguments) {
		return info(aMessageCode, aMessageArguments, (String)null);
	}

	@Override
	@Transactional
	public LogMessage info(String aMessageCode, Throwable aThrowable) {
		return info(aMessageCode, null, aThrowable);
	}

	@Override
	@Transactional
	public LogMessage info(String aMessageCode, List<String> aMessageArguments, Throwable aThrowable) {

		if (aThrowable == null) {
			throw new NullPointerException("Exception must not be null.");
		}

		return info(aMessageCode, aMessageArguments, ExceptionUtils.getStackTrace(aThrowable));
	}

	@Override
	@Transactional
	public LogMessage info(String aMessageCode, String aMessageDetails) {
		return info(aMessageCode, null, aMessageDetails);
	}

	@Override
	@Transactional
	public LogMessage info(String aMessageCode, List<String> aMessageArguments, String aMessageDetails) {

		if (aMessageCode == null) {
			throw new NullPointerException("Message must not be null.");
		}

		log.info(aMessageCode + "\n" + aMessageDetails);

		return saveLogMessage(LogMessage.Type.INFO, aMessageCode, aMessageArguments, aMessageDetails);
	}

	@Override
	@Transactional
	public LogMessage warn(String aMessageCode) {
		return warn(aMessageCode, (String)null);
	}

	@Override
	@Transactional
	public LogMessage warn(String aMessageCode, List<String> aMessageArguments) {
		return warn(aMessageCode, aMessageArguments, (String)null);
	}

	@Override
	@Transactional
	public LogMessage warn(String aMessageCode, Exception aException) {
		return warn(aMessageCode, null, aException);
	}

	@Override
	@Transactional
	public LogMessage warn(String aMessageCode, List<String> aMessageArguments, Throwable aThrowable) {

		if (aThrowable == null) {
			throw new NullPointerException("Exception must not be null.");
		}

		return warn(aMessageCode, aMessageArguments, ExceptionUtils.getStackTrace(aThrowable));
	}

	@Override
	@Transactional
	public LogMessage warn(String aMessageCode, String aMessageDetails) {
		return warn(aMessageCode, null, aMessageDetails);
	}

	@Override
	@Transactional
	public LogMessage warn(String aMessageCode, List<String> aMessageArguments, String aMessageDetails) {

		if (aMessageCode == null) {
			throw new NullPointerException("Message must not be null.");
		}

		log.warn(aMessageCode + "\n" + aMessageDetails);

		return saveLogMessage(LogMessage.Type.WARN, aMessageCode, aMessageArguments, aMessageDetails);
	}

	@Override
	@Transactional
	public LogMessage error(String aMessageCode) {
		return error(aMessageCode, (String)null);
	}

	@Override
	@Transactional
	public LogMessage error(String aMessageCode, List<String> aArguments) {
		return error(aMessageCode, aArguments, (String)null);
	}

	@Override
	@Transactional
	public LogMessage error(String aMessageCode, Throwable aThrowable) {
		return error(aMessageCode, null, aThrowable);
	}

	@Override
	@Transactional
	public LogMessage error(String aMessageCode, List<String> aMessageArguments, Throwable aThrowable) {

		if (aThrowable == null) {
			throw new NullPointerException("Exception must not be null.");
		}

		return error(aMessageCode, aMessageArguments, ExceptionUtils.getStackTrace(aThrowable));
	}

	@Override
	@Transactional
	public LogMessage error(String aMessageCode, String aMessageDetails) {
		return error(aMessageCode, null, aMessageDetails);
	}

	@Override
	@Transactional
	public LogMessage error(String aMessageCode, List<String> aMessageArguments, String aMessageDetails) {

		if (aMessageCode == null) {
			throw new NullPointerException("Message must not be null.");
		}

		log.error(aMessageCode + "\n" + aMessageDetails);

		return saveLogMessage(LogMessage.Type.ERROR, aMessageCode, aMessageArguments, aMessageDetails);
	}

	private LogMessage saveLogMessage(LogMessage.Type aType, String aMessageCode, List<String> aMessageArguments, String aMessageDetails) {

		LogMessage message = new LogMessage();

		message.setDate(new Date());
		message.setType(aType);
		message.setMessageCode(aMessageCode);
		message.setMessageArguments(aMessageArguments);
		message.setMessageDetails(aMessageDetails);

		return logMessageDao.save(message);
	}
}
