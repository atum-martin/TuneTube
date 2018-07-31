package com.atum.tunetube.model;

import android.util.Log;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Log.class})
public class PlaylistTest {

    @Before
    public void setup(){
        PowerMockito.mockStatic(Log.class);
    }

    class TestPlaylistItem extends PlaylistItem implements PlayableItem {

        private String des;
        public TestPlaylistItem(String description) {
            super(description);
            this.des = description;
        }

        @Override
        public String getUrl() {
            return des;
        }

        @Override
        public String getTitle() {
            return des;
        }

        @Override
        public boolean equals(Object o){
            if(o instanceof PlayableItem){
                return ((PlayableItem) o).getTitle().equals(this.getTitle());
            }
            return false;
        }
    }

    class TestInvalidPlaylistItem extends PlaylistItem {

        public TestInvalidPlaylistItem(String description) {
            super(description);
        }
    }

    @Test
    public void test1(){
        PlayerPlaylist playlist = new PlayerPlaylist();
        Assert.assertTrue(playlist.isEmpty());
    }
    @Test
    public void test2(){
        PlayerPlaylist playlist = new PlayerPlaylist();
        playlist.add(new TestPlaylistItem("test1"));
        playlist.addFirst(new TestPlaylistItem("test2"));
        playlist.add(new TestPlaylistItem("test3"));
        Assert.assertEquals("test2", playlist.poll().getTitle());
        Assert.assertEquals("test1", playlist.poll().getTitle());
        Assert.assertEquals("test3", playlist.poll().getTitle());

    }
    @Test
    public void test3(){
        PlayerPlaylist playlist = new PlayerPlaylist();
        playlist.add(new TestInvalidPlaylistItem(""));
        playlist.addFirst(new TestInvalidPlaylistItem(""));
        playlist.add(new TestPlaylistItem("test1"));
        playlist.add(new TestPlaylistItem("test2"));
        playlist.add(new TestPlaylistItem("test3"));
        Assert.assertEquals("test1", playlist.poll().getTitle());
        Assert.assertEquals("test2", playlist.poll().getTitle());
        Assert.assertEquals("test3", playlist.poll().getTitle());
    }
    @Test
    public void test4(){
        PlayerPlaylist playlist = new PlayerPlaylist();
        playlist.add(new TestPlaylistItem("test1"));
        playlist.addFirst(new TestPlaylistItem("test2"));
        TestPlaylistItem lastItem = new TestPlaylistItem("test3");
        playlist.add(lastItem);
        Assert.assertEquals(3, playlist.getCurrentPlaylist().size());
        playlist.poll();
        Assert.assertEquals(2, playlist.getCurrentPlaylist().size());
        playlist.remove(lastItem);
        Assert.assertEquals(1, playlist.getCurrentPlaylist().size());
        Assert.assertEquals("test1", playlist.poll().getTitle());
    }
    @Test
    public void test5(){
        PlayerPlaylist playlist = new PlayerPlaylist();
        playlist.add(new TestPlaylistItem("test1"));
        playlist.add(new TestPlaylistItem("test2"));
        playlist.add(new TestPlaylistItem("test3"));
        Assert.assertEquals(3, playlist.getCurrentPlaylist().size());

        ArrayList<PlaylistItem> inputList = new ArrayList<>();
        inputList.add(new TestPlaylistItem("test4"));
        inputList.add(new TestPlaylistItem("test5"));
        playlist.clearAndPopulate(inputList);
        Assert.assertEquals(2, playlist.getCurrentPlaylist().size());
        Assert.assertEquals("test4", playlist.poll().getTitle());
        Assert.assertEquals("test5", playlist.poll().getTitle());
    }

    @Test
    public void test6(){
        PlayerPlaylist playlist = new PlayerPlaylist();
        Assert.assertNull(playlist.poll());
    }

}
