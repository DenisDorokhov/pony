package net.dorokhov.pony.core.dao;

import net.dorokhov.pony.core.domain.AccessToken;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Date;

public interface AccessTokenDao extends PagingAndSortingRepository<AccessToken, String> {

	public void deleteByUserId(Long aId);
	public void deleteByCreationDateLessThan(Date aMaxCreationDate);

}
