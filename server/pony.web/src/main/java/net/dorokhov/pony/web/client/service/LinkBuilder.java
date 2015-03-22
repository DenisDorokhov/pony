package net.dorokhov.pony.web.client.service;

import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.shared.proxy.PlaceRequest;
import net.dorokhov.pony.web.client.LibraryParams;
import net.dorokhov.pony.web.client.PlaceTokens;
import net.dorokhov.pony.web.client.util.ObjectUtils;
import net.dorokhov.pony.web.shared.ArtistDto;

import javax.inject.Inject;

public class LinkBuilder {

	private final PlaceManager placeManager;

	@Inject
	public LinkBuilder(PlaceManager aPlaceManager) {
		placeManager = aPlaceManager;
	}

	public String buildLinkToArtist(ArtistDto aArtist) {
		return placeManager.buildHistoryToken(buildRequestToArtist(aArtist));
	}

	public PlaceRequest buildRequestToArtist(ArtistDto aArtist) {

		String artistName = aArtist != null ? aArtist.getName() : null;
		String artistId = aArtist != null ? ObjectUtils.nullSafeToString(aArtist.getId()) : null;

		PlaceRequest.Builder builder = new PlaceRequest.Builder().nameToken(PlaceTokens.LIBRARY);

		if (artistName != null && artistName.matches("[\\u0000-\\u00FF]*")) { // use artist name only if it's non-unicode
			builder.with(LibraryParams.ARTIST, artistName);
		} else {
			builder.with(LibraryParams.ARTIST, artistId);
		}

		return builder.build();
	}

}
