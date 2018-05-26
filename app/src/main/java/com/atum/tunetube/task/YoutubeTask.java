package com.atum.tunetube.task;

import com.atum.tunetube.model.PlaylistItem;
import com.atum.tunetube.sql.DatabaseConnection;
import com.atum.tunetube.youtube.YoutubeLink;
import com.atum.tunetube.youtube.YoutubePlaylist;
import com.atum.tunetube.youtube.YoutubeSearch;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by atum-martin on 07/06/2017.
 */

public class YoutubeTask extends PlaylistItem {

    public List<PlaylistItem> execute() {
        List<PlaylistItem> tracks;
        switch(action){
            case SEARCH:
                tracks = YoutubeSearch.getSearchResults(query);
                connection.submitSearch(query, tracks);
                return tracks;
            case PLAYLIST:
                tracks = YoutubePlaylist.parsePlaylist(query);
                return tracks;
            case DATABASE_RECENT:
                tracks = connection.getRecentlyPlayed();
                return tracks;
            case RECOMMENED_RECENT:
                tracks = connection.getRecentlyRecommended();
                return tracks;
            case SEARCHES_RECENT:
                tracks = connection.getRecentSearches();
                return tracks;
            case SEARCH_RESULTS:
                tracks = connection.getSearchResults(query);
                return tracks;
            default:
                break;
        }
        return new LinkedList<>();
    }

    public enum Type {
        SEARCH,
        PLAYLIST,
        DATABASE_RECENT,
        VIDEO,
        SEARCHES_RECENT,
        SEARCH_RESULTS,
        RECOMMENED_RECENT
    }

    private Type action;
    private String query;
    DatabaseConnection connection;

    public YoutubeTask(String decription, Type action, String query){
        this(decription, action, null, query);
    }

    public YoutubeTask(String decription, Type action, DatabaseConnection connection, String query){
        super(decription);
        this.action = action;
        this.query = query;
        this.connection = connection;
    }

    public YoutubeTask(String decription, Type action, DatabaseConnection connection){
        this(decription, action, connection, null);
    }
}
