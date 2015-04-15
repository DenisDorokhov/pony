package net.dorokhov.pony.core.dao;

import net.dorokhov.pony.core.domain.Genre;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Date;

public interface GenreDao extends PagingAndSortingRepository<Genre, Long> {

	public long countByArtworkId(Long aStoredFileId);
	public long countByCreationDateGreaterThan(Date aDate);
	public long countByCreationDateLessThanAndUpdateDateGreaterThan(Date aCreationDate, Date aUpdateDate);

	public Genre findByName(String aName);

	public Page<Genre> findByArtworkId(Long aStoredFileId, Pageable aPageable);

	@Modifying(clearAutomatically = true)
	@Query("UPDATE Genre g SET g.artwork = NULL WHERE g.artwork.id = ?1")
	public void clearArtworkByArtworkId(Long aStoredFileId);

}
