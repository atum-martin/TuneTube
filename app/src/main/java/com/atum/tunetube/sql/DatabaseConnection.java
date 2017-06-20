package com.atum.tunetube.sql;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.atum.tunetube.R;
import com.atum.tunetube.youtube.YoutubeLink;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by atum-martin on 13/06/17.
 */

public class DatabaseConnection {

    private final Context ctx;
    private SQLiteDatabase connection;

    public DatabaseConnection(Context ctx, String name){
        this.ctx = ctx;
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

        Map<Integer, Upgrade> upgradeMap = new HashMap<>();
        List<String> sqlCommands = null;
        InputStream ins = ctx.getResources().openRawResource(R.raw.databaseupdates);
        BufferedReader br = new BufferedReader(new InputStreamReader(ins));
        String line;
        try {
            int id = -1;

            int lineNumber = 0;
            while((line = br.readLine()) != null){
                lineNumber++;
                line = line.trim();
                if(line.startsWith("#")){
                    //comments ignore
                    continue;
                }
                else if(line.startsWith("id")){
                    if(id != -1){
                        upgradeMap.put(id, new Upgrade(id, sqlCommands));
                    }
                    sqlCommands = new LinkedList<>();
                    id = Integer.parseInt(line.substring(3));
                } else if(line.length() > 0){
                    if(sqlCommands == null){
                        throw new IOException("SQL command detected prior to database ID lineNo: "+lineNumber);
                    }
                    sqlCommands.add(line);
                }
            }
            if(id != -1)
                upgradeMap.put(id, new Upgrade(id, sqlCommands));
        } catch (IOException e) {
            e.printStackTrace();
        }

        while(true){
            Upgrade up = upgradeMap.get(dbVersion);
            if(up == null)
                break;
            System.out.println("applying schema update "+dbVersion);
            for(String sql : up.getSqlCommands()){
                connection.execSQL(sql);
            }
            dbVersion++;
            connection.execSQL("PRAGMA user_version = "+dbVersion);
        }
    }

    private class Upgrade {
        private int id;
        private List<String> sqlCommands;

        public Upgrade(int id, List<String> sqlCommands){
            this.id = id;
            this.sqlCommands = sqlCommands;
            System.out.println("DB Upgrade: id: "+id+" commands: "+sqlCommands.size());
        }

        public List<String> getSqlCommands() {
            return sqlCommands;
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
