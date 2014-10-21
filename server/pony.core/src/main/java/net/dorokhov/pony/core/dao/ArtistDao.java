package net.dorokhov.pony.core.dao;

import net.dorokhov.pony.core.domain.Artist;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface ArtistDao extends PagingAndSortingRepository<Artist, Long> {

	public long countByArtworkId(Long aStoredFileId);

	public Page<Artist> findByArtworkId(Long aStoredFileId, Pageable aPageable);

	public Artist findByName(String aName);
}
