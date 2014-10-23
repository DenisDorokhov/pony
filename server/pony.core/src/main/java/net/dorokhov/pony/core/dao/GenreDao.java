package net.dorokhov.pony.core.dao;

import net.dorokhov.pony.core.entity.Genre;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface GenreDao extends PagingAndSortingRepository<Genre, Long> {

	public long countByArtworkId(Long aStoredFileId);

	public Genre findByName(String aName);

	public Page<Genre> findByArtworkId(Long aStoredFileId, Pageable aPageable);
}
