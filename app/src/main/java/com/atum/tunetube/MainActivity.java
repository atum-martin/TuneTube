package com.atum.tunetube;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.provider.DocumentFile;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.SearchView;

import com.atum.tunetube.http.HttpProxy;
import com.atum.tunetube.model.PlayerPlaylist;
import com.atum.tunetube.model.PlaylistItem;
import com.atum.tunetube.player.TunePlayer;
import com.atum.tunetube.presentation.PlaylistAdapter;
import com.atum.tunetube.sql.DatabaseConnection;
import com.atum.tunetube.task.YoutubeAsyncTask;
import com.atum.tunetube.task.YoutubeTask;
import com.atum.tunetube.util.FileUtils;
import com.atum.tunetube.youtube.YoutubeLink;

import java.io.IOException;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    private PlaylistAdapter playListAdapter;
    private TunePlayer player;
    private SearchView searchMenuItem;
    private DatabaseConnection databaseConnection;
    private List<PlaylistItem> playlists = new LinkedList<>();
    private LocalBroadcastManager bManager;
    private Intent serviceIntent;
    private static Context instance;

    public static Context getInstance() {
        return instance;
    }

    private void constructPlaylists(){
        YoutubeTask task = new YoutubeTask("Recently Played", YoutubeTask.Type.DATABASE_RECENT, this);
        playlists.add(task);

        task = new YoutubeTask("Recently Searched", YoutubeTask.Type.SEARCHES_RECENT, this);
        playlists.add(task);

        task = new YoutubeTask("Recommended Tracks", YoutubeTask.Type.RECOMMENED_RECENT, this);
        playlists.add(task);

        task = new YoutubeTask("EDM", YoutubeTask.Type.PLAYLIST, "https://www.youtube.com/channel/UCCIPrrom6DIftcrInjeMvsQ/videos");
        playlists.add(task);

        task = new YoutubeTask("Pop Music", YoutubeTask.Type.PLAYLIST, "https://www.youtube.com/channel/UCE80FOXpJydkkMo-BYoJdEg/videos");
        playlists.add(task);

        task = new YoutubeTask("Rock Music", YoutubeTask.Type.PLAYLIST, "https://www.youtube.com/channel/UCRZoK7sezr5KRjk7BBjmH6w/videos");
        playlists.add(task);

        task = new YoutubeTask("Current Playlist", YoutubeTask.Type.CURRENT_PLAYLIST, this);
        playlists.add(task);

    }

    public static final String PLAYER_ACTION = "com.atum.tunetube.player";
    public static final String PLAYER_ACTION_NEXT_TRACK = "com.atum.tunetube.player.next_track";
    public static final String PLAYER_ACTION_PLAY = "com.atum.tunetube.player.play";
    public static final String PLAYER_ACTION_PAUSE = "com.atum.tunetube.player.pause";
    public static final String UPDATE_TEXT_ACTION = "com.atum.tunetube.player.update";

    private BroadcastReceiver bReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(PLAYER_ACTION)) {
                String playerAction = intent.getStringExtra("action");
                if(playerAction == null){
                    return;
                }
                switch(playerAction){
                    case PLAYER_ACTION_NEXT_TRACK:
                        player.playNextTrack();
                        break;
                    case PLAYER_ACTION_PLAY:
                        if(player.getMediaPlayer() != null && !player.getMediaPlayer().isPlaying()){
                            player.getMediaPlayer().start();
                        }
                        break;
                    case PLAYER_ACTION_PAUSE:
                        if(player.getMediaPlayer() != null)
                             player.getMediaPlayer().pause();
                        break;
                }
            }
        }
    };


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.playlists:
                    playListAdapter.displayPlaylist(playlists);
                    return true;
                case R.id.recently_played:
                    YoutubeTask task2 = new YoutubeTask("Recent", YoutubeTask.Type.DATABASE_RECENT, MainActivity.this);
                    new YoutubeAsyncTask(MainActivity.this).execute(task2);
                    return true;
                case R.id.stop_playing:
                    player.resetPlayer();
                    return true;
                case R.id.settings:
                    Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                    startActivity(intent);
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
        instance = this;
        setContentView(R.layout.activity_main);

        try {
            new HttpProxy();
        } catch (IOException e) {
            e.printStackTrace();
        }
        player = new TunePlayer(this);
        playListAdapter = new PlaylistAdapter(this, player);

        bManager = LocalBroadcastManager.getInstance(this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(PLAYER_ACTION);
        bManager.registerReceiver(bReceiver, intentFilter);

        searchMenuItem = (SearchView) findViewById(R.id.musicsearch);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        searchMenuItem.setOnQueryTextListener(this);

        databaseConnection = new DatabaseConnection(getApplicationContext(), this.getCacheDir()+"/testdb1");
        FileUtils.init(this);
        constructPlaylists();
        startService();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        bManager.unregisterReceiver(bReceiver);
        stopService(serviceIntent);
    }

    public void startService() {
        serviceIntent = new Intent(MainActivity.this, NotificationService.class);
        serviceIntent.setAction(NotificationService.ACTION.STARTFOREGROUND_ACTION);
        startService(serviceIntent);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        searchMenuItem.clearFocus();
        YoutubeTask task = new YoutubeTask("Search", YoutubeTask.Type.SEARCH, this, query);
        new YoutubeAsyncTask(MainActivity.this).execute(task);
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    /*@Override
    protected void onStop() {
        super.onStop();
        mediaController.hide();
        player.stop();
        player.release();
    }*/

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //the MediaController will hide after 3 seconds - tap the screen to make it appear again
        if(player.getMediaController() != null)
            player.getMediaController().show();
        return false;
    }

    public PlayerPlaylist getPlaylistManager(){
        return player.getPlaylist();
    }

    public void updateNotificationText(String text){
        Intent updateNotificationIntent = new Intent(this, MainActivity.class);
        updateNotificationIntent.setAction(MainActivity.PLAYER_ACTION);
        updateNotificationIntent.putExtra("action", UPDATE_TEXT_ACTION);
        updateNotificationIntent.putExtra("text", text);
        LocalBroadcastManager.getInstance(this).sendBroadcast(updateNotificationIntent);
    }

}
