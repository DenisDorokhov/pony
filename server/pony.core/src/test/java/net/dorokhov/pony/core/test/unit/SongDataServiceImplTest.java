package net.dorokhov.pony.core.test.unit;

import net.dorokhov.pony.core.common.SongData;
import net.dorokhov.pony.core.service.ChecksumServiceImpl;
import net.dorokhov.pony.core.service.SongDataServiceImpl;
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

	private static final File TEST_MP3_FILE = new File(FileUtils.getTempDirectory(), "TestSongDataReaderImpl.mp3");
	private static final File TEST_OGG_FILE = new File(FileUtils.getTempDirectory(), "TestSongDataReaderImpl.ogg");

	private SongDataServiceImpl service;

	@Before
	public void setUp() throws Exception {

		service = new SongDataServiceImpl();
		service.setChecksumService(new ChecksumServiceImpl());

		FileUtils.copyFile(new ClassPathResource(TEST_MP3_PATH).getFile(), TEST_MP3_FILE);
		FileUtils.copyFile(new ClassPathResource(TEST_OGG_PATH).getFile(), TEST_OGG_FILE);
	}

	@After
	public void tearDown() {
		TEST_MP3_FILE.delete();
		TEST_OGG_FILE.delete();
	}

	@Test
	public void test() throws Exception {

		SongData songData = service.read(TEST_MP3_FILE);

		Assert.assertEquals(TEST_MP3_FILE.getAbsolutePath(), songData.getPath());
		Assert.assertEquals("MPEG-1 Layer 3", songData.getFormat());
		Assert.assertEquals("audio/mpeg", songData.getMimeType());
		Assert.assertEquals(Long.valueOf(24797), songData.getSize());
		Assert.assertEquals(Integer.valueOf(1), songData.getDuration());
		Assert.assertEquals(Long.valueOf(128), songData.getBitRate());
		Assert.assertEquals(Integer.valueOf(1), songData.getDiscNumber());
		Assert.assertEquals(Integer.valueOf(1), songData.getDiscCount());
		Assert.assertEquals(Integer.valueOf(1), songData.getTrackNumber());
		Assert.assertEquals(Integer.valueOf(8), songData.getTrackCount());
		Assert.assertEquals("Battery", songData.getName());
		Assert.assertEquals("Metallica", songData.getArtist());
		Assert.assertEquals("Metallica", songData.getAlbumArtist());
		Assert.assertEquals("Master Of Puppets", songData.getAlbum());
		Assert.assertEquals(Integer.valueOf(1986), songData.getYear());
		Assert.assertEquals("Rock", songData.getGenre());
		Assert.assertEquals("image/jpeg", songData.getArtwork().getMimeType());
		Assert.assertEquals("0a6632570700e5f595a75999508fc46d", songData.getArtwork().getChecksum());

		boolean isExceptionThrown = false;

		try {
			service.read(TEST_OGG_FILE);
		} catch (Exception e) {
			isExceptionThrown = true;
		}

		Assert.assertTrue(isExceptionThrown);
	}

}
