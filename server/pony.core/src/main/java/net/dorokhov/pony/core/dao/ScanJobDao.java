package net.dorokhov.pony.core.dao;

import net.dorokhov.pony.core.domain.ScanJob;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Collection;

public interface ScanJobDao extends PagingAndSortingRepository<ScanJob, Long> {

	public Page<ScanJob> findByStatusIn(Collection<ScanJob.Status> aStatuses, Pageable aPageable);

}
