package net.dorokhov.pony.core.storage;

import net.dorokhov.pony.core.domain.StoredFile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.File;
import java.util.Date;
import java.util.List;

public interface StoredFileService {

	public long getCount();
	public long getCountByTag(String aTag);
	public long getCountByTagAndMinimalDate(String aTag, Date aMinimalDate);

	public long getSizeByTag(String aTag);

	public StoredFile getById(Long aId);
	public StoredFile getByTagAndChecksum(String aTag, String aChecksum);

	public Page<StoredFile> getAll(Pageable aPageable);
	public Page<StoredFile> getByTag(String aTag, Pageable aPageable);
	public List<StoredFile> getByChecksum(String aChecksum);

	public File getFile(Long aId);
	public File getFile(StoredFile aStoredFile);

	public StoredFile save(StoreFileCommand aCommand);

	public void delete(Long aId);
	public void deleteAll();

}
