package com.atum.tunetube;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;

import com.atum.tunetube.http.HttpProxy;
import com.atum.tunetube.model.PlayerPlaylist;
import com.atum.tunetube.model.PlaylistItem;
import com.atum.tunetube.player.TunePlayer;
import com.atum.tunetube.presentation.PlaylistAdapter;
import com.atum.tunetube.sql.DatabaseConnection;
import com.atum.tunetube.task.YoutubeAsyncTask;
import com.atum.tunetube.task.YoutubeTask;
import com.atum.tunetube.util.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class MainActivity extends AppCompatActivity {


    private TunePlayer player;

    private DatabaseConnection databaseConnection;
    private List<PlaylistItem> playlists = new LinkedList<>();
    private LocalBroadcastManager bManager;
    private Intent serviceIntent;
    private static MainActivity instance;
    private PlaylistFragment playlistFragment;

    public static MainActivity getInstance() {
        return instance;
    }

    private void constructPlaylists(){
        YoutubeTask recentTask = new YoutubeTask("Recently Played", YoutubeTask.Type.DATABASE_RECENT, this);
        playlists.add(recentTask);

        YoutubeTask task = new YoutubeTask("Recently Searched", YoutubeTask.Type.SEARCHES_RECENT, this);
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

        //display the recently played playlist
        new YoutubeAsyncTask(MainActivity.this).execute(recentTask);
    }

    public static final String PLAYER_ACTION = "com.atum.tunetube.player";
    public static final String PLAYER_ACTION_NEXT_TRACK = "com.atum.tunetube.player.next_track";
    public static final String PLAYER_ACTION_PLAY = "com.atum.tunetube.player.play";
    public static final String PLAYER_ACTION_PAUSE = "com.atum.tunetube.player.pause";
    public static final String UPDATE_TEXT_ACTION = "com.atum.tunetube.player.update";
    private static final String PLAYLIST_FRAGMENT_TAG = "PLAYLIST_FRAGMENT_TAG";

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
                    displayPlaylist(playlists);
                    return true;
                case R.id.recently_played:
                    YoutubeTask task2 = new YoutubeTask("Recent", YoutubeTask.Type.DATABASE_RECENT, MainActivity.this);
                    new YoutubeAsyncTask(MainActivity.this).execute(task2);
                    return true;
                case R.id.stop_playing:
                    //player.resetPlayer();
                    Intent intent = new Intent(MainActivity.this, AndroidMediaPlayerActivity.class);
                    startActivity(intent);
                    return true;
                case R.id.settings:
                    //intent = new Intent(MainActivity.this, SettingsActivity.class);
                    //startActivity(intent);
                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                    ft.replace(R.id.main_activity_fragment, new SettingsActivity.GeneralPreferenceFragment());
                    ft.commit();
                    return true;
            }
            return false;
        }

    };

    public void displayPlaylist(List<PlaylistItem> playlists) {

        Fragment playlistFrag = getSupportFragmentManager().findFragmentById(R.id.main_activity_fragment);
        if (playlistFrag != null && playlistFrag instanceof PlaylistFragment) {
            playlistFragment.getPlayListAdapter().displayPlaylist(playlists);
            Log.i(Constants.TAG, "fragment already visible updating playlist.");
        } else {
            playlistFragment.setPlaylist(playlists);
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.main_activity_fragment, playlistFragment, PLAYLIST_FRAGMENT_TAG);
            ft.commit();
            Log.i(Constants.TAG, "fragment not visible.");
        }
    }


    public DatabaseConnection getDBConnection(){
        return databaseConnection;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance = this;
        setContentView(R.layout.activity_main);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        try {
            new HttpProxy();
        } catch (IOException e) {
            e.printStackTrace();
        }


        bManager = LocalBroadcastManager.getInstance(this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(PLAYER_ACTION);
        bManager.registerReceiver(bReceiver, intentFilter);

        databaseConnection = new DatabaseConnection(getResources().openRawResource(R.raw.databaseupdates), new File(this.getCacheDir()+"/testdb1"));
        FileUtils.init(this);

        player = new TunePlayer(this);
        constructPlaylists();
        playlistFragment = new PlaylistFragment();
        playlistFragment.setPlayer(player);
        playlistFragment.setPlaylist(playlists);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.main_activity_fragment, playlistFragment, PLAYLIST_FRAGMENT_TAG);
        ft.commit();


        startService();



    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        player.resetPlayer();
        bManager.unregisterReceiver(bReceiver);
        stopService(serviceIntent);
    }

    public void startService() {
        serviceIntent = new Intent(MainActivity.this, NotificationService.class);
        serviceIntent.setAction(NotificationService.ACTION.STARTFOREGROUND_ACTION);
        startService(serviceIntent);
    }

/*
    @Override
    protected void onStop() {
        super.onStop();

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

    public PlaylistAdapter getPlayListAdapter() { return playlistFragment.getPlayListAdapter(); }

    public TunePlayer getPlayer() {
        return player;
    }

    public void updateNotificationText(String text){
        Intent updateNotificationIntent = new Intent(this, MainActivity.class);
        updateNotificationIntent.setAction(MainActivity.PLAYER_ACTION);
        updateNotificationIntent.putExtra("action", UPDATE_TEXT_ACTION);
        updateNotificationIntent.putExtra("text", text);
        LocalBroadcastManager.getInstance(this).sendBroadcast(updateNotificationIntent);
    }

}
