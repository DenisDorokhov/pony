package net.dorokhov.pony.core.dao;

import net.dorokhov.pony.core.domain.Album;
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

	public List<Album> findByArtworkId(Long aStoredFileId, Sort aSort);
	public List<Album> findByArtistId(Long aArtistId, Sort aSort);

	public Album findByArtistIdAndName(Long aArtistId, String aName);
}
