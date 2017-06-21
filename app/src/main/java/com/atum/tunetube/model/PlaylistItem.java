package com.atum.tunetube.model;

import com.atum.tunetube.task.YoutubeTask;

/**
 * Created by atum-martin on 21/06/2017.
 */

public class PlaylistItem {

    private String description;
    private YoutubeTask tsak;

    public PlaylistItem(String description, YoutubeTask tsak){
        this.description = description;
        this.tsak = tsak;

    }

    public String toString(){
        return description;
    }
}
