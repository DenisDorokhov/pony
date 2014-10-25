package net.dorokhov.pony.core.dao;

import net.dorokhov.pony.core.entity.Genre;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface GenreDao extends PagingAndSortingRepository<Genre, Long> {

	@Query("SELECT g FROM Genre g " +
			"LEFT JOIN FETCH g.artwork " +
			"WHERE g.name = ?1")
	public Genre findByName(String aName);

	public Page<Genre> findByArtworkId(Long aStoredFileId, Pageable aPageable);
}
