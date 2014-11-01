package net.dorokhov.pony.core.dao;

import net.dorokhov.pony.core.entity.Album;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Date;

public interface AlbumDao extends PagingAndSortingRepository<Album, Long> {

	public long countByCreationDateGreaterThan(Date aDate);
	public long countByUpdateDateGreaterThan(Date aDate);

	public long countByArtistId(Long aArtistId);
	public long countByArtistIdAndArtworkNotNull(Long aArtistId);

	@Query("SELECT COUNT (DISTINCT s.album) FROM Song s WHERE s.genre.id = ?1 AND s.album.artwork IS NOT NULL")
	public long countByGenreIdAndArtworkNotNull(Long aGenreId);

	public Album findByArtistIdAndName(Long aArtistId, String aName);

	public Page<Album> findByArtistIdAndArtworkNotNull(Long aArtistId, Pageable aPageable);

	@Query(value = "SELECT DISTINCT s.album FROM Song s " +
			"INNER JOIN FETCH s.album.artist ar " +
			"LEFT JOIN FETCH s.album.artwork " +
			"LEFT JOIN FETCH ar.artwork " +
			"WHERE s.genre.id = ?1",
			countQuery = "SELECT COUNT (DISTINCT s.album) FROM Song s WHERE s.genre.id = ?1")
	public Page<Album> findByGenreId(Long aGenreId, Pageable aPageable);

	@Query(value = "SELECT DISTINCT s.album FROM Song s " +
			"WHERE s.genre.id = ?1 AND s.album.artwork IS NOT NULL",
			countQuery = "SELECT COUNT (DISTINCT s.album) FROM Song s WHERE s.genre.id = ?1 AND s.album.artwork IS NOT NULL")
	public Page<Album> findByGenreIdAndArtworkNotNull(Long aGenreId, Pageable aPageable);

	public Page<Album> findByArtworkId(Long aStoredFileId, Pageable aPageable);
}
