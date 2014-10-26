package net.dorokhov.pony.core.dao;

import net.dorokhov.pony.core.entity.StoredFile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Date;
import java.util.List;

public interface StoredFileDao extends PagingAndSortingRepository<StoredFile, Long> {

	public long countByTag(String aTag);
	public long countByTagAndCreationDateGreaterThan(String aTag, Date aDate);
	public long countByTagAndUpdateDateGreaterThan(String aTag, Date aDate);

	public Page<StoredFile> findByTag(String aTag, Pageable aPageable);

	public List<StoredFile> findByChecksum(String aChecksum);

	public StoredFile findByTagAndChecksum(String aTag, String aChecksum);
}
