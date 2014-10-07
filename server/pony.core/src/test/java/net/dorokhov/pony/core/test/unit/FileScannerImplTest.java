package net.dorokhov.pony.core.test.unit;

import net.dorokhov.pony.core.common.ScannedFile;
import net.dorokhov.pony.core.common.ScannedFolder;
import net.dorokhov.pony.core.service.FileScanner;
import net.dorokhov.pony.core.service.FileScannerImpl;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FileScannerImplTest {

	private static final File TEST_FOLDER = new File(FileUtils.getTempDirectory(), "FileScannerImplTest");

	private static final String TEST_IMAGE_PATH = "data/image.png";
	private static final String TEST_SONG_PATH = "data/Metallica-Battery-with_artwork.mp3";
	private static final String TEST_OTHER_PATH = "data/mp3-info.txt";

	private FileScanner service;

	@Before
	public void setUp() throws Exception {

		service = new FileScannerImpl();

		FileUtils.deleteDirectory(TEST_FOLDER);
	}

	@After
	public void tearDown() throws Exception {
		FileUtils.deleteDirectory(TEST_FOLDER);
	}

	@Test
	public void testScanFile() throws Exception {

		File testImage = new ClassPathResource(TEST_IMAGE_PATH).getFile();
		File testSong = new ClassPathResource(TEST_SONG_PATH).getFile();
		File testOther = new ClassPathResource(TEST_OTHER_PATH).getFile();

		ScannedFile scannedImage = service.scanFile(testImage);

		Assert.assertNotNull(scannedImage);
		Assert.assertEquals(ScannedFile.Type.IMAGE, scannedImage.getType());

		ScannedFile scannedSong = service.scanFile(testSong);

		Assert.assertNotNull(scannedSong);
		Assert.assertEquals(ScannedFile.Type.SONG, scannedSong.getType());

		Assert.assertNull(service.scanFile(testOther));

		boolean isExceptionThrown;

		isExceptionThrown = false;
		try {
			service.scanFile(TEST_FOLDER); // file not found
		} catch (IllegalArgumentException e) {
			isExceptionThrown = true;
		}
		Assert.assertTrue(isExceptionThrown);

		TEST_FOLDER.mkdir();

		isExceptionThrown = false;
		try {
			service.scanFile(TEST_FOLDER); // file is directory
		} catch (IllegalArgumentException e) {
			isExceptionThrown = true;
		}
		Assert.assertTrue(isExceptionThrown);
	}

	@Test
	public void testScanFolder() throws Exception {

		boolean isExceptionThrown;

		isExceptionThrown = false;
		try {
			service.scanFolder(TEST_FOLDER); // file not found
		} catch (IllegalArgumentException e) {
			isExceptionThrown = true;
		}
		Assert.assertTrue(isExceptionThrown);

		createTestFileTree();

		doTestRoot(service.scanFolder(TEST_FOLDER));

		isExceptionThrown = false;
		try {
			service.scanFolder(new ClassPathResource(TEST_IMAGE_PATH).getFile());
		} catch (IllegalArgumentException e) {
			isExceptionThrown = true;
		}
		Assert.assertTrue(isExceptionThrown);
	}

	private void doTestRoot(ScannedFolder aFolder) {

		Assert.assertEquals(0, aFolder.getChildFiles().size());
		Assert.assertEquals(1, aFolder.getChildFolders().size());

		Assert.assertEquals(0, aFolder.getImageFiles(false).size());
		Assert.assertEquals(0, aFolder.getSongFiles(false).size());

		ScannedFolder artist = aFolder.getFolderByName("artist");

		Assert.assertNotNull(artist);

		doTestArtist(artist);

		List<String> imageFiles = new ArrayList<>();
		for (ScannedFile file : aFolder.getImageFiles(true)) {
			imageFiles.add(file.getFile().getName());
		}
		Assert.assertEquals(2, imageFiles.size());
		Assert.assertTrue(imageFiles.containsAll(Arrays.asList("cover-01.png", "cover-02.png")));

		List<String> songFiles = new ArrayList<>();
		for (ScannedFile file : aFolder.getSongFiles(true)) {
			songFiles.add(file.getFile().getName());
		}
		Assert.assertEquals(3, songFiles.size());
		Assert.assertTrue(songFiles.containsAll(Arrays.asList("song-01.mp3", "song-02.mp3", "song-03.mp3")));
	}

	private void doTestArtist(ScannedFolder aFolder) {

		Assert.assertEquals(0, aFolder.getChildFiles().size());
		Assert.assertEquals(2, aFolder.getChildFolders().size());

		Assert.assertEquals(0, aFolder.getImageFiles(false).size());
		Assert.assertEquals(0, aFolder.getSongFiles(false).size());

		ScannedFolder album01 = aFolder.getFolderByName("album-01");
		ScannedFolder album02 = aFolder.getFolderByName("album-02");

		Assert.assertNotNull(album01);
		Assert.assertNotNull(album02);

		doTestAlbum01(album01);
		doTestAlbum02(album02);
	}

	private void doTestAlbum01(ScannedFolder aFolder) {

		Assert.assertEquals(3, aFolder.getChildFiles().size());
		Assert.assertEquals(0, aFolder.getChildFolders().size());

		Assert.assertEquals(1, aFolder.getImageFiles(false).size());
		Assert.assertEquals(2, aFolder.getSongFiles(false).size());

		Assert.assertNotNull(aFolder.getFileByName("song-01.mp3"));
		Assert.assertNotNull(aFolder.getFileByName("song-02.mp3"));
		Assert.assertNotNull(aFolder.getFileByName("cover-01.png"));
	}

	private void doTestAlbum02(ScannedFolder aFolder) {

		Assert.assertEquals(2, aFolder.getChildFiles().size());
		Assert.assertEquals(0, aFolder.getChildFolders().size());

		Assert.assertEquals(1, aFolder.getImageFiles(false).size());
		Assert.assertEquals(1, aFolder.getSongFiles(false).size());

		Assert.assertNotNull(aFolder.getFileByName("song-03.mp3"));
		Assert.assertNotNull(aFolder.getFileByName("cover-02.png"));
	}

	/**
	 * Creates the following file tree structure:
	 *
	 * [TEST_FOLDER]
	 * - [artist]
	 * -- [album-01]
	 * --- song-01.mp3
	 * --- song-02.mp3
	 * --- cover-01.png
	 * --- other-01.txt
	 * -- [album-02]
	 * --- song-03.mp3
	 * --- cover-02.png
	 * -- other-02.txt
	 */
	private void createTestFileTree() throws Exception {

		File testImage = new ClassPathResource(TEST_IMAGE_PATH).getFile();
		File testSong = new ClassPathResource(TEST_SONG_PATH).getFile();
		File testOther = new ClassPathResource(TEST_OTHER_PATH).getFile();

		TEST_FOLDER.mkdir();

		File artist = new File(TEST_FOLDER, "artist");

		artist.mkdir();

		File album01 = new File(artist, "album-01");
		File album02 = new File(artist, "album-02");

		album01.mkdir();
		album02.mkdir();

		File song01 = new File(album01, "song-01.mp3");
		File song02 = new File(album01, "song-02.mp3");

		FileUtils.copyFile(testSong, song01);
		FileUtils.copyFile(testSong, song02);

		File cover01 = new File(album01, "cover-01.png");

		FileUtils.copyFile(testImage, cover01);

		File other01 = new File(album01, "other-01.txt");

		FileUtils.copyFile(testOther, other01);

		File song03 = new File(album02, "song-03.mp3");

		FileUtils.copyFile(testSong, song03);

		File cover02 = new File(album02, "cover-02.png");

		FileUtils.copyFile(testImage, cover02);

		File other02 = new File(artist, "other-02.txt");

		FileUtils.copyFile(testOther, other02);
	}
}
