package net.dorokhov.pony.core.dao;

import net.dorokhov.pony.core.domain.StoredFile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface StoredFileDao extends PagingAndSortingRepository<StoredFile, Long> {

	public Page<StoredFile> findByTag(String aTag, Pageable aPageable);

	public List<StoredFile> findByChecksum(String aChecksum);

	public StoredFile findByTagAndChecksum(String aTag, String aChecksum);
}
