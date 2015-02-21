package net.dorokhov.pony.web.client.control;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.UriUtils;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;
import net.dorokhov.pony.web.client.common.LoadingState;
import net.dorokhov.pony.web.client.service.SecurityStorage;
import net.dorokhov.pony.web.client.util.ObjectUtils;

public class ImageLoader extends Composite {

	interface MyUiBinder extends UiBinder<Widget, ImageLoader> {}

	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

	private final SecurityStorage securityStorage;

	@UiField
	Image loadingImage;

	@UiField
	Image errorImage;

	@UiField
	Image loadedImage;

	private String url;

	private LoadingState loadingState;

	private JavaScriptObject currentRequest;

	public ImageLoader() {

		securityStorage = SecurityStorage.INSTANCE;

		initWidget(uiBinder.createAndBindUi(this));

		setLoadingState(LoadingState.LOADING);
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String aUrl) {
		if (getLoadingState() == LoadingState.ERROR || !ObjectUtils.nullSafeEquals(url, aUrl)) {

			url = UriUtils.fromString(aUrl).asString();

			load();
		}
	}

	public void setResource(ImageResource aResource) {

		if (currentRequest != null) {

			cancelRequest(currentRequest);

			currentRequest = null;
		}

		loadedImage.setResource(aResource);

		url = loadedImage.getUrl();

		setLoadingState(LoadingState.LOADED);
	}

	public LoadingState getLoadingState() {
		return loadingState;
	}

	private void setLoadingState(LoadingState aLoadingState) {

		loadingState = aLoadingState;

		updateLoadingState();
	}

	private void updateLoadingState() {
		loadingImage.setVisible(getLoadingState() == LoadingState.LOADING);
		errorImage.setVisible(getLoadingState() == LoadingState.ERROR);
		loadedImage.setVisible(getLoadingState() == LoadingState.LOADED);
	}

	private void load() {

		setLoadingState(LoadingState.LOADING);

		if (currentRequest != null) {
			cancelRequest(currentRequest);
		}

		currentRequest = sendRequest(getUrl(), securityStorage.getAccessToken());
	}

	private native JavaScriptObject sendRequest(String aUrl, String aAccessToken) /*-{

        var xhr;

        if ($wnd.XMLHttpRequest) {
            xhr = new $wnd.XMLHttpRequest();
        } else {
            try {
                xhr = new $wnd.ActiveXObject("MSXML2.XMLHTTP.3.0");
            } catch (e) {
                xhr = new $wnd.ActiveXObject("Microsoft.XMLHTTP");
            }
        }

        xhr.open('GET', aUrl, true);

        xhr.responseType = "blob";

		if (aAccessToken != null) {
        	xhr.setRequestHeader("X-Access-Token", aAccessToken);
		}

		var self = this;

		xhr.onload = function(aEvent) {
            if (xhr.status < 400) {

				var source = $wnd.URL.createObjectURL(xhr.response);

            	self.@net.dorokhov.pony.web.client.control.ImageLoader::complete(Ljava/lang/String;)(source);

            } else {
                self.@net.dorokhov.pony.web.client.control.ImageLoader::fail()();
			}
		};
		xhr.onerror = function(aEvent) {
            self.@net.dorokhov.pony.web.client.control.ImageLoader::fail()();
		};

		xhr.send();

        return xhr;
    }-*/;

	private native void cancelRequest(JavaScriptObject aXhr) /*-{
		aXhr.abort();
	}-*/;

	private void complete(String aSource) {

		loadedImage.addLoadHandler(new LoadHandler() {
			@Override
			public void onLoad(LoadEvent aEvent) {
				setLoadingState(LoadingState.LOADED);
			}
		});

		loadedImage.setUrl(aSource);

		currentRequest = null;
	}

	private void fail() {

		setLoadingState(LoadingState.ERROR);

		currentRequest = null;
	}

}
