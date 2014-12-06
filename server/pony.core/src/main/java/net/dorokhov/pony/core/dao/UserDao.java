package net.dorokhov.pony.core.dao;

import net.dorokhov.pony.core.domain.User;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface UserDao extends PagingAndSortingRepository<User, Long> {

	public User findByEmail(String aEmail);
	public User findByEmailAndPassword(String aEmail, String aPassword);

}
