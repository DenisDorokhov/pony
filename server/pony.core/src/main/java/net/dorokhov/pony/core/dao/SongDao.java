package net.dorokhov.pony.core.dao;

import net.dorokhov.pony.core.domain.Song;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Date;
import java.util.List;

public interface SongDao extends PagingAndSortingRepository<Song, Long> {

	public long countByGenreId(Long aGenreId);
	public long countByAlbumId(Long aAlbumId);
	public long countByAlbumArtistId(Long aArtistId);
	public long countByArtworkId(Long aStoredFileId);
	public long countByCreationDateGreaterThan(Date aDate);
	public long countByCreationDateLessThanAndUpdateDateGreaterThan(Date aCreationDate, Date aUpdateDate);
	public long countByGenreIdAndArtworkNotNull(Long aGenreId);
	public long countByAlbumIdAndArtworkNotNull(Long aGenreId);

	@Query("SELECT SUM(s.size) FROM Song s")
	public Long sumSize();

	public Song findByPath(String aPath);

	@Query("SELECT s FROM Song s " +
			"INNER JOIN FETCH s.genre g " +
			"INNER JOIN FETCH s.album al " +
			"INNER JOIN FETCH al.artist ar " +
			"LEFT JOIN FETCH s.artwork " +
			"LEFT JOIN FETCH al.artwork " +
			"LEFT JOIN FETCH ar.artwork " +
			"WHERE al.id = ?1")
	public List<Song> findByAlbumId(Long aAlbumId, Sort aSort);

	@Query("SELECT s FROM Song s " +
			"INNER JOIN FETCH s.genre g " +
			"INNER JOIN FETCH s.album al " +
			"INNER JOIN FETCH al.artist ar " +
			"LEFT JOIN FETCH s.artwork " +
			"LEFT JOIN FETCH al.artwork " +
			"LEFT JOIN FETCH ar.artwork " +
			"WHERE ar.id = ?1")
	public List<Song> findByAlbumArtistId(Long aArtistId, Sort aSort);

	@Query(value = "SELECT s FROM Song s " +
			"INNER JOIN FETCH s.genre g " +
			"INNER JOIN FETCH s.album al " +
			"INNER JOIN FETCH al.artist ar " +
			"LEFT JOIN FETCH s.artwork " +
			"LEFT JOIN FETCH al.artwork " +
			"LEFT JOIN FETCH ar.artwork " +
			"WHERE ar.id = ?1",
			countQuery = "SELECT COUNT(s) FROM Song s WHERE s.album.artist.id = ?1")
	public Page<Song> findByAlbumArtistId(Long aArtistId, Pageable aPageable);

	public Page<Song> findByGenreIdAndArtworkNotNull(Long aGenreId, Pageable aPageable);
	public Page<Song> findByAlbumIdAndArtworkNotNull(Long aGenreId, Pageable aPageable);

	@Modifying(clearAutomatically = true)
	@Query("UPDATE Song s SET s.artwork = NULL WHERE s.artwork.id = ?1")
	public void clearArtworkByArtworkId(Long aStoredFileId);
}
