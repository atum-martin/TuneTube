package com.atum.tunetube;

import android.Manifest;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.SearchView;

import com.atum.tunetube.http.HttpProxy;
import com.atum.tunetube.player.TunePlayer;
import com.atum.tunetube.presentation.PlaylistAdapter;
import com.atum.tunetube.sql.DatabaseConnection;
import com.atum.tunetube.task.YoutubeTask;

import java.io.File;
import java.io.IOException;

public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    private PlaylistAdapter playListAdapter;
    private TunePlayer player;
    private SearchView searchMenuItem;
    private DatabaseConnection databaseConnection;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    YoutubeTask task = new YoutubeTask(YoutubeTask.Type.SEARCH, "Adele");
                    new YoutubeAsyncTask(MainActivity.this).execute(task);
                    return true;
                case R.id.navigation_dashboard:
                    YoutubeTask task2 = new YoutubeTask(YoutubeTask.Type.DATABASE_RECENT, databaseConnection);
                    new YoutubeAsyncTask(MainActivity.this).execute(task2);
                    return true;
                case R.id.navigation_notifications:
                    player.resetPlayer();
                    return true;
            }
            return false;
        }

    };

    public PlaylistAdapter getPlayListAdapter() {
        return playListAdapter;
    }

    public DatabaseConnection getDBConnection(){
        return databaseConnection;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        try {
            new HttpProxy();
        } catch (IOException e) {
            e.printStackTrace();
        }
        player = new TunePlayer(this);
        playListAdapter = new PlaylistAdapter(this, player);
        searchMenuItem = (SearchView) findViewById(R.id.musicsearch);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        searchMenuItem.setOnQueryTextListener(this);

        //new File(this.getCacheDir()+"/testdb1").delete();
        databaseConnection = new DatabaseConnection(getApplicationContext(), this.getCacheDir()+"/testdb1");
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        YoutubeTask task = new YoutubeTask(YoutubeTask.Type.SEARCH, query);
        new YoutubeAsyncTask(MainActivity.this).execute(task);
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }
}
