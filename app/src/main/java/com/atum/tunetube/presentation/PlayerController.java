package com.atum.tunetube.presentation;

import android.app.Activity;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Looper;
import android.widget.MediaController;

import com.atum.tunetube.R;


/**
 * Created by atum-martin on 24/07/2017.
 */

public class PlayerController implements MediaPlayer.OnPreparedListener, MediaController.MediaPlayerControl {

    private MediaPlayer player;
    private Activity activity;
    private Handler handler = null;
    private MediaController mediaController;

    public PlayerController(Context context, MediaPlayer player, Activity activity){
        this.activity = activity;
        this.player = player;
        mediaController = new MediaController(context);
    }

    public void displayController(){

        if(handler == null) {
            Looper.prepare();
            handler = new Handler();
        }
        System.out.println("preparing media controller");
        mediaController.setMediaPlayer(this);
        activity.findViewById(R.id.media_controller);
        mediaController.setAnchorView(activity.findViewById(R.id.media_controller));
        mediaController.setEnabled(true);
        mediaController.show();

        handler.post(new Runnable() {
            public void run() {
                mediaController.setEnabled(true);
                mediaController.show();
            }
        });
    }

    public MediaController getMediaController(){
        return mediaController;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {

    }

    @Override
    public void start() {
        player.start();
    }

    @Override
    public void pause() {
        player.pause();
    }

    @Override
    public int getDuration() {
        return player.getDuration();
    }

    @Override
    public int getCurrentPosition() {
        return player.getCurrentPosition();
    }

    @Override
    public void seekTo(int pos) {
        player.seekTo(pos);
    }

    @Override
    public boolean isPlaying() {
        return player.isPlaying();
    }

    @Override
    public int getBufferPercentage() {
        return 0;
    }

    @Override
    public boolean canPause() {
        return true;
    }

    @Override
    public boolean canSeekBackward() {
        return true;
    }

    @Override
    public boolean canSeekForward() {
        return true;
    }

    @Override
    public int getAudioSessionId() {
        return 0;
    }
}
