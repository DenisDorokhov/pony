package net.dorokhov.pony.core.dao;

import net.dorokhov.pony.core.domain.Album;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface AlbumDao extends PagingAndSortingRepository<Album, Long> {

	public long countByArtistId(Long aArtistId);
	public long countByArtworkId(Long aStoredFileId);

	@Query("SELECT a FROM Album a " +
			"INNER JOIN FETCH a.artist " +
			"WHERE a.id = ?1")
	public Album findById(Long aId);

	public Album findByArtistIdAndName(Long aArtistId, String aName);

	public List<Album> findByArtistId(Long aArtistId, Sort aSort);

	public Page<Album> findByArtworkId(Long aStoredFileId, Pageable aPageable);

	@Query(value = "SELECT DISTINCT s.album FROM Song s " +
			"INNER JOIN FETCH s.album.artist " +
			"WHERE s.genre.id = ?1",
			countQuery = "SELECT COUNT (DISTINCT s.album) FROM Song s WHERE s.genre.id = ?1")
	public Page<Album> findByGenreId(Long aGenreId, Pageable aPageable);
}
