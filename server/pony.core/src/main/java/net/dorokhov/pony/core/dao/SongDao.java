package net.dorokhov.pony.core.dao;

import net.dorokhov.pony.core.entity.Song;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Date;
import java.util.List;

public interface SongDao extends PagingAndSortingRepository<Song, Long> {

	@Query("SELECT SUM(s.size) FROM Song s WHERE s.album.artist.id = ?1")
	public long sumSizeByArtistId(Long aGenreId);

	@Query("SELECT SUM(s.size) FROM Song s WHERE s.album.id = ?1")
	public long sumSizeByAlbumId(Long aGenreId);

	public long countByGenreId(Long aGenreId);
	public long countByAlbumArtistId(Long aArtistId);
	public long countByAlbumId(Long aAlbumId);
	public long countByArtworkId(Long aStoredFileId);
	public long countByCreationDateGreaterThan(Date aDate);
	public long countByUpdateDateGreaterThan(Date aDate);

	public Song findByPath(String aPath);

	@Query("SELECT s FROM Song s " +
			"INNER JOIN FETCH s.genre g " +
			"INNER JOIN FETCH s.album al " +
			"INNER JOIN FETCH al.artist ar " +
			"LEFT JOIN FETCH s.artwork " +
			"LEFT JOIN FETCH al.artwork " +
			"LEFT JOIN FETCH ar.artwork " +
			"WHERE ar.id = ?1")
	public List<Song> findByAlbumArtistId(Long aArtistId, Sort aSort);

	public Page<Song> findByArtworkId(Long aStoredFileId, Pageable aPageable);
}
