package net.dorokhov.pony.core.test.integration;

import net.dorokhov.pony.core.storage.StoredFileSaveCommand;
import net.dorokhov.pony.core.entity.StoredFile;
import net.dorokhov.pony.core.storage.StoredFileService;
import net.dorokhov.pony.core.test.AbstractIntegrationCase;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.PageRequest;

import java.util.Date;

public class StoredFileServiceIT extends AbstractIntegrationCase {

	private static final String TEST_FILE_PATH = "data/image.png";
	private static final String TEST_FILE_MIME_TYPE = "image/png";

	private StoredFileService service;

	@Before
	public void setUp() throws Exception {
		service = context.getBean(StoredFileService.class);
	}

	@Test
	public void test() throws Exception {

		Date minCreationDate = new Date();

		StoredFileSaveCommand command = buildCommand(1);

		StoredFile storedFile = service.save(command);

		checkStoredFile(storedFile, 1);

		storedFile = service.getById(storedFile.getId());

		checkStoredFile(storedFile, 1);

		command = buildCommand(2);

		storedFile = service.save(command);

		checkStoredFile(storedFile, 2);

		Assert.assertEquals(2, service.getCount());
		Assert.assertEquals(2, service.getCountByTag("tag"));
		Assert.assertEquals(2, service.getCountByTagAndMinimalDate("tag", minCreationDate));

		Assert.assertNotNull(service.getFile(storedFile.getId()));
		Assert.assertNotNull(service.getFile(storedFile));

		storedFile = service.getByTagAndChecksum("tag", "checksum1");

		checkStoredFile(storedFile, 1);

		Assert.assertEquals(2, service.getAll(new PageRequest(0, 100)).getTotalElements());
		Assert.assertEquals(2, service.getByTag("tag", new PageRequest(0, 100)).getTotalElements());
		Assert.assertEquals(1, service.getByChecksum("checksum1").size());

		service.deleteById(storedFile.getId());

		Assert.assertEquals(1, service.getCount());

		service.deleteAll();

		Assert.assertEquals(0, service.getCount());
	}

	private StoredFileSaveCommand buildCommand(int aIndex) throws Exception{

		StoredFileSaveCommand command = new StoredFileSaveCommand(StoredFileSaveCommand.Type.COPY, new ClassPathResource(TEST_FILE_PATH).getFile());

		command.setName("file" + aIndex);
		command.setMimeType(TEST_FILE_MIME_TYPE);
		command.setChecksum("checksum" + aIndex);
		command.setTag("tag");
		command.setUserData("userData" + aIndex);

		return command;
	}

	private void checkStoredFile(StoredFile aStoredFile, int aIndex) throws Exception {

		Assert.assertNotNull(aStoredFile.getId());
		Assert.assertNotNull(aStoredFile.getDate());

		Assert.assertEquals("file" + aIndex, aStoredFile.getName());
		Assert.assertEquals("checksum" + aIndex, aStoredFile.getChecksum());
		Assert.assertEquals(new ClassPathResource(TEST_FILE_PATH).getFile().length(), (long) aStoredFile.getSize());
		Assert.assertEquals("tag", aStoredFile.getTag());
		Assert.assertEquals("userData" + aIndex, aStoredFile.getUserData());

		Assert.assertEquals(TEST_FILE_MIME_TYPE, aStoredFile.getMimeType());
		Assert.assertNotNull(aStoredFile.getPath());
	}

}
