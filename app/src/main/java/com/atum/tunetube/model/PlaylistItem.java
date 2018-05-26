package com.atum.tunetube.model;

/**
 * Created by atum-martin on 21/06/2017.
 */

public abstract class PlaylistItem {

    private String description;

    public PlaylistItem(String description){
        this.description = description;
    }

    public String toString(){
        return description;
    }

}
