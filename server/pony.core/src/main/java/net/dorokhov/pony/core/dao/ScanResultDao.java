package net.dorokhov.pony.core.dao;

import net.dorokhov.pony.core.domain.ScanResult;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface ScanResultDao extends PagingAndSortingRepository<ScanResult, Long> {

}
