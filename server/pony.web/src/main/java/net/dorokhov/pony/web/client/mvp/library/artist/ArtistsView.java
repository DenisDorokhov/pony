package net.dorokhov.pony.web.client.mvp.library.artist;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Widget;
import com.gwtplatform.mvp.client.ViewImpl;
import net.dorokhov.pony.web.shared.ArtistDto;
import org.gwtbootstrap3.client.ui.LinkedGroup;
import org.gwtbootstrap3.client.ui.LinkedGroupItem;

import java.util.ArrayList;
import java.util.List;

public class ArtistsView extends ViewImpl implements ArtistsPresenter.MyView {

	interface MyUiBinder extends UiBinder<Widget, ArtistsView> {}

	private static final MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

	@UiField
	LinkedGroup artistList;

	private List<ArtistDto> artists;

	public ArtistsView() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	@Override
	public List<ArtistDto> getArtists() {
		
		if (artists == null) {
			artists = new ArrayList<>();
		}
		
		return artists;
	}

	@Override
	public void setArtists(List<ArtistDto> aArtists) {

		artists = aArtists;

		updateArtists();
	}

	private void updateArtists() {

		artistList.clear();

		for (ArtistDto artist : getArtists()) {

			ArtistListItemView artistView = new ArtistListItemView();

			artistView.setArtist(artist);

			LinkedGroupItem item = new LinkedGroupItem();

			item.add(artistView);

			artistList.add(item);
		}
	}

}
