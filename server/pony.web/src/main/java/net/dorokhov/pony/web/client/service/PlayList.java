package net.dorokhov.pony.web.client.service;

import net.dorokhov.pony.web.shared.SongDto;

import java.util.List;

public interface PlayList {

	public static interface Delegate {

		public void onPlayListSongAdded(PlayList aPlayList, SongDto aSong, int aIndex);

		public void onPlayListSongRemoved(PlayList aPlayList, SongDto aSong, int aIndex);

		public void onPlayListSongMoved(PlayList aPlayList, int aOldIndex, int aNewIndex);

	}

	public void addDelegate(Delegate aDelegate);

	public void removeDelegate(Delegate aDelegate);

	public void add(List<SongDto> aSongs);

	public void add(SongDto aSong);

	public void add(SongDto aSong, int aIndex) throws IndexOutOfBoundsException;

	public void remove(int aIndex) throws IndexOutOfBoundsException;

	public void removeAll();

	public void move(int aOldIndex, int aNewIndex) throws IndexOutOfBoundsException;

	public SongDto get(int aIndex) throws IndexOutOfBoundsException;

	public int size();

}
