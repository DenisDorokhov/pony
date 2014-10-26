package net.dorokhov.pony.core.dao;

import net.dorokhov.pony.core.entity.Artist;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface ArtistDao extends PagingAndSortingRepository<Artist, Long> {

	public long countByArtworkId(Long aStoredFileId);

	public Artist findByName(String aName);

	@Query("SELECT ar FROM Artist ar " +
			"LEFT JOIN FETCH ar.artwork ")
	List<Artist> findAll(Sort sort);

	public Page<Artist> findByArtworkId(Long aStoredFileId, Pageable aPageable);
}
