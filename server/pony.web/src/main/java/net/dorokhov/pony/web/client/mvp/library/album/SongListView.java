package net.dorokhov.pony.web.client.mvp.library.album;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SetSelectionModel;
import net.dorokhov.pony.web.client.event.SongSelectionRequestEvent;
import net.dorokhov.pony.web.client.event.SongStartRequestEvent;
import net.dorokhov.pony.web.shared.SongDto;
import org.gwtbootstrap3.client.ui.Heading;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SongListView extends Composite implements SelectionChangeEvent.Handler,
		SongSelectionRequestEvent.HasHandler, SongSelectionRequestEvent.Handler,
		SongStartRequestEvent.HasHandler, SongStartRequestEvent.Handler {

	interface MyUiBinder extends UiBinder<Widget, SongListView> {}

	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

	private static final List<SongView> viewCache = new ArrayList<>();

	static {
		for (int i = 0; i < 20; i++) {
			viewCache.add(new SongView());
		}
	}

	private final Map<Long, SongView> songToView = new HashMap<>();

	private final Map<SongView, HandlerRegistration> songViewToSelectionRegistration = new HashMap<>();
	private final Map<SongView, HandlerRegistration> songViewToActivationRegistration = new HashMap<>();

	private final HandlerManager handlerManager = new HandlerManager(this);

	@UiField
	Heading captionHeader;

	@UiField
	FlowPanel songList;

	private List<SongDto> songs;

	private String caption;

	private boolean playing;

	private SetSelectionModel<SongDto> selectionModel;
	private SetSelectionModel<SongDto> activationModel;

	private HandlerRegistration selectionRegistration;
	private HandlerRegistration activationRegistration;

	public SongListView() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	public List<SongDto> getSongs() {

		if (songs == null) {
			songs = new ArrayList<>();
		}

		return songs;
	}

	public void setSongs(List<SongDto> aSongs) {

		songs = aSongs;

		updateSongs();
	}

	public String getCaption() {
		return caption;
	}

	public void setCaption(String aCaption) {

		caption = aCaption;

		updateCaption();
	}

	public boolean isPlaying() {
		return playing;
	}

	public void setPlaying(boolean aPlaying) {

		playing = aPlaying;

		updateSongViews();
	}

	public SetSelectionModel<SongDto> getSelectionModel() {
		return selectionModel;
	}

	public void setSelectionModel(SetSelectionModel<SongDto> aSelectionModel) {

		if (selectionRegistration != null) {
			selectionRegistration.removeHandler();
			selectionRegistration = null;
		}

		selectionModel = aSelectionModel;

		if (selectionModel != null) {
			selectionRegistration = selectionModel.addSelectionChangeHandler(this);
		}

		updateSongViews();
	}

	public SetSelectionModel<SongDto> getActivationModel() {
		return activationModel;
	}

	public void setActivationModel(SetSelectionModel<SongDto> aActivationModel) {

		if (activationRegistration != null) {
			activationRegistration.removeHandler();
			activationRegistration = null;
		}

		activationModel = aActivationModel;

		if (activationModel != null) {
			activationRegistration = activationModel.addSelectionChangeHandler(this);
		}

		updateSongViews();
	}

	public void scrollToSong(SongDto aSong) {

		final SongView view = songToView.get(aSong.getId());

		if (view != null) {
			Scheduler.get().scheduleFinally(new Command() {
				@Override
				public void execute() {
					view.getElement().scrollIntoView();
				}
			});
		}
	}

	@Override
	public void onSelectionChange(SelectionChangeEvent aEvent) {
		updateSongViews();
	}

	@Override
	public HandlerRegistration addSongSelectionRequestHandler(SongSelectionRequestEvent.Handler aHandler) {
		return handlerManager.addHandler(SongSelectionRequestEvent.TYPE, aHandler);
	}

	@Override
	public void onSongSelectionRequest(SongSelectionRequestEvent aEvent) {
		handlerManager.fireEvent(aEvent);
	}

	@Override
	public HandlerRegistration addSongStartRequestHandler(SongStartRequestEvent.Handler aHandler) {
		return handlerManager.addHandler(SongStartRequestEvent.TYPE, aHandler);
	}

	@Override
	public void onSongStartRequest(SongStartRequestEvent aEvent) {
		handlerManager.fireEvent(aEvent);
	}

	private void updateSongs() {

		while (songList.getWidgetCount() > getSongs().size()) {

			int i = songList.getWidgetCount() - 1;

			SongView songView = (SongView) songList.getWidget(i);

			songList.remove(i);

			songView.setSong(null);

			songViewToSelectionRegistration.get(songView).removeHandler();
			songViewToSelectionRegistration.remove(songView);

			songViewToActivationRegistration.get(songView).removeHandler();
			songViewToActivationRegistration.remove(songView);

			viewCache.add(songView);
		}

		songToView.clear();

		for (int i = 0; i < getSongs().size(); i++) {

			SongDto song = getSongs().get(i);

			SongView songView;

			if (i < songList.getWidgetCount()) {
				songView = (SongView) songList.getWidget(i);
			} else {

				songView = viewCache.size() > 0 ? viewCache.remove(0) : null;

				if (songView == null) {
					songView = new SongView();
				}

				songViewToSelectionRegistration.put(songView, songView.addSongSelectionRequestHandler(this));
				songViewToActivationRegistration.put(songView, songView.addSongStartRequestHandler(this));

				songList.add(songView);
			}

			songView.setSong(song);

			songToView.put(song.getId(), songView);
		}

		updateSongViews();
	}

	private void updateCaption() {
		captionHeader.setText(caption != null ? caption : "");
	}

	private void updateSongViews() {
		for (Map.Entry<Long, SongView> entry : songToView.entrySet()) {

			SongDto song = entry.getValue().getSong();

			if (getSelectionModel() != null) {
				entry.getValue().setSelected(getSelectionModel().isSelected(song));
			}
			if (getActivationModel() != null) {
				entry.getValue().setActivated(getActivationModel().isSelected(song));
			}

			entry.getValue().setPlaying(isPlaying());
		}
	}

}
