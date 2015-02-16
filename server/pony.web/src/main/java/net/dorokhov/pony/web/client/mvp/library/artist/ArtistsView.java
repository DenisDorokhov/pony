package net.dorokhov.pony.web.client.mvp.library.artist;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Widget;
import com.gwtplatform.mvp.client.ViewImpl;
import net.dorokhov.pony.web.shared.ArtistDto;
import net.dorokhov.pony.web.shared.list.ArtistListDto;
import org.gwtbootstrap3.client.ui.LinkedGroup;
import org.gwtbootstrap3.client.ui.LinkedGroupItem;

public class ArtistsView extends ViewImpl implements ArtistsPresenter.MyView {

	interface MyUiBinder extends UiBinder<Widget, ArtistsView> {}

	private static final MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

	@UiField
	LinkedGroup artistList;

	private ArtistListDto artists;

	public ArtistsView() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	@Override
	public ArtistListDto getArtists() {
		return artists;
	}

	@Override
	public void setArtists(ArtistListDto aArtists) {

		artists = aArtists;

		updateArtists();
	}

	private void updateArtists() {

		artistList.clear();

		if (getArtists() != null) {
			for (ArtistDto artist : getArtists().getContent()) {

				ArtistListItemView artistView = new ArtistListItemView();

				artistView.setArtist(artist);

				LinkedGroupItem item = new LinkedGroupItem();

				item.add(artistView);

				artistList.add(item);
			}
		}
	}

}
