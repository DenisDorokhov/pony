package net.dorokhov.pony.web.client.control;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;
import net.dorokhov.pony.web.client.util.ObjectUtils;

public class ImageLoader extends Composite {

	public enum State {
		EMPTY, PENDING, LOADING, ERROR, LOADED
	}

	interface MyUiBinder extends UiBinder<Widget, ImageLoader> {}

	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

	@UiField
	Image emptyImage;

	@UiField
	Image loadingImage;

	@UiField
	Image errorImage;

	@UiField
	Image loadedImage;

	private String url;

	private State loadingState;

	private Widget scroller;

	private HandlerRegistration scrollRegistration;
	private HandlerRegistration resizeRegistration;

	private Timer timer;

	public ImageLoader() {

		initWidget(uiBinder.createAndBindUi(this));

		setState(State.EMPTY);
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String aUrl) {
		if (aUrl != null) {
			if (getState() == State.ERROR || !ObjectUtils.nullSafeEquals(url, aUrl)) {

				url = aUrl;

				setState(State.PENDING);

				if (isAttached()) {
					lazyLoad();
				}
			}
		} else {
			clear();
		}
	}

	public void clear() {

		url = null;

		cancelTimer();

		setState(State.EMPTY);

		loadedImage.setUrl("");
	}

	public State getState() {
		return loadingState;
	}

	@Override
	protected void onAttach() {

		super.onAttach();

		resizeRegistration = Window.addResizeHandler(new ResizeHandler() {
			@Override
			public void onResize(ResizeEvent event) {
				lazyLoad();
			}
		});

		Widget currentParent = getParent();

		while (currentParent != null) {
			if (currentParent instanceof HasScrollHandlers) {

				scroller = currentParent;

				scrollRegistration = ((HasScrollHandlers) scroller).addScrollHandler(new ScrollHandler() {
					@Override
					public void onScroll(ScrollEvent aEvent) {
						lazyLoad();
					}
				});

				break;

			} else {
				currentParent = currentParent.getParent();
			}
		}

		lazyLoad();
	}

	@Override
	protected void onDetach() {

		if (scrollRegistration != null) {
			scrollRegistration.removeHandler();
		}
		if (resizeRegistration != null) {
			resizeRegistration.removeHandler();
		}

		scroller = null;
		scrollRegistration = null;
		resizeRegistration = null;

		cancelTimer();

		super.onDetach();
	}

	@UiHandler("loadedImage")
	void onImageLoaded(LoadEvent aEvent) {
		if (getState() == State.LOADING) {
			setState(State.LOADED);
		}
	}

	@UiHandler("loadedImage")
	void onImageError(ErrorEvent aEvent) {
		if (getState() == State.LOADING) {

			setState(State.ERROR);

			loadedImage.setUrl("");
		}
	}

	private void setState(State aLoadingState) {

		loadingState = aLoadingState;

		emptyImage.setVisible(getState() == State.EMPTY);
		loadingImage.setVisible(getState() == State.PENDING || getState() == State.LOADING);
		errorImage.setVisible(getState() == State.ERROR);
		loadedImage.setVisible(getState() == State.LOADED);
	}

	private void lazyLoad() {
		if (getState() == State.PENDING && timer == null) {
			timer = new Timer() {
				@Override
				public void run() {

					doLazyLoad();

					timer = null;
				}
			};
			timer.schedule(50);
		}
	}

	private void doLazyLoad() {

		boolean load = true;

		if (scroller != null) {
			load = (getAbsoluteTop() < scroller.getAbsoluteTop() + scroller.getOffsetHeight()) &&
					(getAbsoluteTop() + getOffsetHeight() > scroller.getAbsoluteTop());
		}

		if (load) {

			setState(State.LOADING);

			loadedImage.setUrl(getUrl());
		}
	}

	private void cancelTimer() {
		if (timer != null) {

			timer.cancel();

			timer = null;
		}
	}

}
