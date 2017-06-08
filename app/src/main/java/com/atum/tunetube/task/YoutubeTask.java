package com.atum.tunetube.task;

import com.atum.tunetube.youtube.YoutubeLink;
import com.atum.tunetube.youtube.YoutubePlaylist;
import com.atum.tunetube.youtube.YoutubeSearch;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by atum-martin on 07/06/2017.
 */

public class YoutubeTask {

    public List<YoutubeLink> execute() {
        List<YoutubeLink> tracks;
        switch(action){
            case SEARCH:
                tracks = YoutubeSearch.getSearchResults(query);
                return tracks;
            case PLAYLIST:
                tracks = YoutubePlaylist.parsePlaylist(query);
                return tracks;
            default:
                break;
        }
        return new LinkedList<>();
    }

    public enum Type {
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
