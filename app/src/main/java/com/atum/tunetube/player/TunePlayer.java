package com.atum.tunetube.player;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;

import com.atum.tunetube.MainActivity;
import com.atum.tunetube.sql.DatabaseConnection;
import com.atum.tunetube.youtube.YoutubeLink;

import java.io.IOException;
import java.net.URLEncoder;

/**
 * Created by atum-martin on 23/05/2017.
 */

public class TunePlayer implements MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener, PlayTrackListener {

    private final MainActivity context;
    private String url = null;
    private String nextUrl = null;
    private String nextTitle = null;
    private MediaPlayer player = null;
    private TunePlayerCompleted playerCompletedListener = null;

    public TunePlayer(MainActivity context){
        this.context = context;
    }

    public void setUrl(String url, String title) throws IOException {
        System.out.println("url of player: "+url);
        url = "http://localhost:8093/?url="+ URLEncoder.encode(url)+"&title="+URLEncoder.encode(title);
        System.out.println("url of player: "+url);
        this.url = url;
        if(player == null) {
            player = createPlayer();
        } else {
            resetPlayer();
            player.setDataSource(context, Uri.parse(url));
            player.prepare();
            player.start();
        }
    }

    public void setTunePlayerCompletedListener(TunePlayerCompleted listener){
        this.playerCompletedListener = listener;
    }

    public void setNextUrl(String url, String title) throws IOException {
        if(!player.isPlaying()){
            setUrl(url, title);
            return;
        }
        this.nextUrl = url;
    }

    public void resetPlayer() {
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
        mPlayer.setOnCompletionListener(this);
        mPlayer.setOnErrorListener(this);
        //mPlayer.setVolume(0.1f, 0.1f);
        mPlayer.start();
        return mPlayer;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        String tmpUrl = this.url;
        try {
            if(nextUrl != null)
                setUrl(nextUrl, nextTitle);
        } catch (IOException e) {
            e.printStackTrace();
        }
        nextUrl = null;
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
    public void playTrack(YoutubeLink link) {
        System.out.println("play track: "+link.getYoutubeTitle()+" "+link.getYoutubeUrl());
        new PlayTrackAsync(this).execute(link);
    }

    public DatabaseConnection getDBConnection(){
        return context.getDBConnection();
    }
}
