package net.dorokhov.pony.web.client.service;

import net.dorokhov.pony.web.client.service.api.ApiService;
import net.dorokhov.pony.web.client.service.api.MethodCallbackAdapter;
import net.dorokhov.pony.web.client.service.api.RequestAdapter;
import net.dorokhov.pony.web.client.service.common.OperationCallback;
import net.dorokhov.pony.web.client.service.common.OperationRequest;
import net.dorokhov.pony.web.shared.ArtistDto;
import net.dorokhov.pony.web.shared.ErrorDto;

import javax.inject.Inject;
import java.util.List;
import java.util.logging.Logger;

public class SongService {

	private final Logger log = Logger.getLogger(getClass().getName());

	private final ApiService apiService;

	@Inject
	public SongService(ApiService aApiService) {
		apiService = aApiService;
	}

	public OperationRequest getArtists(final OperationCallback<List<ArtistDto>> aCallback) {

		log.info("Getting artists...");
		
		return new RequestAdapter(apiService.getArtists(new MethodCallbackAdapter<>(new OperationCallback<List<ArtistDto>>() {
			@Override
			public void onSuccess(List<ArtistDto> aArtists) {
				
				log.info("[" + aArtists.size() + "] artists returned.");
				
				aCallback.onSuccess(aArtists);
			}

			@Override
			public void onError(List<ErrorDto> aErrors) {

				log.info("Could not get artists.");
				
				aCallback.onError(aErrors);
			}
		})));
	}

}
