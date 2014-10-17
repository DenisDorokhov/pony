package net.dorokhov.pony.core.dao;

import net.dorokhov.pony.core.domain.Genre;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface GenreDao extends PagingAndSortingRepository<Genre, Long> {
	public Genre findByName(String aName);
}
