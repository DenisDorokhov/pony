package net.dorokhov.pony.core.service;

import net.dorokhov.pony.core.common.LibraryFolder;
import net.dorokhov.pony.core.common.LibraryImage;
import net.dorokhov.pony.core.common.LibrarySong;
import net.dorokhov.pony.core.common.SimpleImageInfo;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ArtworkDiscoveryServiceImpl implements ArtworkDiscoveryService {

	private final Logger log = LoggerFactory.getLogger(getClass());

	private double artworkMinSizeRatio = 0.9;
	private double artworkMaxSizeRatio = 1.1;

	private Set<String> artworkFileNames = new HashSet<>();
	private Set<String> artworkFolderNames = new HashSet<>();

	public double getArtworkMinSizeRatio() {
		return artworkMinSizeRatio;
	}

	@Value("${library.artworkMinSizeRatio}")
	public void setArtworkMinSizeRatio(double aArtworkMinSizeRatio) {
		artworkMinSizeRatio = aArtworkMinSizeRatio;
	}

	public double getArtworkMaxSizeRatio() {
		return artworkMaxSizeRatio;
	}

	@Value("${library.artworkMaxSizeRatio}")
	public void setArtworkMaxSizeRatio(double aArtworkMaxSizeRatio) {
		artworkMaxSizeRatio = aArtworkMaxSizeRatio;
	}

	public Set<String> getArtworkFileNames() {
		return new HashSet<>(artworkFileNames);
	}

	public void setArtworkFileNames(Set<String> aArtworkFileNames) {
		artworkFileNames = new HashSet<>(aArtworkFileNames);
	}

	public Set<String> getArtworkFolderNames() {
		return new HashSet<>(artworkFolderNames);
	}

	public void setArtworkFolderNames(Set<String> aArtworkFolderNames) {
		artworkFolderNames = new HashSet<>(aArtworkFolderNames);
	}

	@Value("${library.artworkFileNames}")
	public void setArtworkFileNames(String aArtworkFileNames) {
		setArtworkFileNames(splitCommaSeparatedList(aArtworkFileNames));
	}

	@Value("${library.artworkFolderNames}")
	public void setArtworkFolderNames(String aArtworkFolderNames) {
		setArtworkFolderNames(splitCommaSeparatedList(aArtworkFolderNames));
	}

	@Override
	public LibraryImage discoverArtwork(LibrarySong aSong) {

		LibraryImage artwork = null;

		LibraryFolder folder = aSong.getParentFolder();

		while (folder != null) {

			artwork = doFetchArtwork(folder);

			if (artwork != null) {
				break;
			} else {
				folder = folder.getParentFolder();
			}
		}

		return artwork;
	}

	private LibraryImage doFetchArtwork(LibraryFolder aFolder) {

		LibraryImage artwork = fetchArtworkFromFolder(aFolder);

		if (artwork == null) {

			for (LibraryFolder childFolder : aFolder.getChildFolders()) {
				if (isFolderArtwork(childFolder)) {

					artwork = fetchArtworkFromFolder(childFolder);

					if (artwork != null) {
						break;
					}
				}
			}
		}

		return artwork;
	}

	private boolean isImageArtworkByName(LibraryImage aImage) {

		String name = FilenameUtils.getBaseName(aImage.getFile().getAbsolutePath()).toLowerCase();

		return artworkFileNames.contains(name);
	}

	private boolean isImageArtworkBySize(LibraryImage aImage) {

		SimpleImageInfo info = null;

		try {
			info = new SimpleImageInfo(aImage.getFile());
		} catch (Exception e) {
			log.warn("Could not read image data from file [{}]", aImage.getFile().getAbsolutePath(), e);
		}

		if (info != null) {

			double sizeRatio = info.getWidth() / (double) info.getHeight();

			if (sizeRatio <= artworkMaxSizeRatio && sizeRatio >= artworkMinSizeRatio) {
				return true;
			}
		}

		return false;
	}

	private boolean isFolderArtwork(LibraryFolder aFolder) {

		String name = FilenameUtils.getBaseName(aFolder.getFile().getAbsolutePath()).toLowerCase();

		return artworkFolderNames.contains(name);
	}

	private LibraryImage fetchArtworkFromFolder(LibraryFolder aFolder) {

		List<LibraryImage> candidatesBySize = new ArrayList<>();

		List<LibraryImage> childImages = new ArrayList<>(aFolder.getChildImages());
		for (LibraryImage image : childImages) {
			if (isImageArtworkBySize(image)) {
				candidatesBySize.add(image);
			}
		}

		Collections.sort(candidatesBySize, new Comparator<LibraryImage>() {
			@Override
			public int compare(LibraryImage image1, LibraryImage image2) {
				return image1.getFile().getName().compareTo(image2.getFile().getName());
			}
		});

		for (LibraryImage image : candidatesBySize) {
			if (isImageArtworkByName(image)) {
				return image;
			}
		}

		if (candidatesBySize.size() > 0) {
			return childImages.get(0);
		}

		return null;
	}

	private Set<String> splitCommaSeparatedList(String aString) {

		Set<String> result = new HashSet<>();

		for (String item : aString.split(",")) {

			item = item.trim();

			if (item.length() > 0) {
				result.add(item.toLowerCase());
			}
		}

		return result;
	}

}
