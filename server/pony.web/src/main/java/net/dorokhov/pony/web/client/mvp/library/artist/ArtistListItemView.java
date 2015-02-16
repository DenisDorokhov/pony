package net.dorokhov.pony.web.client.mvp.library.artist;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import net.dorokhov.pony.web.client.Messages;
import net.dorokhov.pony.web.shared.ArtistDto;

public class ArtistListItemView extends Composite {

	interface MyUiBinder extends UiBinder<Widget, ArtistListItemView> {}

	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

	@UiField
	Label artistNameLabel;

	private ArtistDto artist;

	public ArtistListItemView() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	public ArtistDto getArtist() {
		return artist;
	}

	public void setArtist(ArtistDto aArtist) {

		artist = aArtist;

		updateArtist();
	}

	private void updateArtist() {

		String nameValue = null;

		if (getArtist() != null) {

			nameValue = getArtist().getName();

			if (nameValue == null) {
				nameValue = Messages.INSTANCE.artistNameUnknown();
			}
		}

		artistNameLabel.setText(nameValue);
	}

}
