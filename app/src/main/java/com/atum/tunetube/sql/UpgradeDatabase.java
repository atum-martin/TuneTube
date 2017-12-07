package com.atum.tunetube.sql;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.SparseArray;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by mchapman on 07/12/17.
 */

public class UpgradeDatabase {

    private SQLiteDatabase connection;

    public UpgradeDatabase(SQLiteDatabase connection, InputStream upgradeFile){
        this.connection = connection;
        createTables();
        upgradeDB(upgradeFile);
    }

    private class Upgrade {
        private int id;
        private List<String> sqlCommands;

        Upgrade(int id, List<String> sqlCommands){
            this.id = id;
            this.sqlCommands = sqlCommands;
            System.out.println("DB Upgrade: id: "+id+" commands: "+sqlCommands.size());
        }

        List<String> getSqlCommands() {
            return sqlCommands;
        }
    }

    private void createTables() {
        connection.execSQL("CREATE TABLE IF NOT EXISTS tracks_played(youtubeTitle VARCHAR,youtubeUrl VARCHAR PRIMARY KEY, last_played LONG);");
        connection.execSQL("CREATE TABLE IF NOT EXISTS searches (query VARCHAR,results VARCHAR, search_time LONG);");
    }

    private SparseArray<Upgrade> readUpgradeMap(InputStream in){
        SparseArray<Upgrade> upgradeMap = new SparseArray<>();
        List<String> sqlCommands = null;

        BufferedReader br = new BufferedReader(new InputStreamReader(in));
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
        return upgradeMap;
    }

    private int readDBVersion(){
        Cursor resultSet = connection.rawQuery("PRAGMA user_version", null);
        int dbVersion = -1;
        if(resultSet.moveToNext()){
            dbVersion = resultSet.getInt(0);
            System.out.println("db version: "+resultSet.getInt(0));
        }
        resultSet.close();
        return dbVersion;
    }

    private void upgradeDB(InputStream upgradeFile) {
        int dbVersion = readDBVersion();
        SparseArray<Upgrade> upgradeMap = readUpgradeMap(upgradeFile);

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

}
