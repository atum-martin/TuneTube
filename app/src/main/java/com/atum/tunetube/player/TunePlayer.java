package com.atum.tunetube.player;

import android.media.MediaPlayer;
import android.net.Uri;
import android.widget.LinearLayout;

import com.atum.tunetube.MainActivity;
import com.atum.tunetube.R;
import com.atum.tunetube.model.PlayableItem;
import com.atum.tunetube.model.PlayerPlaylist;
import com.atum.tunetube.model.PlaylistItem;
import com.atum.tunetube.presentation.PlayerController;
import com.atum.tunetube.sql.DatabaseConnection;
import com.atum.tunetube.youtube.YoutubeLink;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by atum-martin on 23/05/2017.
 */

public class TunePlayer implements MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener, PlayTrackListener {

    private final MainActivity context;
    private static final String ENCODING = "UTF-8";
    private String url = null;
    private MediaPlayer player = null;
    private TunePlayerCompleted playerCompletedListener = null;
    private PlayerController controller = null;
    private PlayerPlaylist playlist = new PlayerPlaylist();

    public TunePlayer(MainActivity context){
        this.context = context;
    }

    public void setUrl(String url, String title) throws IOException {
        System.out.println("url of player: "+url);

        if(url.toLowerCase().startsWith("http"))
            url = "http://localhost:8093/?url="+ URLEncoder.encode(url, ENCODING)+"&title="+URLEncoder.encode(title, ENCODING);
        System.out.println("url of player: "+url);
        this.url = url;
        if(player == null) {
            player = createPlayer();
        } else {
            resetPlayer();
            player.setDataSource(context, Uri.parse(url));
            player.prepare();
            System.out.println("media player prepared");
            player.start();
            System.out.println("media player started");
        }
    }

    public void setTunePlayerCompletedListener(TunePlayerCompleted listener){
        this.playerCompletedListener = listener;
    }

    public PlayerController getMediaController(){
        return controller;
    }

    public void resetPlayer() {
        if(player != null)
            player.reset();
    }

    public MediaPlayer createPlayer(){
        MediaPlayer mPlayer = null;
        try {
            //mPlayer = MediaPlayer.create(this, Uri.parse(url));
            mPlayer = MediaPlayer.create(context, Uri.parse(url));
        } catch (Exception e){
            e.printStackTrace();
        }
        System.out.println("media player prepared");
        mPlayer.setOnCompletionListener(this);
        mPlayer.setOnErrorListener(this);
        controller = new PlayerController(mPlayer, context);
        mPlayer.setOnPreparedListener(controller);

        LinearLayout root = (LinearLayout) context.findViewById(R.id.container);

        //display controller element
        context.runOnUiThread(controller);
        //mPlayer.setVolume(0.1f, 0.1f);
        mPlayer.start();
        System.out.println("media player started");
        return mPlayer;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        System.out.println("attempting to play the next track.");
        String tmpUrl = this.url;
        playNextTrack();
        if (playerCompletedListener != null){
            playerCompletedListener.trackCompleted(tmpUrl);
        }
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        System.out.println("error with media player");
        return false;
    }

    @Override
    public void playTrack(PlayableItem link) {
        PlaylistItem item = (PlaylistItem) link;
        playlist.addFirst(item);
        playNextTrack();
    }
    public void playNextTrack(){
        PlayableItem item = playlist.poll();
        if(item != null)
            new PlayTrackAsync(this).execute(item);
    }

    public PlayerPlaylist getPlaylist(){
        return playlist;
    }

    public DatabaseConnection getDBConnection(){
        return context.getDBConnection();
    }

    public MediaPlayer getMediaPlayer() {
        return player;
    }

}
