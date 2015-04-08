package net.dorokhov.pony.core.dao;

import net.dorokhov.pony.core.domain.AccessToken;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Date;

public interface AccessTokenDao extends PagingAndSortingRepository<AccessToken, String> {

	public Long deleteByUserId(Long aId);
	public Long deleteByDateLessThan(Date aMaxCreationDate);

}
