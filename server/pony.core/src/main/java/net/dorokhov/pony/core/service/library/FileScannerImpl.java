package net.dorokhov.pony.core.service.library;

import net.dorokhov.pony.core.service.file.FileTypeService;
import net.dorokhov.pony.core.service.image.ImageSize;
import net.dorokhov.pony.core.service.image.ImageSizeReader;
import net.dorokhov.pony.core.service.library.common.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
public class FileScannerImpl implements FileScanner {

	private FileTypeService fileTypeService;

	private ImageSizeReader imageSizeReader;

	@Autowired
	public void setFileTypeService(FileTypeService aFileTypeService) {
		fileTypeService = aFileTypeService;
	}

	@Autowired
	public void setImageSizeReader(ImageSizeReader aImageSizeReader) {
		imageSizeReader = aImageSizeReader;
	}

	@Override
	public LibraryFile scanFile(File aFile) {
		return doScanFile(aFile, null);
	}

	@Override
	public LibraryFolder scanFolder(File aFolder) {
		return doScanFolder(aFolder, null);
	}

	private AbstractLibraryFile doScanFile(File aFile, LibraryFolder aParentFolder) {

		if (!aFile.exists()) {
			throw new IllegalArgumentException("File must exist.");
		}
		if (aFile.isDirectory()) {
			throw new IllegalArgumentException("File must not be a directory.");
		}

		AbstractLibraryFile result = null;

		FileTypeService.FileType type = fileTypeService.getFileType(aFile.getName());

		if (type != null) {
			switch (type) {

				case IMAGE:
					result = new LibraryImageImpl(aFile, aParentFolder);
					break;

				case SONG:
					result = new LibrarySongImpl(aFile, aParentFolder);
					break;
			}
		}

		return result;
	}

	private LibraryFolderImpl doScanFolder(File aFolder, LibraryFolder aParentFolder) {

		if (!aFolder.exists()) {
			throw new IllegalArgumentException("File must exist.");
		}
		if (!aFolder.isDirectory()) {
			throw new IllegalArgumentException("File must be a directory.");
		}

		LibraryFolderImpl currentFolder = new LibraryFolderImpl(aFolder, aParentFolder);

		File[] fileList = aFolder.listFiles();

		if (fileList != null) {
			for (File childFile : fileList) {
				if (childFile.isDirectory()) {
					currentFolder.getChildFoldersMutable().add(doScanFolder(childFile, currentFolder));
				} else {

					LibraryFile libraryFile = doScanFile(childFile, currentFolder);

					if (libraryFile != null) {
						if (libraryFile instanceof LibraryImage) {
							currentFolder.getChildImagesMutable().add((LibraryImage) libraryFile);
						} else if (libraryFile instanceof LibrarySong) {
							currentFolder.getChildSongsMutable().add((LibrarySong) libraryFile);
						} else {
							throw new RuntimeException("Unknown file type.");
						}
					}
				}
			}
		}

		return currentFolder;
	}

	private abstract class AbstractLibraryNode implements LibraryNode {

		private File file;

		private LibraryFolder parentFolder;

		protected AbstractLibraryNode(File aFile, LibraryFolder aParentFolder) {
			setFile(aFile);
			setParentFolder(aParentFolder);
		}

		@Override
		public File getFile() {
			return file;
		}

		public void setFile(File aFile) {
			file = aFile;
		}

		@Override
		public LibraryFolder getParentFolder() {
			return parentFolder;
		}

		public void setParentFolder(LibraryFolder aParentFolder) {
			parentFolder = aParentFolder;
		}

		@Override
		public int hashCode() {
			return file.hashCode();
		}

		@Override
		public boolean equals(Object aObj) {

			if (this == aObj) {
				return true;
			}

			if (aObj != null && getClass().equals(aObj.getClass())) {

				AbstractLibraryNode that = (AbstractLibraryNode) aObj;

				return file.equals(that.file);
			}

			return false;
		}
	}

	private class LibraryFolderImpl extends AbstractLibraryNode implements LibraryFolder {

		private final Set<LibraryImage> childImages;
		private final Set<LibrarySong> childSongs;
		private final Set<LibraryFolderImpl> childFolders;

		public LibraryFolderImpl(File aFile, LibraryFolder aParentFolder) {

			super(aFile, aParentFolder);

			childImages = new HashSet<>();
			childSongs = new HashSet<>();
			childFolders = new HashSet<>();
		}

