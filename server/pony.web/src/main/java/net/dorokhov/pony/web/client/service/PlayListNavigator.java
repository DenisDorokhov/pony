package net.dorokhov.pony.web.client.service;

import net.dorokhov.pony.web.shared.SongDto;

import java.util.List;

public interface PlayListNavigator {

	public PlayList getPlayList();

	public void setPlayList(PlayList aPlayList);

	public List<SongDto> getQueue();

	public SongDto getCurrent();

	public SongDto setCurrentIndex(int aIndex);

	public boolean hasPrevious();

	public boolean hasNext();

	public SongDto switchToPrevious();

	public SongDto switchToNext();

}
