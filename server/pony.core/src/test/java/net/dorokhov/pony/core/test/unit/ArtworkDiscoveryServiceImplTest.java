package net.dorokhov.pony.core.test.unit;

import net.dorokhov.pony.core.audio.SongDataServiceImpl;
import net.dorokhov.pony.core.library.ArtworkDiscoveryServiceImpl;
import net.dorokhov.pony.core.library.FileScanServiceImpl;
import net.dorokhov.pony.core.file.ChecksumServiceImpl;
import net.dorokhov.pony.core.file.FileTypeServiceImpl;
import net.dorokhov.pony.core.image.ImageSizeReaderImpl;
import net.dorokhov.pony.core.library.file.LibraryFolder;
import net.dorokhov.pony.core.library.file.LibraryImage;
import net.dorokhov.pony.core.library.file.LibrarySong;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class ArtworkDiscoveryServiceImplTest {

	private static final String TEST_FILE_PATH = "data/image.png"; // red picture 90x100
	private static final File TEST_FOLDER = new File(FileUtils.getTempDirectory(), "ArtworkServiceImplTest");

	private FileScanServiceImpl fileScanService;

	private ArtworkDiscoveryServiceImpl artworkDiscoveryService;

	@Before
	public void setUp() throws Exception {

		SongDataServiceImpl songDataService = new SongDataServiceImpl();

		songDataService.setChecksumService(new ChecksumServiceImpl());

		fileScanService = new FileScanServiceImpl();
		fileScanService.setFileTypeService(new FileTypeServiceImpl());
		fileScanService.setImageSizeReader(new ImageSizeReaderImpl());

		artworkDiscoveryService = new ArtworkDiscoveryServiceImpl();

		artworkDiscoveryService.setArtworkMinSizeRatio(0.9);
		artworkDiscoveryService.setArtworkMaxSizeRatio(1.1);

		artworkDiscoveryService.setArtworkFileNames(new HashSet<>(Arrays.asList("cover")));
		artworkDiscoveryService.setArtworkFolderNames(new HashSet<>(Arrays.asList("artwork")));

		FileUtils.deleteDirectory(TEST_FOLDER);
	}

	@After
	public void tearDown() throws Exception {
		FileUtils.deleteDirectory(TEST_FOLDER);
	}

	@Test
	public void testDiscoveryOfNotExistingArtwork() throws Exception {

		TEST_FOLDER.mkdir();

		FileUtils.touch(new File(TEST_FOLDER, "song.mp3"));

		LibraryFolder testFolder = fileScanService.scanFolder(TEST_FOLDER);

		LibraryImage artwork = artworkDiscoveryService.discoverArtwork(getChildSongByName(testFolder, "song.mp3"));

		Assert.assertNull(artwork);
	}

	@Test
	public void testDiscoveryInCurrentFolder() throws Exception {

		File testImage = new ClassPathResource(TEST_FILE_PATH).getFile();

		TEST_FOLDER.mkdir();

		FileUtils.touch(new File(TEST_FOLDER, "song.mp3"));

		File image = new File(TEST_FOLDER, "cover.png");

		FileUtils.copyFile(testImage, image);

		LibraryFolder testFolder = fileScanService.scanFolder(TEST_FOLDER);

		LibraryImage artwork = artworkDiscoveryService.discoverArtwork(getChildSongByName(testFolder, "song.mp3"));

		Assert.assertEquals("cover.png", artwork.getFile().getName());
	}

	@Test
	public void testDiscoveryInParentFolder() throws Exception {

		File testImage = new ClassPathResource(TEST_FILE_PATH).getFile();

		TEST_FOLDER.mkdir();

		File childFolder = new File(TEST_FOLDER, "test");

		childFolder.mkdir();

		File image = new File(TEST_FOLDER, "cover.jpg");

		FileUtils.copyFile(testImage, image);

		FileUtils.touch(new File(childFolder, "song.mp3"));

		LibraryFolder testFolder = fileScanService.scanFolder(TEST_FOLDER);

		LibraryImage artwork = artworkDiscoveryService.discoverArtwork(getChildSongByName(getChildFolderByName(testFolder, "test"), "song.mp3"));

		Assert.assertEquals("cover.jpg", artwork.getFile().getName());
	}

	@Test
	public void testDiscoveryInChildFolder() throws Exception {

		File testImage = new ClassPathResource(TEST_FILE_PATH).getFile();

		TEST_FOLDER.mkdir();

		FileUtils.touch(new File(TEST_FOLDER, "song.mp3"));

		File artworkFolder = new File(TEST_FOLDER, "artwork");

		artworkFolder.mkdir();

		File image = new File(artworkFolder, "image.jpg");

		FileUtils.copyFile(testImage, image);

		LibraryFolder testFolder = fileScanService.scanFolder(TEST_FOLDER);

		LibraryImage artwork = artworkDiscoveryService.discoverArtwork(getChildSongByName(testFolder, "song.mp3"));

		Assert.assertEquals("image.jpg", artwork.getFile().getName());
	}

	@Test
	public void testFailedDiscovery() throws Exception {

		TEST_FOLDER.mkdir();

		FileUtils.touch(new File(TEST_FOLDER, "song.mp3"));
		FileUtils.touch(new File(TEST_FOLDER, "cover.jpg"));

		LibraryFolder testFolder = fileScanService.scanFolder(TEST_FOLDER);

		LibraryImage artwork = artworkDiscoveryService.discoverArtwork(getChildSongByName(testFolder, "song.mp3"));

		Assert.assertNull(artwork);
	}

	@Test
	public void testConfiguration() {

		Set<String> result;

		artworkDiscoveryService.setArtworkFileNames(",cover, folder,artwork");

		result = artworkDiscoveryService.getArtworkFileNames();

		Assert.assertEquals(3, result.size());
		Assert.assertTrue(result.contains("cover"));
		Assert.assertTrue(result.contains("folder"));
		Assert.assertTrue(result.contains("artwork"));

		artworkDiscoveryService.setArtworkFolderNames("artwork, covers");

		result = artworkDiscoveryService.getArtworkFolderNames();

		Assert.assertEquals(2, result.size());
		Assert.assertTrue(result.contains("artwork"));
		Assert.assertTrue(result.contains("covers"));
	}

	private LibraryFolder getChildFolderByName(LibraryFolder aFolder, String aName) {

		for (LibraryFolder folder : aFolder.getChildFolders()) {
			if (folder.getFile().getName().equals(aName)) {
				return folder;
			}
		}

		return null;
	}

	private LibrarySong getChildSongByName(LibraryFolder aFolder, String aName) {

		for (LibrarySong song : aFolder.getChildSongs()) {
			if (song.getFile().getName().equals(aName)) {
				return song;
			}
		}

		return null;
	}
}
