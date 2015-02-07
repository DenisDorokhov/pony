package com.gwtplatform.dispatch.rest.client.gin;

import com.gwtplatform.common.client.CommonGinModule;
import com.gwtplatform.dispatch.rest.client.*;
import com.gwtplatform.dispatch.rest.client.serialization.MultimapJsonSerializer;
import com.gwtplatform.dispatch.rest.client.serialization.Serialization;
import com.gwtplatform.dispatch.rest.shared.RestDispatch;
import net.dorokhov.pony.web.client.service.ResponseDeserializerImpl;

import javax.inject.Singleton;

public class RestDispatchAsyncModuleCustom extends RestDispatchAsyncModule {

	public static class Builder extends RestDispatchAsyncModuleBuilder {
		@Override
		public RestDispatchAsyncModule build() {
			return new RestDispatchAsyncModuleCustom(this);
		}
	}

	private final RestDispatchAsyncModuleBuilder builder;
	private final MultimapJsonSerializer multimapJsonSerializer = new MultimapJsonSerializer();

	public RestDispatchAsyncModuleCustom() {
		this(new RestDispatchAsyncModuleCustom.Builder());
	}

	public RestDispatchAsyncModuleCustom(RestDispatchAsyncModuleBuilder builder) {

		super(builder);

		this.builder = builder;
	}

	@Override
	protected void configureDispatch() {

		install(new CommonGinModule());

		bindConstant().annotatedWith(XsrfHeaderName.class).to(builder.getXsrfTokenHeaderName());
		bindConstant().annotatedWith(RequestTimeout.class).to(builder.getRequestTimeoutMs());
		bindConstant().annotatedWith(DefaultDateFormat.class).to(builder.getDefaultDateFormat());
		bindConstant().annotatedWith(GlobalHeaderParams.class).to(multimapJsonSerializer.serialize(builder.getGlobalHeaderParams()));
		bindConstant().annotatedWith(GlobalQueryParams.class).to(multimapJsonSerializer.serialize(builder.getGlobalQueryParams()));

		bind(RestDispatchCallFactory.class).to(DefaultRestDispatchCallFactory.class).in(Singleton.class);
		bind(RestRequestBuilderFactory.class).to(DefaultRestRequestBuilderFactory.class).in(Singleton.class);
		bind(RestResponseDeserializer.class).to(ResponseDeserializerImpl.class).in(Singleton.class);

		bind(Serialization.class).to(builder.getSerializationClass()).in(Singleton.class);

		bind(RestDispatch.class).to(RestDispatchAsync.class).in(Singleton.class);
	}

}
