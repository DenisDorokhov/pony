package net.dorokhov.pony.core.dao;

import net.dorokhov.pony.core.domain.Role;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface RoleDao extends PagingAndSortingRepository<Role, Long> {

	public Role findByName(String aName);

}
