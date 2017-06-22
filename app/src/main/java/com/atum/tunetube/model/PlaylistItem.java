package com.atum.tunetube.model;

import com.atum.tunetube.task.YoutubeTask;
import com.atum.tunetube.youtube.YoutubeLink;

/**
 * Created by atum-martin on 21/06/2017.
 */

public class PlaylistItem extends YoutubeLink {

    private String description;
    private YoutubeTask task;

    public PlaylistItem(String description, YoutubeTask task){
        super(null, null);
        this.description = description;
        this.task = task;

    }

    public String toString(){
        return description;
    }

    public YoutubeTask getTask(){
        return task;
    }
}
