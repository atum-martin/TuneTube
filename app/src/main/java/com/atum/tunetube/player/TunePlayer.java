package com.atum.tunetube.player;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;

import java.io.IOException;

/**
 * Created by atum-martin on 23/05/2017.
 */

public class TunePlayer implements MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener {

    private final Context context;
    private String url = null;
    private String nextUrl = null;
    private MediaPlayer player = null;
    private TunePlayerCompleted playerCompletedListener = null;

    public TunePlayer(Context context){
        this.context = context;
    }

    public void setUrl(String url) throws IOException {
        this.url = url;
        if(player == null) {
            player = createPlayer();
        } else {
            resetPlayer();
            player.setDataSource(context, Uri.parse(url));
            player.prepareAsync();
        }
    }

    public void setTunePlayerCompletedListener(TunePlayerCompleted listener){
        this.playerCompletedListener = listener;
    }

    public void setNextUrl(String url){
        this.nextUrl = url;
    }

    private void resetPlayer() {
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
        mPlayer.setVolume(0.5f, 0.5f);
        mPlayer.start();
        return mPlayer;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        String tmpUrl = this.url;
        try {
            if(nextUrl != null)
                setUrl(nextUrl);
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
        return true;
    }
}
