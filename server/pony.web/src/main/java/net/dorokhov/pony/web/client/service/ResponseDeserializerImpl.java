package net.dorokhov.pony.web.client.service;

import com.github.nmorel.gwtjackson.client.exception.JsonMappingException;
import com.google.common.base.Strings;
import com.google.gwt.http.client.Response;
import com.google.inject.Inject;
import com.gwtplatform.dispatch.rest.client.ActionMetadataProvider;
import com.gwtplatform.dispatch.rest.client.RestResponseDeserializer;
import com.gwtplatform.dispatch.rest.client.serialization.Serialization;
import com.gwtplatform.dispatch.rest.shared.MetadataType;
import com.gwtplatform.dispatch.rest.shared.RestAction;
import com.gwtplatform.dispatch.shared.ActionException;

public class ResponseDeserializerImpl implements RestResponseDeserializer {

	private final ActionMetadataProvider metadataProvider;
	private final Serialization serialization;

	@Inject
	public ResponseDeserializerImpl(ActionMetadataProvider metadataProvider, Serialization serialization) {
		this.metadataProvider = metadataProvider;
		this.serialization = serialization;
	}

	@Override
	public <A extends RestAction<R>, R> R deserialize(A aAction, Response aResponse) throws ActionException {

		String resultType = (String) metadataProvider.getValue(aAction, MetadataType.RESPONSE_TYPE);

		if (!Strings.isNullOrEmpty(resultType) && serialization.canDeserialize(resultType)) {
			try {
				return serialization.deserialize(aResponse.getText(), resultType);
			} catch (JsonMappingException e) {
				throw new ActionException("Unable to deserialize response. An unexpected error occurred.", e);
			}
		}

		throw new ActionException("Unable to deserialize response. No serializer found.");
	}

}
