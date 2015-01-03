package net.dorokhov.pony.core.security;

import net.dorokhov.pony.core.dao.UserDao;
import net.dorokhov.pony.core.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

	private UserDao userDao;

	@Autowired
	public void setUserDao(UserDao aUserDao) {
		userDao = aUserDao;
	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.REQUIRES_NEW)
	public UserDetails loadUserByUsername(String aUsername) throws UsernameNotFoundException {

		User user = userDao.findByEmail(aUsername);

		if (user == null) {
			throw new UsernameNotFoundException("User [" + aUsername + "] not found.");
		}

		return new UserDetailsImpl(user);
	}

}
