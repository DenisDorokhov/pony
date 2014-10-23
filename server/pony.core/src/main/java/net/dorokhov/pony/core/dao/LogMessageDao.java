package net.dorokhov.pony.core.dao;

import net.dorokhov.pony.core.entity.LogMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Date;

public interface LogMessageDao extends PagingAndSortingRepository<LogMessage, Long> {

	public Page<LogMessage> findByTypeGreaterThanEqual(LogMessage.Type aType, Pageable aPageable);

	public Page<LogMessage> findByTypeGreaterThanEqualAndDateBetween(LogMessage.Type aType, Date aMinDate, Date aMaxDate, Pageable aPageable);
}
