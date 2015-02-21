package net.dorokhov.pony.web.client.mvp.library.artist;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import net.dorokhov.pony.web.client.control.ImageLoader;
import net.dorokhov.pony.web.client.resource.Images;
import net.dorokhov.pony.web.client.resource.Messages;
import net.dorokhov.pony.web.shared.ArtistDto;
import org.gwtbootstrap3.client.ui.LinkedGroupItem;

public class ArtistView extends Composite implements HasClickHandlers {

	interface MyUiBinder extends UiBinder<LinkedGroupItem, ArtistView> {}

	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

	@UiField
	ImageLoader artworkImage;

	@UiField
	Label nameLabel;

	private final LinkedGroupItem linkedGroupItem;

	private ArtistDto artist;

	public ArtistView() {

		linkedGroupItem = uiBinder.createAndBindUi(this);

		initWidget(linkedGroupItem);
	}

	public ArtistDto getArtist() {
		return artist;
	}

	public void setArtist(ArtistDto aArtist) {

		artist = aArtist;

		updateArtist();
	}

	public boolean isActive() {
		return linkedGroupItem.isActive();
	}

	public void setActive(boolean aActive) {
		linkedGroupItem.setActive(aActive);
	}

	@Override
	public HandlerRegistration addClickHandler(ClickHandler aHandler) {
		return linkedGroupItem.addClickHandler(aHandler);
	}

	private void updateArtist() {

		String nameValue = null;
		String artworkValue = null;

		if (getArtist() != null) {

			nameValue = getArtist().getName();

			if (nameValue == null) {
				nameValue = Messages.INSTANCE.artistNameUnknown();
			}

			artworkValue = getArtist().getArtworkUrl();
		}

		nameLabel.setText(nameValue);

		if (artworkValue != null) {
			artworkImage.setUrl(artworkValue);
		} else {
			artworkImage.setResource(Images.INSTANCE.unknown());
		}
	}

}
