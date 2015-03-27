package net.dorokhov.pony.core.dao;

import net.dorokhov.pony.core.domain.RefreshToken;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Date;

public interface RefreshTokenDao extends PagingAndSortingRepository<RefreshToken, String> {

	public Long deleteByUserId(Long aId);
	public Long deleteByCreationDateLessThan(Date aMaxCreationDate);

}
