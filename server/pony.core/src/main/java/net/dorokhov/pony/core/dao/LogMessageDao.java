package net.dorokhov.pony.core.dao;

import net.dorokhov.pony.core.domain.LogMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Date;

public interface LogMessageDao extends PagingAndSortingRepository<LogMessage, Long> {

	@Query(value = "SELECT m FROM LogMessage m " +
			"LEFT JOIN FETCH m.messageArguments " +
			"WHERE m.type > ?1",
			countQuery = "SELECT count(m) FROM LogMessage m WHERE m.type > ?1")
	public Page<LogMessage> findByTypeGreaterThan(LogMessage.Type aType, Pageable aPageable);

	@Query(value = "SELECT m FROM LogMessage m " +
			"LEFT JOIN FETCH m.messageArguments " +
			"WHERE m.type > :aType AND m.date <= :aMinDate AND m.date >= :aMaxDate",
			countQuery = "SELECT count(m) FROM LogMessage m WHERE m.type > :aType AND m.date <= :aMinDate AND m.date >= :aMaxDate")
	public Page<LogMessage> findByTypeGreaterThanAndDateBetween(LogMessage.Type aType, Date aMinDate, Date aMaxDate, Pageable aPageable);
}
