package net.dorokhov.pony.core.test.unit;

import net.dorokhov.pony.core.audio.data.SongDataReadable;
import net.dorokhov.pony.core.audio.data.SongDataWritable;
import net.dorokhov.pony.core.file.ChecksumServiceImpl;
import net.dorokhov.pony.core.audio.SongDataServiceImpl;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.bridge.SLF4JBridgeHandler;
import org.springframework.core.io.ClassPathResource;

import java.io.File;

public class SongDataServiceImplTest {

	private static final String TEST_MP3_PATH = "data/Metallica-Battery-with_artwork.mp3"; // see tags in data/mp3-info.txt
	private static final String TEST_OGG_PATH = "data/test.ogg";
	private static final String TEST_IMAGE_PATH = "data/image.png";

	private static final File TEST_MP3_FILE = new File(FileUtils.getTempDirectory(), "TestSongDataReaderImpl.mp3");
	private static final File TEST_OGG_FILE = new File(FileUtils.getTempDirectory(), "TestSongDataReaderImpl.ogg");
	private static final File TEST_IMAGE_FILE = new File(FileUtils.getTempDirectory(), "TestSongDataReaderImpl.png");

	private SongDataServiceImpl service;

	@Before
	public void setUp() throws Exception {

		SLF4JBridgeHandler.removeHandlersForRootLogger();
		SLF4JBridgeHandler.install();

		service = new SongDataServiceImpl();
		service.setChecksumService(new ChecksumServiceImpl());

		FileUtils.copyFile(new ClassPathResource(TEST_MP3_PATH).getFile(), TEST_MP3_FILE);
	}

	@After
	public void tearDown() {
		TEST_MP3_FILE.delete();
		TEST_OGG_FILE.delete();
		TEST_IMAGE_FILE.delete();
	}

	@Test
	public void testReading() throws Exception {

		SongDataReadable songData = service.read(TEST_MP3_FILE);

		doTestReadData(songData);

		FileUtils.copyFile(new ClassPathResource(TEST_OGG_PATH).getFile(), TEST_OGG_FILE);

		boolean isExceptionThrown = false;

		try {
			service.read(TEST_OGG_FILE);
		} catch (Exception e) {
			isExceptionThrown = true;
		}

		Assert.assertTrue(isExceptionThrown);
	}

	@Test
	public void testWriting() throws Exception {

		FileUtils.copyFile(new ClassPathResource(TEST_IMAGE_PATH).getFile(), TEST_IMAGE_FILE);

		SongDataWritable command = new SongDataWritable();

		command.setDiscNumber(2);
		command.setDiscCount(3);

		command.setTrackNumber(3);
		command.setTrackCount(10);

		command.setTitle("SomeTitle");
		command.setArtist("SomeArtist");
		command.setAlbumArtist("SomeAlbumArtist");
		command.setAlbum("SomeAlbum");

		command.setYear(1991);

		command.setGenre("SomeGenre");

		command.setArtwork(TEST_IMAGE_FILE);

		doTestWrittenData(service.write(TEST_MP3_FILE, command));
		doTestWrittenData(service.read(TEST_MP3_FILE));

		FileUtils.copyFile(new ClassPathResource(TEST_OGG_PATH).getFile(), TEST_OGG_FILE);

		command = new SongDataWritable();

		boolean isExceptionThrown = false;

		try {
			service.write(TEST_OGG_FILE, command);
		} catch (Exception e) {
			isExceptionThrown = true;
		}

		Assert.assertTrue(isExceptionThrown);
	}

