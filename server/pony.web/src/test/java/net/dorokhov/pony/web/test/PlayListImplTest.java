package net.dorokhov.pony.web.test;

import net.dorokhov.pony.web.client.service.PlayList;
import net.dorokhov.pony.web.client.service.PlayListImpl;
import net.dorokhov.pony.web.shared.SongDto;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class PlayListImplTest implements PlayList.Delegate {

	private PlayListImpl playList;

	private boolean didCallSongAdded;
	private boolean didCallSongRemoved;
	private boolean didCallSongMoved;

	@Test
	public void test() {

		playList = new PlayListImpl();

		playList.addDelegate(this);

		playList.add(buildSong(0));

		Assert.assertTrue(didCallSongAdded);

		playList.removeDelegate(this);

		didCallSongAdded = false;

		playList.add(buildSong(1));

		Assert.assertFalse(didCallSongAdded);

		playList.add(buildSong(2));
		playList.add(buildSong(1));
		playList.add(playList.get(0));

		playList.add(buildSong(3), 0);

		List<SongDto> songList = new ArrayList<SongDto>();

		songList.add(buildSong(4));
		songList.add(buildSong(5));

		playList.add(songList);

		Assert.assertEquals(Long.valueOf(3), playList.get(0).getId());
		Assert.assertEquals(Long.valueOf(0), playList.get(1).getId());
		Assert.assertEquals(Long.valueOf(1), playList.get(2).getId());
		Assert.assertEquals(Long.valueOf(2), playList.get(3).getId());
		Assert.assertEquals(Long.valueOf(1), playList.get(4).getId());
		Assert.assertEquals(Long.valueOf(0), playList.get(5).getId());
		Assert.assertEquals(Long.valueOf(4), playList.get(6).getId());
		Assert.assertEquals(Long.valueOf(5), playList.get(7).getId());

		Assert.assertEquals(8, playList.size());

		playList.addDelegate(this);

		playList.remove(2);

		Assert.assertTrue(didCallSongRemoved);

		playList.removeDelegate(this);

		didCallSongRemoved = false;

		playList.remove(0);

		Assert.assertFalse(didCallSongRemoved);

		playList.addDelegate(this);

		playList.move(2, 3);

		Assert.assertTrue(didCallSongMoved);

		playList.removeDelegate(this);

		didCallSongMoved = false;

		playList.move(0, 1);

		Assert.assertFalse(didCallSongMoved);

		Assert.assertEquals(Long.valueOf(2), playList.get(0).getId());
		Assert.assertEquals(Long.valueOf(0), playList.get(1).getId());
		Assert.assertEquals(Long.valueOf(0), playList.get(2).getId());
		Assert.assertEquals(Long.valueOf(1), playList.get(3).getId());
		Assert.assertEquals(Long.valueOf(4), playList.get(4).getId());
		Assert.assertEquals(Long.valueOf(5), playList.get(5).getId());

		playList.removeAll();

		Assert.assertEquals(0, playList.size());
	}

	@Override
	public void onPlayListSongAdded(PlayList aPlayList, SongDto aSong, int aIndex) {

		didCallSongAdded = true;

		Assert.assertTrue(playList == aPlayList);

		Assert.assertEquals(Long.valueOf(0), aSong.getId());
		Assert.assertEquals(0, aIndex);
	}

	@Override
	public void onPlayListSongRemoved(PlayList aPlayList, SongDto aSong, int aIndex) {

		didCallSongRemoved = true;

		Assert.assertTrue(playList == aPlayList);

		Assert.assertEquals(Long.valueOf(1), aSong.getId());
		Assert.assertEquals(2, aIndex);
	}

	@Override
	public void onPlayListSongMoved(PlayList aPlayList, int aOldIndex, int aNewIndex) {

		didCallSongMoved = true;

		Assert.assertTrue(playList == aPlayList);

		Assert.assertEquals(2, aOldIndex);
		Assert.assertEquals(3, aNewIndex);
	}

	private SongDto buildSong(int aIndex) {

		SongDto song = new SongDto();

		song.setId((long) aIndex);

		return song;
	}

}
