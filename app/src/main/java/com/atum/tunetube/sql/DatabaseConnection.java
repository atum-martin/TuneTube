package com.atum.tunetube.sql;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.atum.tunetube.youtube.YoutubeLink;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by atum-martin on 13/06/17.
 */

public class DatabaseConnection {

    private SQLiteDatabase connection;

    public DatabaseConnection(String name){
        connection = SQLiteDatabase.openOrCreateDatabase(name, null);
        createTables();
        updatePlaytime(null);
    }

    private void createTables() {
        connection.execSQL("CREATE TABLE IF NOT EXISTS tracks_played(youtubeTitle VARCHAR,youtubeUrl VARCHAR PRIMARY KEY, last_played LONG);");
        connection.execSQL("INSERT INTO tracks_played VALUES('Tobu - Infectious [NCS Release]','/watch?v=ux8-EbW6DUI','"+System.currentTimeMillis()+"');");
        connection.execSQL("INSERT INTO tracks_played VALUES('Tobu - Infectious 22','/watch?v=ux8-EbW6DUI23','"+(System.currentTimeMillis()-20000)+"');");

    }

    public List<YoutubeLink> getRecentlyPlayed(){
        LinkedList<YoutubeLink> output = new LinkedList<>();
        Cursor resultSet = connection.rawQuery("Select * from tracks_played ORDER BY last_played DESC",null);
        while(resultSet.moveToNext()){
            String youtubeTitle = resultSet.getString(0);
            String youtubeUrl = resultSet.getString(1);
            long lastPlayed = resultSet.getLong(2);
            YoutubeLink link = new YoutubeLink(youtubeUrl, youtubeTitle);
            output.add(link);
            System.out.println("recentlyPlayed: "+link.getYoutubeTitle());
        }
        return output;
    }

    public void updatePlaytime(YoutubeLink track){
        String[] args = new String[]{new String(""+System.currentTimeMillis()), "/watch?v=ux8-EbW6DUI"};
        //Cursor resultSet = connection.rawQuery("INSERT INTO tracks_played VALUES(?, ?, ?);", args);
        Cursor resultSet = connection.rawQuery("UPDATE tracks_played SET last_played = ? WHERE youtubeUrl = ?;", args);
        if(resultSet.get){
            System.out.println("update successful.");
        } else {

            args = new String[]{"Tobu - Infectious [NCS Release]", "/watch?v=ux8-EbW6DUI", new String(""+System.currentTimeMillis())};
            connection.execSQL("INSERT INTO tracks_played VALUES(?, ?, ?);", args);
            System.out.println("update not successful.");
        }
    }
}
