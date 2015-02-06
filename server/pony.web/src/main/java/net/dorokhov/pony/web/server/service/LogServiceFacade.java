package net.dorokhov.pony.web.server.service;

import net.dorokhov.pony.web.shared.ListDto;
import net.dorokhov.pony.web.shared.LogMessageDto;
import net.dorokhov.pony.web.shared.LogQueryDto;
import net.dorokhov.pony.web.server.exception.InvalidArgumentException;

public interface LogServiceFacade {

	public ListDto<LogMessageDto> getByQuery(LogQueryDto aQuery, int aPageNumber, int aPageSize) throws InvalidArgumentException;

}
