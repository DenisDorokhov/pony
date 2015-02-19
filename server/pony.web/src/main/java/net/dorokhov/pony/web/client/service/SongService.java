package net.dorokhov.pony.web.client.service;

import net.dorokhov.pony.web.client.service.api.ApiService;
import net.dorokhov.pony.web.client.service.api.MethodCallbackAdapter;
import net.dorokhov.pony.web.client.service.api.RequestAdapter;
import net.dorokhov.pony.web.client.service.common.OperationCallback;
import net.dorokhov.pony.web.client.service.common.OperationRequest;
import net.dorokhov.pony.web.shared.ArtistDto;

import javax.inject.Inject;
import java.util.List;

public class SongService {

	private final ApiService apiService;

	@Inject
	public SongService(ApiService aApiService) {
		apiService = aApiService;
	}

	public OperationRequest getArtists(OperationCallback<List<ArtistDto>> aCallback) {
		return new RequestAdapter(apiService.getArtists(new MethodCallbackAdapter<>(aCallback)));
	}

}
