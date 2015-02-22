package net.dorokhov.pony.web.client.service;

import net.dorokhov.pony.web.shared.SongDto;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PlayListImpl implements PlayList {

	private final List<Delegate> delegates = new ArrayList<>();

	private final List<SongDto> songList = new ArrayList<>();

	public PlayListImpl() {
		this(new ArrayList<SongDto>());
	}

	public PlayListImpl(List<SongDto> aSongList) {
		add(aSongList);
	}

	@Override
	public void addDelegate(Delegate aDelegate) {
		if (!delegates.contains(aDelegate)) {
			delegates.add(aDelegate);
		}
	}

	@Override
	public void removeDelegate(Delegate aDelegate) {
		delegates.remove(aDelegate);
	}

	@Override
	public void add(List<SongDto> aSongs) {
		for (SongDto song : aSongs) {
			add(song);
		}
	}

	@Override
	public void add(SongDto aSong) {
		add(aSong, songList.size());
	}

	@Override
	public void add(SongDto aSong, int aIndex) throws IndexOutOfBoundsException {

		songList.add(aIndex, aSong);

		for (Delegate nextDelegate : new ArrayList<>(delegates)) {
			nextDelegate.onPlayListSongAdded(this, aSong, aIndex);
		}
	}

	@Override
	public void remove(int aIndex) throws IndexOutOfBoundsException {

		SongDto song = songList.remove(aIndex);

		for (Delegate nextDelegate : new ArrayList<>(delegates)) {
			nextDelegate.onPlayListSongRemoved(this, song, aIndex);
		}
	}

	@Override
	public void removeAll() {
		while (songList.size() > 0) {
			remove(0);
		}
	}

	@Override
	public void move(int aOldIndex, int aNewIndex) throws IndexOutOfBoundsException {

		Collections.swap(songList, aOldIndex, aNewIndex);

		for (Delegate nextDelegate : new ArrayList<>(delegates)) {
			nextDelegate.onPlayListSongMoved(this, aOldIndex, aNewIndex);
		}
	}

	@Override
	public SongDto get(int aIndex) throws IndexOutOfBoundsException {
		return songList.get(aIndex);
	}

	@Override
	public int size() {
		return songList.size();
	}

}
