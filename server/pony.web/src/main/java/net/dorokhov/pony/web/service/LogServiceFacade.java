package net.dorokhov.pony.web.service;

import net.dorokhov.pony.web.domain.ListDto;
import net.dorokhov.pony.web.domain.LogMessageDto;
import net.dorokhov.pony.web.domain.LogQueryDto;
import net.dorokhov.pony.web.exception.InvalidArgumentException;

public interface LogServiceFacade {

	public ListDto<LogMessageDto> getByQuery(LogQueryDto aQuery, int aPageNumber, int aPageSize) throws InvalidArgumentException;

}
