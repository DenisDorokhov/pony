package net.dorokhov.pony.web.client.mvp;

import com.google.gwt.user.client.Window;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyStandard;
import com.gwtplatform.mvp.client.proxy.ProxyPlace;
import net.dorokhov.pony.web.client.Messages;
import net.dorokhov.pony.web.client.PlaceTokens;

import javax.inject.Inject;

public class LibraryPresenter extends Presenter<LibraryPresenter.MyView, LibraryPresenter.MyProxy> {

	@ProxyStandard
	@NameToken(PlaceTokens.LIBRARY)
	public interface MyProxy extends ProxyPlace<LibraryPresenter> {}

	public interface MyView extends View {}

	public static final Object SLOT_PLAYER = new Object();
	public static final Object SLOT_TOOLBAR = new Object();
	public static final Object SLOT_CONTENT = new Object();

	private final PlayerPresenter playerPresenter;

	private final ToolbarPresenter toolbarPresenter;

	private final LibraryContentPresenter contentPresenter;

	@Inject
	public LibraryPresenter(EventBus aEventBus, MyView aView, MyProxy aProxy,
							PlayerPresenter aPlayerPresenter,
							ToolbarPresenter aToolbarPresenter,
							LibraryContentPresenter aLibraryContentPresenter) {

		super(aEventBus, aView, aProxy, RevealType.Root);

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
	}

	@Override
	protected void onReveal() {

		super.onReveal();

		Window.setTitle(Messages.INSTANCE.libraryTitle());
	}

}
