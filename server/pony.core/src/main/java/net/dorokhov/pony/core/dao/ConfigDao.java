package net.dorokhov.pony.core.dao;

import net.dorokhov.pony.core.entity.Config;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface ConfigDao extends PagingAndSortingRepository<Config, String> {

}
