package net.dorokhov.pony.web.client.mvp.common;

public enum LoadingState {

	EMPTY, LOADING, LOADED, ERROR;

	public boolean isEmptyOrLoaded() {
		return this == EMPTY || this == LOADED;
	}

}
