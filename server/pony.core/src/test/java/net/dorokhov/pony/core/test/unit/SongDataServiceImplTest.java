package net.dorokhov.pony.core.test.unit;

import net.dorokhov.pony.core.service.audio.SongData;
import net.dorokhov.pony.core.service.audio.SongDataWriteCommand;
import net.dorokhov.pony.core.service.file.ChecksumServiceImpl;
import net.dorokhov.pony.core.service.audio.SongDataServiceImpl;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

import java.io.File;

public class SongDataServiceImplTest {

	private static final String TEST_MP3_PATH = "data/Metallica-Battery-with_artwork.mp3"; // see tags in data/mp3-info.txt
	private static final String TEST_OGG_PATH = "data/test.ogg";
	private static final String TEST_IMAGE_PATH = "data/image-blue.png";

	private static final File TEST_MP3_FILE = new File(FileUtils.getTempDirectory(), "TestSongDataReaderImpl.mp3");
	private static final File TEST_OGG_FILE = new File(FileUtils.getTempDirectory(), "TestSongDataReaderImpl.ogg");
	private static final File TEST_IMAGE_FILE = new File(FileUtils.getTempDirectory(), "TestSongDataReaderImpl.png");

	private SongDataServiceImpl service;

	@Before
	public void setUp() throws Exception {

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

		SongData songData = service.read(TEST_MP3_FILE);

		Assert.assertEquals(TEST_MP3_FILE.getAbsolutePath(), songData.getPath());
		Assert.assertEquals("MPEG-1 Layer 3", songData.getFormat());
		Assert.assertEquals("audio/mpeg3", songData.getMimeType());
		Assert.assertEquals(Long.valueOf(24797), songData.getSize());
		Assert.assertEquals(Integer.valueOf(1), songData.getDuration());
		Assert.assertEquals(Long.valueOf(128), songData.getBitRate());
		Assert.assertEquals(Integer.valueOf(1), songData.getDiscNumber());
		Assert.assertEquals(Integer.valueOf(1), songData.getDiscCount());
		Assert.assertEquals(Integer.valueOf(1), songData.getTrackNumber());
		Assert.assertEquals(Integer.valueOf(8), songData.getTrackCount());
		Assert.assertEquals("Battery", songData.getTitle());
		Assert.assertEquals("Metallica", songData.getArtist());
		Assert.assertEquals("Metallica", songData.getAlbumArtist());
		Assert.assertEquals("Master Of Puppets", songData.getAlbum());
		Assert.assertEquals(Integer.valueOf(1986), songData.getYear());
		Assert.assertEquals("Rock", songData.getGenre());
		Assert.assertEquals("image/jpeg", songData.getArtwork().getMimeType());
		Assert.assertEquals("0a6632570700e5f595a75999508fc46d", songData.getArtwork().getChecksum());

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

		SongDataWriteCommand command = new SongDataWriteCommand(TEST_MP3_FILE);

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

		doTestWrittenData(service.write(command));
		doTestWrittenData(service.read(TEST_MP3_FILE));
	}

	private void doTestWrittenData(SongData aSongData) {
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
		Assert.assertEquals("c7162a48f6e6eb9211376339cd38b1d6", aSongData.getArtwork().getChecksum());
	}

}
