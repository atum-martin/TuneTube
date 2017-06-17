package com.atum.tunetube.sql;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

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
        upgradeDB();
    }

    private void upgradeDB() {
        Cursor resultSet = connection.rawQuery("PRAGMA user_version", null);
        int dbVersion = -1;
        if(resultSet.moveToNext()){
            dbVersion = resultSet.getInt(0);
            System.out.println("db version: "+resultSet.getInt(0));
        }
        while(true){
            switch (dbVersion){
                case 0:
                    System.out.println("applying schema update 0");
                    connection.execSQL("ALTER TABLE tracks_played ADD COLUMN play_count INT");
                    break;
                default:
                    return;
            }
            dbVersion++;
            connection.execSQL("PRAGMA user_version = "+dbVersion);
        }
    }

    private void createTables() {
        connection.execSQL("CREATE TABLE IF NOT EXISTS tracks_played(youtubeTitle VARCHAR,youtubeUrl VARCHAR PRIMARY KEY, last_played LONG);");
        //connection.execSQL("INSERT INTO tracks_played VALUES('Tobu - Infectious [NCS Release]','/watch?v=ux8-EbW6DUI','"+System.currentTimeMillis()+"');");
        //connection.execSQL("INSERT INTO tracks_played VALUES('Tobu - Infectious 22','/watch?v=ux8-EbW6DUI23','"+(System.currentTimeMillis()-20000)+"');");

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
        String updateQuery = "UPDATE tracks_played SET last_played = "+System.currentTimeMillis()+" WHERE youtubeUrl = '"+track.getVideoId()+"';";
        SQLiteStatement statement = connection.compileStatement(updateQuery);
        int affectedRows = statement.executeUpdateDelete();
        if(affectedRows <= 0){
            String[] args = new String[]{track.getYoutubeTitle(), track.getVideoId(), new Long(System.currentTimeMillis()).toString()};
            connection.execSQL("INSERT INTO tracks_played VALUES(?, ?, ?);", args);
        }
    }
}
