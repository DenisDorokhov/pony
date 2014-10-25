package net.dorokhov.pony.core.dao;

import net.dorokhov.pony.core.entity.Album;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface AlbumDao extends PagingAndSortingRepository<Album, Long> {

	public long countByArtworkId(Long aStoredFileId);

	@Query("SELECT al FROM Album al " +
			"INNER JOIN FETCH al.artist ar " +
			"LEFT JOIN FETCH al.artwork " +
			"LEFT JOIN FETCH ar.artwork " +
			"WHERE ar.id = ?1 AND al.name = ?2")
	public Album findByArtistIdAndName(Long aArtistId, String aName);

	@Query(value = "SELECT DISTINCT s.album FROM Song s " +
			"INNER JOIN FETCH s.album.artist ar " +
			"LEFT JOIN FETCH s.album.artwork " +
			"LEFT JOIN FETCH ar.artwork " +
			"WHERE s.genre.id = ?1",
			countQuery = "SELECT COUNT (DISTINCT s.album) FROM Song s WHERE s.genre.id = ?1")
	public Page<Album> findByGenreId(Long aGenreId, Pageable aPageable);

	public Page<Album> findByArtworkId(Long aStoredFileId, Pageable aPageable);
}
