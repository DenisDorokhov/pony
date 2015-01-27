package net.dorokhov.pony.web.service;

import net.dorokhov.pony.core.domain.LogMessage;
import net.dorokhov.pony.core.logging.LogService;
import net.dorokhov.pony.web.domain.ListDto;
import net.dorokhov.pony.web.domain.LogMessageDto;
import net.dorokhov.pony.web.domain.LogQueryDto;
import net.dorokhov.pony.web.exception.InvalidRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class LogServiceFacadeImpl implements LogServiceFacade {

	private static final int MAX_PAGE_SIZE = 100;

	private LogService logService;

	@Autowired
	public void setLogService(LogService aLogService) {
		logService = aLogService;
	}

	@Override
	public ListDto<LogMessageDto> getByQuery(LogQueryDto aQuery, int aPageNumber, int aPageSize) throws InvalidRequestException {

		if (aPageNumber < 0) {
			throw new InvalidRequestException("errorPageNumberInvalid", "Page number [" + aPageNumber + "] is invalid", String.valueOf(aPageNumber));
		}
		if (aPageSize > MAX_PAGE_SIZE) {
			throw new InvalidRequestException("errorPageSizeInvalid", "Page size [" + aPageNumber + "] must be less than or equal to [" + MAX_PAGE_SIZE + "]",
					String.valueOf(aPageSize), String.valueOf(MAX_PAGE_SIZE));
		}

		LogMessage.Type type = aQuery.getType();
		if (type == null) {
			type = LogMessage.Type.DEBUG;
		}

		Date minDate = aQuery.getMinDate();
		if (minDate == null) {
			minDate = new Date(0);
		}

		Date maxDate = aQuery.getMaxDate();
		if (maxDate == null) {
			maxDate = new Date();
		}

		return ListDto.valueOf(logService.getByTypeAndDate(type, minDate, maxDate, new PageRequest(aPageNumber, aPageSize, Sort.Direction.DESC, "date")),
				new ListDto.ContentConverter<LogMessage, LogMessageDto>() {
					@Override
					public LogMessageDto convert(LogMessage aItem) {
						return LogMessageDto.valueOf(aItem);
					}
				});
	}

}
