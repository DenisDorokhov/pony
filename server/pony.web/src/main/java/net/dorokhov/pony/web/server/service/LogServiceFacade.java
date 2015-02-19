package net.dorokhov.pony.web.server.service;

import net.dorokhov.pony.web.server.exception.InvalidArgumentException;
import net.dorokhov.pony.web.shared.LogMessageDto;
import net.dorokhov.pony.web.shared.LogQueryDto;
import net.dorokhov.pony.web.shared.PagedListDto;

public interface LogServiceFacade {

	public PagedListDto<LogMessageDto> getByQuery(LogQueryDto aQuery, int aPageNumber, int aPageSize) throws InvalidArgumentException;

}
