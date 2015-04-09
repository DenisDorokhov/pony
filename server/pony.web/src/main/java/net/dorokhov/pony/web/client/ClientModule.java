package net.dorokhov.pony.web.client;

import com.gwtplatform.mvp.client.annotations.DefaultPlace;
import com.gwtplatform.mvp.client.annotations.ErrorPlace;
import com.gwtplatform.mvp.client.annotations.UnauthorizedPlace;
import com.gwtplatform.mvp.client.gin.AbstractPresenterModule;
import com.gwtplatform.mvp.client.gin.DefaultModule;
import net.dorokhov.pony.web.client.mvp.*;
import net.dorokhov.pony.web.client.mvp.library.*;
import net.dorokhov.pony.web.client.mvp.library.album.AlbumListPresenter;
import net.dorokhov.pony.web.client.mvp.library.album.AlbumListView;
import net.dorokhov.pony.web.client.mvp.library.artist.ArtistListPresenter;
import net.dorokhov.pony.web.client.mvp.library.artist.ArtistListView;
import net.dorokhov.pony.web.client.service.*;
import net.dorokhov.pony.web.client.service.api.ApiService;

public class ClientModule extends AbstractPresenterModule {

	@Override
	protected void configure() {

		install(new DefaultModule());

		bindPresenter(LoginPresenter.class, LoginPresenter.MyView.class, LoginView.class, LoginPresenter.MyProxy.class);

		bindPresenter(LibraryPresenter.class, LibraryPresenter.MyView.class, LibraryView.class, LibraryPresenter.MyProxy.class);

		bindSingletonPresenterWidget(PlayerPresenter.class, PlayerPresenter.MyView.class, PlayerView.class);
		bindSingletonPresenterWidget(ToolbarPresenter.class, ToolbarPresenter.MyView.class, ToolbarView.class);
		bindSingletonPresenterWidget(LibraryContentPresenter.class, LibraryContentPresenter.MyView.class, LibraryContentView.class);
		bindSingletonPresenterWidget(ArtistListPresenter.class, ArtistListPresenter.MyView.class, ArtistListView.class);
		bindSingletonPresenterWidget(AlbumListPresenter.class, AlbumListPresenter.MyView.class, AlbumListView.class);
		bindSingletonPresenterWidget(ScanningPresenter.class, ScanningPresenter.MyView.class, ScanningView.class);
		bindSingletonPresenterWidget(LogPresenter.class, LogPresenter.MyView.class, LogView.class);
		bindSingletonPresenterWidget(UserListPresenter.class, UserListPresenter.MyView.class, UserListView.class);

		bindPresenter(ErrorPresenter.class, ErrorPresenter.MyView.class, ErrorView.class, ErrorPresenter.MyProxy.class);

		bindConstant().annotatedWith(DefaultPlace.class).to(PlaceTokens.LIBRARY);
		bindConstant().annotatedWith(ErrorPlace.class).to(PlaceTokens.ERROR);
		bindConstant().annotatedWith(UnauthorizedPlace.class).to(PlaceTokens.LOGIN);

		bind(ApiService.class).asEagerSingleton();

		bind(ErrorNotifier.class).to(ErrorNotifierImpl.class).asEagerSingleton();
		bind(BusyModeManager.class).asEagerSingleton();

		bind(SecurityStorage.class).to(SecurityStorageImpl.class).asEagerSingleton();

		bind(AuthenticationManager.class).asEagerSingleton();
		bind(AuthenticationListener.class).asEagerSingleton();
		bind(AuthenticationDispatcherFilter.class).asEagerSingleton();

		bind(SongService.class).asEagerSingleton();

		bind(TitleManager.class).asEagerSingleton();

		bind(ScanJobService.class).asEagerSingleton();
		bind(LibraryScanner.class).asEagerSingleton();
		bind(LibraryScannerListener.class).asEagerSingleton();

		bind(LinkBuilder.class).asEagerSingleton();
	}

}
