package net.dorokhov.pony.web.client.mvp.library;

import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.PresenterWidget;
import com.gwtplatform.mvp.client.View;
import net.dorokhov.pony.web.client.mvp.library.album.AlbumListPresenter;
import net.dorokhov.pony.web.client.mvp.library.artist.ArtistListPresenter;

import javax.inject.Inject;

public class LibraryContentPresenter extends PresenterWidget<LibraryContentPresenter.MyView> {

	public interface MyView extends View {}

	public static final Object SLOT_ARTISTS = new Object();
	public static final Object SLOT_ALBUMS = new Object();

	private final ArtistListPresenter artistListPresenter;

	private final AlbumListPresenter albumListPresenter;

	@Inject
	public LibraryContentPresenter(EventBus aEventBus, MyView aView,
								   ArtistListPresenter aArtistListPresenter, AlbumListPresenter aAlbumListPresenter) {

		super(aEventBus, aView);

		artistListPresenter = aArtistListPresenter;
		albumListPresenter = aAlbumListPresenter;
	}

	@Override
	protected void onBind() {

		super.onBind();

		setInSlot(SLOT_ARTISTS, artistListPresenter);
		setInSlot(SLOT_ALBUMS, albumListPresenter);
	}

}