		@Override
		public Set<LibraryImage> getChildImages() {
			return new HashSet<>(childImages);
		}

		@Override
		public Set<LibraryImage> getChildImages(boolean aRecursive) {

			Set<LibraryImage> result = new HashSet<>();

			doGetChildImages(result, aRecursive);

			return result;
		}

		@Override
		public Set<LibrarySong> getChildSongs() {
			return new HashSet<>(childSongs);
		}

		@Override
		public Set<LibrarySong> getChildSongs(boolean aRecursive) {

			Set<LibrarySong> result = new HashSet<>();

			doGetChildSongs(result, aRecursive);

			return result;
		}

		@Override
		public Set<LibraryFolder> getChildFolders() {
			return new HashSet<LibraryFolder>(childFolders);
		}

		@Override
		public Set<LibraryFolder> getChildFolders(boolean aRecursive) {

			Set<LibraryFolder> result = new HashSet<>();

			doGetChildFolders(result, aRecursive);

			return result;
		}

		@Override
		public Set<LibraryFile> getChildFiles() {
			return getChildFiles(false);
		}

		@Override
		public Set<LibraryFile> getChildFiles(boolean aRecursive) {

			Set<LibraryFile> result = new HashSet<>();

			doGetChildFiles(result, aRecursive);

			return result;
		}

		public Set<LibraryImage> getChildImagesMutable() {
			return childImages;
		}

		public Set<LibrarySong> getChildSongsMutable() {
			return childSongs;
		}

		public Set<LibraryFolderImpl> getChildFoldersMutable() {
			return childFolders;
		}

		private void doGetChildImages(Set<LibraryImage> aResult, boolean aRecursive) {

			for (LibraryImage image : getChildImages()) {
				aResult.add(image);
			}

			if (aRecursive) {
				for (LibraryFolderImpl folder : childFolders) {
					folder.doGetChildImages(aResult, true);
				}
			}
		}

		private void doGetChildSongs(Set<LibrarySong> aResult, boolean aRecursive) {

			for (LibrarySong song : getChildSongs()) {
				aResult.add(song);
			}

			if (aRecursive) {
				for (LibraryFolderImpl folder : childFolders) {
					folder.doGetChildSongs(aResult, true);
				}
			}
		}

		private void doGetChildFiles(Set<LibraryFile> aResult, boolean aRecursive) {

			aResult.addAll(getChildImages());
			aResult.addAll(getChildSongs());

			if (aRecursive) {
				for (LibraryFolderImpl folder : childFolders) {
					folder.doGetChildFiles(aResult, true);
				}
			}
		}

		private void doGetChildFolders(Set<LibraryFolder> aResult, boolean aRecursive) {

			for (LibraryFolder folder : getChildFolders()) {
				aResult.add(folder);
			}

			if (aRecursive) {
				for (LibraryFolderImpl folder : childFolders) {
					folder.doGetChildFolders(aResult, true);
				}
			}
		}

	}

	private abstract class AbstractLibraryFile extends AbstractLibraryNode implements LibraryFile {

		private final Object mimeTypeLock = new Object();

		private AtomicBoolean isMimeTypeNull = new AtomicBoolean();

		private volatile String mimeType;

		protected AbstractLibraryFile(File aFile, LibraryFolder aParentFolder) {
			super(aFile, aParentFolder);
		}

		@Override
		public String getMimeType() {

			if (mimeType == null && !isMimeTypeNull.get()) {
				synchronized (mimeTypeLock) {
					if (mimeType == null && !isMimeTypeNull.get()) {

						mimeType = fileTypeService.getFileMimeType(getFile().getName());

						if (mimeType == null) {
							isMimeTypeNull.set(true);
						}
					}
				}
			}

			return mimeType;
		}
	}

	private class LibraryImageImpl extends AbstractLibraryFile implements LibraryImage {

		private final Object sizeLock = new Object();

		private volatile ImageSize size;

		private LibraryImageImpl(File aFile, LibraryFolder aParentFolder) {
			super(aFile, aParentFolder);
		}

		@Override
		public ImageSize getSize() throws Exception {

			if (size == null) {
				synchronized (sizeLock) {
					if (size == null) {
						size = imageSizeReader.read(getFile());
					}
				}
			}

			return size;
		}

	}

	private class LibrarySongImpl extends AbstractLibraryFile implements LibrarySong {

		private LibrarySongImpl(File aFile, LibraryFolder aParentFolder) {
			super(aFile, aParentFolder);
		}

	}
}
