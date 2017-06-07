package com.atum.tunetube.task;

/**
 * Created by atum-martin on 07/06/2017.
 */

public class YoutubeTask {

    private enum Type {
        SEARCH,
        PLAYLIST,
        VIDEO;
    }

    private Type action;
    private String query;

    public YoutubeTask(Type action, String query){
        this.action = action;
        this.query = query;
    }
}
