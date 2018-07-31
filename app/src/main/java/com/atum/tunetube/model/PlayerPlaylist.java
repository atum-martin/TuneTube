package com.atum.tunetube.model;

import android.util.Log;

import com.atum.tunetube.Constants;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by atum_martin on 26/05/2018.
 */

public class PlayerPlaylist {
    private LinkedList<PlaylistItem> items = new LinkedList<>();

    public PlayableItem poll(){
        PlaylistItem item = items.poll();
        if(item == null){
            return null;
        }
        return (PlayableItem) item;
    }

    public boolean isEmpty(){
        return items.size() == 0;
    }

    public boolean add(PlaylistItem addItem){
        if(!(addItem instanceof PlayableItem)){
            return false;
        }
        boolean existed = remove(addItem);
        items.add(addItem);
        return existed;
    }

    public boolean addFirst(PlaylistItem addItem){
        if(!(addItem instanceof PlayableItem)){
            return false;
        }
        boolean existed = remove(addItem);
        items.add(0, addItem);
        return existed;
    }

    public boolean remove(PlaylistItem addItem){
        int index = items.indexOf(addItem);
        if(index == -1) return false;
        items.remove(index);
        return true;
    }

    public void clearAndPopulate(Collection<PlaylistItem> newItems){
        items.clear();
        //items.addAll(newItems);
        for (PlaylistItem item : newItems){
            if(item instanceof PlayableItem) {
                items.add(item);
                Log.i(Constants.TAG,"constructed playlist: " + item.toString());
            }
        }
    }

    public List<PlaylistItem> getCurrentPlaylist() {
        List<PlaylistItem> currentPlaylist = new ArrayList<>(items);
        return currentPlaylist;
    }
}
