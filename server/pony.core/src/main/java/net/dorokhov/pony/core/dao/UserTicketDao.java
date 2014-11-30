package net.dorokhov.pony.core.dao;

import net.dorokhov.pony.core.domain.UserTicket;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Date;

public interface UserTicketDao extends PagingAndSortingRepository<UserTicket, String> {

	public void deleteByUpdateDateNullAndCreationDateLessThan(Date aMaxCreationDate);
	public void deleteByUpdateDateNotNullAndUpdateDateLessThan(Date aMaxUpdateDate);

}
