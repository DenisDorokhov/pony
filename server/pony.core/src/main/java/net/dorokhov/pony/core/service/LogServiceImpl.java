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
	public LogMessage getById(Long aId) {
		return logMessageDao.findOne(aId);
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
	public LogMessage debug(String aMessage) {
		return debug(aMessage, (String)null);
	}

	@Override
	@Transactional
	public LogMessage debug(String aMessage, Exception aException) {

		if (aException == null) {
			throw new NullPointerException("Exception must not be null.");
		}

		return debug(aMessage, ExceptionUtils.getStackTrace(aException));
	}

	@Override
	@Transactional
	public LogMessage debug(String aMessage, String aDetails) {

		if (aMessage == null) {
			throw new NullPointerException("Message must not be null.");
		}

		log.debug(aMessage + "\n" + aDetails);

		return saveLogMessage(LogMessage.Type.DEBUG, aMessage, aDetails);
	}

	@Override
	@Transactional
	public LogMessage info(String aMessage) {
		return info(aMessage, (String)null);
	}

	@Override
	@Transactional
	public LogMessage info(String aMessage, Exception aException) {

		if (aException == null) {
			throw new NullPointerException("Exception must not be null.");
		}

		return info(aMessage, ExceptionUtils.getStackTrace(aException));
	}

	@Override
	@Transactional
	public LogMessage info(String aMessage, String aDetails) {

		if (aMessage == null) {
			throw new NullPointerException("Message must not be null.");
		}

		log.info(aMessage + "\n" + aDetails);

		return saveLogMessage(LogMessage.Type.INFO, aMessage, aDetails);
	}

	@Override
	@Transactional
	public LogMessage warn(String aMessage) {
		return warn(aMessage, (String)null);
	}

	@Override
	@Transactional
	public LogMessage warn(String aMessage, Exception aException) {

		if (aException == null) {
			throw new NullPointerException("Exception must not be null.");
		}

		return warn(aMessage, ExceptionUtils.getStackTrace(aException));
	}

	@Override
	@Transactional
	public LogMessage warn(String aMessage, String aDetails) {

		if (aMessage == null) {
			throw new NullPointerException("Message must not be null.");
		}

		log.warn(aMessage + "\n" + aDetails);

		return saveLogMessage(LogMessage.Type.WARN, aMessage, aDetails);
	}

	@Override
	@Transactional
	public LogMessage error(String aMessage) {
		return error(aMessage, (String)null);
	}

	@Override
	@Transactional
	public LogMessage error(String aMessage, Exception aException) {

		if (aException == null) {
			throw new NullPointerException("Exception must not be null.");
		}

		return error(aMessage, ExceptionUtils.getStackTrace(aException));
	}

	@Override
	@Transactional
	public LogMessage error(String aMessage, String aDetails) {

		if (aMessage == null) {
			throw new NullPointerException("Message must not be null.");
		}

		log.error(aMessage + "\n" + aDetails);

		return saveLogMessage(LogMessage.Type.ERROR, aMessage, aDetails);
	}

	private LogMessage saveLogMessage(LogMessage.Type aType, String aMessage, String aDetails) {

		LogMessage message = new LogMessage();

		message.setDate(new Date());
		message.setType(aType);
		message.setMessage(aMessage);
		message.setDetails(aDetails);

		return logMessageDao.save(message);
	}
}
