package net.dorokhov.pony.web.server.service;

import net.dorokhov.pony.core.domain.LogMessage;
import net.dorokhov.pony.core.logging.LogService;
import net.dorokhov.pony.web.server.exception.InvalidArgumentException;
import net.dorokhov.pony.web.shared.ErrorCode;
import net.dorokhov.pony.web.shared.LogMessageDto;
import net.dorokhov.pony.web.shared.LogQueryDto;
import net.dorokhov.pony.web.shared.list.LogMessageListDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class LogServiceFacadeImpl implements LogServiceFacade {

	private static final int MAX_PAGE_SIZE = 100;

	private LogService logService;

	private DtoConverter dtoConverter;

	@Autowired
	public void setLogService(LogService aLogService) {
		logService = aLogService;
	}

	@Autowired
	public void setDtoConverter(DtoConverter aDtoConverter) {
		dtoConverter = aDtoConverter;
	}

	@Override
	public LogMessageListDto getByQuery(LogQueryDto aQuery, int aPageNumber, int aPageSize) throws InvalidArgumentException {

		if (aPageNumber < 0) {
			throw new InvalidArgumentException(ErrorCode.PAGE_NUMBER_INVALID, "Page number [" + aPageNumber + "] is invalid", String.valueOf(aPageNumber));
		}
		if (aPageSize > MAX_PAGE_SIZE) {
			throw new InvalidArgumentException(ErrorCode.PAGE_SIZE_INVALID, "Page size [" + aPageNumber + "] must be less than or equal to [" + MAX_PAGE_SIZE + "]",
					String.valueOf(aPageSize), String.valueOf(MAX_PAGE_SIZE));
		}

		LogMessageDto.Type typeDto = aQuery.getType();
		if (typeDto == null) {
			typeDto = LogMessageDto.Type.DEBUG;
		}

		Date minDate = aQuery.getMinDate();
		if (minDate == null) {
			minDate = new Date(0);
		}

		Date maxDate = aQuery.getMaxDate();
		if (maxDate == null) {
			maxDate = new Date();
		}

		LogMessage.Type type = null;

		switch (typeDto) {

			case DEBUG:
				type = LogMessage.Type.DEBUG;
				break;
			case INFO:
				type = LogMessage.Type.INFO;
				break;
			case WARN:
				type = LogMessage.Type.WARN;
				break;
			case ERROR:
				type = LogMessage.Type.ERROR;
				break;
		}

		Page<LogMessage> page = logService.getByTypeAndDate(type, minDate, maxDate, new PageRequest(aPageNumber, aPageSize, Sort.Direction.DESC, "date"));

		return dtoConverter.pagedListToDto(LogMessageListDto.class, page, new DtoConverter.ListConverter<LogMessage, LogMessageDto>() {
			@Override
			public LogMessageDto convert(LogMessage aItem) {
				return dtoConverter.logMessageToDto(aItem);
			}
		});
	}

}
