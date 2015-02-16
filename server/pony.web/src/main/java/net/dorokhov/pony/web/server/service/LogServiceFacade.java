package net.dorokhov.pony.web.server.service;

import net.dorokhov.pony.web.server.exception.InvalidArgumentException;
import net.dorokhov.pony.web.shared.LogQueryDto;
import net.dorokhov.pony.web.shared.list.LogMessageListDto;

public interface LogServiceFacade {

	public LogMessageListDto getByQuery(LogQueryDto aQuery, int aPageNumber, int aPageSize) throws InvalidArgumentException;

}
