package net.dorokhov.pony.core.dao;

import net.dorokhov.pony.core.domain.Song;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface SongDao extends PagingAndSortingRepository<Song, Long> {

	public long countByAlbumId(Long aAlbumId);
	public long countByAlbumArtistId(Long aArtistId);
	public long countByArtworkId(Long aStoredFileId);
	public long countByGenreId(Long aGenreId);

	public Song findByPath(String aPath);

	public Page<Song> findByArtworkId(Long aStoredFileId, Pageable aPageable);

	@Query("SELECT s FROM Song s " +
			"INNER JOIN FETCH s.genre " +
			"INNER JOIN FETCH s.album a " +
			"INNER JOIN FETCH a.artist " +
			"WHERE s.id = ?1")
	public Song findById(Long aId);

	@Query("SELECT s FROM Song s " +
			"INNER JOIN FETCH s.genre " +
			"INNER JOIN FETCH s.album a " +
			"INNER JOIN FETCH a.artist " +
			"WHERE a.id = ?1")
	public List<Song> findByAlbumId(Long aAlbumId, Sort aSort);

	@Query("SELECT s FROM Song s " +
			"INNER JOIN FETCH s.genre " +
			"INNER JOIN FETCH s.album a " +
			"INNER JOIN FETCH a.artist " +
			"WHERE a.artist.id = ?1")
	public List<Song> findByAlbumArtistId(Long aArtistId, Sort aSort);

	@Query("SELECT s FROM Song s " +
			"INNER JOIN FETCH s.genre " +
			"INNER JOIN FETCH s.album a " +
			"INNER JOIN FETCH a.artist " +
			"WHERE s.genre.id = ?1")
	public List<Song> findByGenreId(Long aGenreId);
}
