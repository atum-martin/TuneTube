package com.atum.tunetube;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import com.atum.tunetube.player.TunePlayer;
import com.atum.tunetube.presentation.PlaylistAdapter;
import com.atum.tunetube.task.YoutubeTask;
import com.atum.tunetube.youtube.YoutubeLink;
import com.atum.tunetube.youtube.YoutubePlaylist;
import com.atum.tunetube.youtube.YoutubeSearch;
import com.github.axet.vget.vhs.YouTubeParser;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    private PlaylistAdapter playListAdapter;
    private TunePlayer player;
    private SearchView searchMenuItem;

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
                    player.resetPlayer();
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        player = new TunePlayer(this);
        playListAdapter = new PlaylistAdapter(this, player);
        searchMenuItem = (SearchView) findViewById(R.id.musicsearch);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        searchMenuItem.setOnQueryTextListener(this);
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