	@Test
	public void testPartialWriting() throws Exception {

		FileUtils.copyFile(new ClassPathResource(TEST_IMAGE_PATH).getFile(), TEST_IMAGE_FILE);

		SongDataWritable command = new SongDataWritable();

		command.setDiscNumber(2);
		command.setDiscCount(3);

		command.setTrackNumber(3);
		command.setTrackCount(10);

		command.setTitle("SomeTitle");
		command.setArtist("SomeArtist");
		command.setAlbumArtist("SomeAlbumArtist");
		command.setAlbum("SomeAlbum");

		command.setYear(1991);

		command.setGenre("SomeGenre");

		command.setArtwork(TEST_IMAGE_FILE);

		command.setWriteDiscNumber(false);
		command.setWriteDiscCount(false);

		command.setWriteTrackNumber(false);
		command.setWriteTrackCount(false);

		command.setWriteTitle(false);
		command.setWriteArtist(false);
		command.setWriteAlbumArtist(false);
		command.setWriteAlbum(false);

		command.setWriteYear(false);

		command.setWriteGenre(false);

		command.setWriteArtwork(false);

		doTestReadData(service.write(TEST_MP3_FILE, command));
		doTestReadData(service.read(TEST_MP3_FILE));
	}

	private void doTestReadData(SongDataReadable aSongData) {
		Assert.assertEquals(TEST_MP3_FILE.getAbsolutePath(), aSongData.getPath());
		Assert.assertEquals("MPEG-1 Layer 3", aSongData.getFormat());
		Assert.assertEquals("audio/mpeg3", aSongData.getMimeType());
		Assert.assertEquals(Long.valueOf(24797), aSongData.getSize());
		Assert.assertEquals(Integer.valueOf(1), aSongData.getDuration());
		Assert.assertEquals(Long.valueOf(128), aSongData.getBitRate());
		Assert.assertEquals(Integer.valueOf(1), aSongData.getDiscNumber());
		Assert.assertEquals(Integer.valueOf(1), aSongData.getDiscCount());
		Assert.assertEquals(Integer.valueOf(1), aSongData.getTrackNumber());
		Assert.assertEquals(Integer.valueOf(8), aSongData.getTrackCount());
		Assert.assertEquals("Battery", aSongData.getTitle());
		Assert.assertEquals("Metallica", aSongData.getArtist());
		Assert.assertEquals("Metallica", aSongData.getAlbumArtist());
		Assert.assertEquals("Master Of Puppets", aSongData.getAlbum());
		Assert.assertEquals(Integer.valueOf(1986), aSongData.getYear());
		Assert.assertEquals("Rock", aSongData.getGenre());
		Assert.assertEquals("image/jpeg", aSongData.getArtwork().getMimeType());
		Assert.assertEquals("0a6632570700e5f595a75999508fc46d", aSongData.getArtwork().getChecksum());
	}

	private void doTestWrittenData(SongDataReadable aSongData) {
		Assert.assertEquals(TEST_MP3_FILE.getAbsolutePath(), aSongData.getPath());
		Assert.assertEquals("MPEG-1 Layer 3", aSongData.getFormat());
		Assert.assertEquals("audio/mpeg3", aSongData.getMimeType());
		Assert.assertEquals(Long.valueOf(24797), aSongData.getSize());
		Assert.assertEquals(Integer.valueOf(1), aSongData.getDuration());
		Assert.assertEquals(Long.valueOf(128), aSongData.getBitRate());
		Assert.assertEquals(Integer.valueOf(2), aSongData.getDiscNumber());
		Assert.assertEquals(Integer.valueOf(3), aSongData.getDiscCount());
		Assert.assertEquals(Integer.valueOf(3), aSongData.getTrackNumber());
		Assert.assertEquals(Integer.valueOf(10), aSongData.getTrackCount());
		Assert.assertEquals("SomeTitle", aSongData.getTitle());
		Assert.assertEquals("SomeArtist", aSongData.getArtist());
		Assert.assertEquals("SomeAlbumArtist", aSongData.getAlbumArtist());
		Assert.assertEquals("SomeAlbum", aSongData.getAlbum());
		Assert.assertEquals(Integer.valueOf(1991), aSongData.getYear());
		Assert.assertEquals("SomeGenre", aSongData.getGenre());
		Assert.assertEquals("image/png", aSongData.getArtwork().getMimeType());
		Assert.assertEquals("fc3adeae14ecc5f77d6dde58d40b1559", aSongData.getArtwork().getChecksum());
	}

}
