package net.dorokhov.pony.core.storage;

import net.dorokhov.pony.core.entity.StoredFile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.File;
import java.util.Date;
import java.util.List;

public interface StoredFileService {

	public long getCount();
	public long getCountByTag(String aTag);
	public long getCountByTagAndCreationDate(String aTag, Date aMinimalCreationDate);
	public long getCountByTagAndUpdateDate(String aTag, Date aMinimalUpdateDate);

	public StoredFile getById(Long aId);
	public StoredFile getByTagAndChecksum(String aTag, String aChecksum);

	public Page<StoredFile> getAll(Pageable aPageable);
	public Page<StoredFile> getByTag(String aTag, Pageable aPageable);
	public List<StoredFile> getByChecksum(String aChecksum);

	public File getFile(Long aId);
	public File getFile(StoredFile aStoredFile);

	public StoredFile save(StoredFileSaveCommand aCommand);

	public void deleteById(Long aId);
	public void deleteAll();
}
