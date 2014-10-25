package net.dorokhov.pony.core.dao;

import net.dorokhov.pony.core.entity.StoredFile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import javax.persistence.LockModeType;
import java.util.List;

public interface StoredFileDao extends PagingAndSortingRepository<StoredFile, Long> {

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("SELECT sf FROM StoredFile sf WHERE sf.id = ?1")
	public StoredFile findByIdAndLock(Long aId);

	public Page<StoredFile> findByTag(String aTag, Pageable aPageable);

	public List<StoredFile> findByChecksum(String aChecksum);

	public StoredFile findByTagAndChecksum(String aTag, String aChecksum);
}
