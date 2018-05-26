package com.atum.tunetube.sql;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.atum.tunetube.R;
import com.atum.tunetube.model.PlaylistItem;
import com.atum.tunetube.util.IndexDiskFiles;
import com.atum.tunetube.task.YoutubeTask;
import com.atum.tunetube.util.FileUtils;
import com.atum.tunetube.youtube.YoutubeLink;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by atum-martin on 13/06/17.
 */

public class DatabaseConnection {

    private Context ctx;
    private SQLiteDatabase connection;
    private static DatabaseConnection instance = null;

    public DatabaseConnection(Context ctx, String name){
        this.ctx = ctx;
        instance = this;
        File databaseFile = new File(name);
        //databaseFile.delete();
        boolean dbExists = databaseFile.exists();

        connection = SQLiteDatabase.openOrCreateDatabase(name, null);

        InputStream databaseUpgradeFile = ctx.getResources().openRawResource(R.raw.databaseupdates);
        new UpgradeDatabase(connection, databaseUpgradeFile);

        //If the DB doesn't exist index files previously created by the application.
        if(!dbExists){
            File songDir = new File(FileUtils.getWorkingDirectory());
            IndexDiskFiles indexer = new IndexDiskFiles(this);
            indexer.indexDirectory(songDir);
        }
    }

    public static DatabaseConnection getInstance(){
        return instance;
    }

    public void submitSearch(String query, List<PlaylistItem> tracks) {

        String json =  new Gson().toJson(tracks);
        String[] args = new String[]{query, json, Long.valueOf(System.currentTimeMillis()).toString()};
        connection.execSQL("INSERT INTO searches VALUES(?, ?, ?);", args);
    }

    public List<PlaylistItem> getRecentSearches(){
        LinkedList<PlaylistItem> output = new LinkedList<>();
        Cursor resultSet = connection.rawQuery("Select * from searches ORDER BY search_time DESC",null);
        while(resultSet.moveToNext()){
            String query = resultSet.getString(0);

            System.out.println("row in db: "+query);
            YoutubeTask task = new YoutubeTask(query, YoutubeTask.Type.SEARCH_RESULTS, this, query);
            output.add(task);
        }
        resultSet.close();
        return output;
    }

    public List<YoutubeLink> getSearchResults(String query) {
        Cursor resultSet = connection.rawQuery("Select * from searches WHERE query = '"+query+"' ORDER BY search_time DESC LIMIT 1",null);
        if(resultSet.moveToNext()){
            String results = resultSet.getString(1);
            Type listType = new TypeToken<ArrayList<YoutubeLink>>(){}.getType();
            List<YoutubeLink> output = new Gson().fromJson(results, listType);
            resultSet.close();
            return output;
        }
        resultSet.close();
        return new LinkedList<>();
    }

    public List<PlaylistItem> getRecentlyPlayed(){
        LinkedList<PlaylistItem> output = new LinkedList<>();
        Cursor resultSet = connection.rawQuery("Select * from tracks_played ORDER BY last_played DESC",null);
        while(resultSet.moveToNext()){
            String youtubeTitle = resultSet.getString(0);
            String youtubeUrl = resultSet.getString(1);
            long lastPlayed = resultSet.getLong(2);
            YoutubeLink link = new YoutubeLink(youtubeUrl, youtubeTitle);
            output.add(link);
            System.out.println("recentlyPlayed: "+link.getYoutubeTitle());
        }
        resultSet.close();
        return output;
    }

    public List<PlaylistItem> getRecentlyRecommended(){
        LinkedList<PlaylistItem> output = new LinkedList<>();
        Cursor resultSet = connection.rawQuery("Select * from tracks_played ORDER BY last_played DESC",null);
        while(resultSet.moveToNext()){

            //play_count column 3
            String json = resultSet.getString(4);
            YoutubeLink link = new Gson().fromJson(json, YoutubeLink.class);
            if(link != null && link.getRelatedItems() != null) {
                output.addAll(link.getRelatedItems());
            }
        }
        resultSet.close();
        return output;
    }

    public void updatePlaytime(YoutubeLink track){
        String updateQuery = "UPDATE tracks_played SET last_played = "+System.currentTimeMillis()+", play_count=play_count+1 WHERE youtubeUrl = '"+track.getVideoId()+"';";
        SQLiteStatement statement = connection.compileStatement(updateQuery);
        int affectedRows = statement.executeUpdateDelete();
        if(affectedRows <= 0){
            String json =  new Gson().toJson(track);
            String[] args = new String[]{track.getYoutubeTitle(), track.getVideoId(),  Long.valueOf(System.currentTimeMillis()).toString(), "1", json};
            connection.execSQL("INSERT INTO tracks_played VALUES(?, ?, ?, ?, ?);", args);
        }
    }
}
