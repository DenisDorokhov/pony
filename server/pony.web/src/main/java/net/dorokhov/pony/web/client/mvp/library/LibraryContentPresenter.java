package net.dorokhov.pony.web.client.mvp.library;

import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.PresenterWidget;
import com.gwtplatform.mvp.client.View;
import net.dorokhov.pony.web.client.mvp.library.album.AlbumsPresenter;
import net.dorokhov.pony.web.client.mvp.library.artist.ArtistsPresenter;

import javax.inject.Inject;

public class LibraryContentPresenter extends PresenterWidget<LibraryContentPresenter.MyView> {

	public interface MyView extends View {}

	public static final Object SLOT_ARTISTS = new Object();
	public static final Object SLOT_ALBUMS = new Object();

	private final ArtistsPresenter artistsPresenter;

	private final AlbumsPresenter albumsPresenter;

	@Inject
	public LibraryContentPresenter(EventBus aEventBus, MyView aView,
								   ArtistsPresenter aArtistsPresenter, AlbumsPresenter aAlbumsPresenter) {

		super(aEventBus, aView);

		artistsPresenter = aArtistsPresenter;
		albumsPresenter = aAlbumsPresenter;
	}

	@Override
	protected void onBind() {

		super.onBind();

		setInSlot(SLOT_ARTISTS, artistsPresenter);
		setInSlot(SLOT_ALBUMS, albumsPresenter);
	}

}
