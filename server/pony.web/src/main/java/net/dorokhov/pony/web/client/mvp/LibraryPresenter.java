package net.dorokhov.pony.web.client.mvp;

import com.google.gwt.user.client.Window;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyStandard;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.client.proxy.ProxyPlace;
import com.gwtplatform.mvp.shared.proxy.PlaceRequest;
import net.dorokhov.pony.web.client.LibraryParams;
import net.dorokhov.pony.web.client.event.ArtistSelectionDoneEvent;
import net.dorokhov.pony.web.client.event.ArtistSelectionRequestedEvent;
import net.dorokhov.pony.web.client.message.Messages;
import net.dorokhov.pony.web.client.PlaceTokens;
import net.dorokhov.pony.web.client.mvp.library.LibraryContentPresenter;
import net.dorokhov.pony.web.client.mvp.library.PlayerPresenter;
import net.dorokhov.pony.web.client.mvp.library.ToolbarPresenter;
import net.dorokhov.pony.web.client.util.ObjectUtils;
import net.dorokhov.pony.web.client.util.StringUtils;
import net.dorokhov.pony.web.shared.ArtistDto;

import javax.inject.Inject;

public class LibraryPresenter extends Presenter<LibraryPresenter.MyView, LibraryPresenter.MyProxy> implements ArtistSelectionDoneEvent.Handler {

	@ProxyStandard
	@NameToken(PlaceTokens.LIBRARY)
	public interface MyProxy extends ProxyPlace<LibraryPresenter> {}

	public interface MyView extends View {}

	public static final Object SLOT_PLAYER = new Object();
	public static final Object SLOT_TOOLBAR = new Object();
	public static final Object SLOT_CONTENT = new Object();
	
	private final PlaceManager placeManager;

	private final PlayerPresenter playerPresenter;

	private final ToolbarPresenter toolbarPresenter;

	private final LibraryContentPresenter contentPresenter;

	@Inject
	public LibraryPresenter(EventBus aEventBus, MyView aView, MyProxy aProxy, PlaceManager aPlaceManager,
							PlayerPresenter aPlayerPresenter,
							ToolbarPresenter aToolbarPresenter,
							LibraryContentPresenter aLibraryContentPresenter) {

		super(aEventBus, aView, aProxy, RevealType.Root);
		
		placeManager = aPlaceManager;

		playerPresenter = aPlayerPresenter;
		toolbarPresenter = aToolbarPresenter;
		contentPresenter = aLibraryContentPresenter;
	}

	@Override
	protected void onBind() {

		super.onBind();

		setInSlot(SLOT_PLAYER, playerPresenter);
		setInSlot(SLOT_TOOLBAR, toolbarPresenter);
		setInSlot(SLOT_CONTENT, contentPresenter);
		
		addRegisteredHandler(ArtistSelectionDoneEvent.TYPE, this);
	}

	@Override
	protected void onReveal() {

		super.onReveal();

		Window.setTitle(Messages.INSTANCE.libraryTitle());
	}

	@Override
	public void prepareFromRequest(PlaceRequest aRequest) {

		super.prepareFromRequest(aRequest);
		
		getEventBus().fireEvent(new ArtistSelectionRequestedEvent(aRequest.getParameter(LibraryParams.ARTIST, null)));
	}

	@Override
	public void onArtistSelectionDone(ArtistSelectionDoneEvent aEvent) {
		goToArtist(aEvent.getArtist());
	}

	private void goToArtist(ArtistDto aArtist) {

		String artistName = aArtist != null ? aArtist.getName() : null;
		String artistId = aArtist != null ? ObjectUtils.nullSafeToString(aArtist.getId()) : null;

		PlaceRequest currentPlaceRequest = placeManager.getCurrentPlaceRequest();

		boolean nameTokenChanged = !StringUtils.nullSafeNormalizedEquals(currentPlaceRequest.getNameToken(), PlaceTokens.LIBRARY);

		boolean artistChanged = !StringUtils.nullSafeNormalizedEquals(currentPlaceRequest.getParameter(LibraryParams.ARTIST, null), artistName) &&
				!StringUtils.nullSafeNormalizedEquals(currentPlaceRequest.getParameter(LibraryParams.ARTIST, null), artistId);

		if (nameTokenChanged || artistChanged) {

			PlaceRequest.Builder builder = new PlaceRequest.Builder().nameToken(PlaceTokens.LIBRARY);

			if (artistName != null) {
				builder.with(LibraryParams.ARTIST, artistName);
			}

			placeManager.revealPlace(builder.build());
		}
	}
	
}
