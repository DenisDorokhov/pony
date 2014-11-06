package net.dorokhov.pony.core.dao;

import net.dorokhov.pony.core.entity.Album;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Date;

public interface AlbumDao extends PagingAndSortingRepository<Album, Long> {

	public long countByCreationDateGreaterThan(Date aDate);
	public long countByCreationDateLessThanAndUpdateDateGreaterThan(Date aCreationDate, Date aUpdateDate);
	public long countByArtistIdAndArtworkNotNull(Long aArtistId);

	public Album findByArtistIdAndName(Long aArtistId, String aName);

	public Page<Album> findByArtistIdAndArtworkNotNull(Long aArtistId, Pageable aPageable);

	public Page<Album> findByArtworkId(Long aStoredFileId, Pageable aPageable);
}
