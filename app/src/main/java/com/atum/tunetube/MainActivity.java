package com.atum.tunetube;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.SearchView;

import com.atum.tunetube.http.HttpProxy;
import com.atum.tunetube.model.PlaylistItem;
import com.atum.tunetube.player.TunePlayer;
import com.atum.tunetube.presentation.PlaylistAdapter;
import com.atum.tunetube.sql.DatabaseConnection;
import com.atum.tunetube.task.YoutubeTask;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    private PlaylistAdapter playListAdapter;
    private TunePlayer player;
    private SearchView searchMenuItem;
    private DatabaseConnection databaseConnection;
    private List<PlaylistItem> playlists = new LinkedList<>();

    private void constructPlaylists(){
        YoutubeTask task2 = new YoutubeTask(YoutubeTask.Type.DATABASE_RECENT, databaseConnection);
        playlists.add(new PlaylistItem("Recently Played", task2));
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.playlists:
                    //YoutubeTask task = new YoutubeTask(YoutubeTask.Type.PLAYLIST, "https://www.youtube.com/channel/UC-9-kyTW8ZkZNDHQJ6FgpwQ");
                    //new YoutubeAsyncTask(MainActivity.this).execute(task);

                    /*playlists.add("Recent Searches");
                    playlists.add("EDM");
                    playlists.add("Pop Music");
                    playlists.add("Dance Music");*/
                    playListAdapter.displayPlaylists(playlists);
                    return true;
                case R.id.recently_played:
                    YoutubeTask task2 = new YoutubeTask(YoutubeTask.Type.DATABASE_RECENT, databaseConnection);
                    new YoutubeAsyncTask(MainActivity.this).execute(task2);
                    return true;
                case R.id.stop_playing:
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
