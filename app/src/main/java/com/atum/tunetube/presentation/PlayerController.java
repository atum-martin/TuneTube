package com.atum.tunetube.presentation;

import android.app.Activity;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.MediaController;

import com.atum.tunetube.R;


/**
 * Created by atum-martin on 24/07/2017.
 */

public class PlayerController implements MediaPlayer.OnPreparedListener, MediaController.MediaPlayerControl, Runnable {

    private MediaPlayer player;
    private Activity activity;
    private Handler handler = null;
    private MediaController mediaController;
    private static final String TAG = "AudioPlayer";
    private static final int TIMEOUT = 5;

    public PlayerController(MediaPlayer player, Activity activity){
        this.activity = activity;
        this.player = player;
        Looper.prepare();
        mediaController = new MediaController(activity);
    }

    public void run(){
        displayController();
    }

   public void displayController(){

        if(handler == null) {
            handler = new Handler();
        }
        System.out.println("preparing media controller");
       Log.d(TAG, "onPrepared");
       mediaController.setMediaPlayer(this);
       mediaController.setAnchorView(activity.findViewById(R.id.main_audio_controller));

       handler.post(new Runnable() {
           public void run() {
               System.out.println("preparing media controller 2");
               mediaController.setEnabled(true);
               mediaController.show(TIMEOUT);
           }
       });
    }

    public MediaController getMediaController(){
        return mediaController;
    }

    //--MediaPlayerControl methods----------------------------------------------------
    public void start() {
        player.start();
    }

    public void show() {
        mediaController.show(TIMEOUT);
    }

    public void pause() {
        player.pause();
    }

    public int getDuration() {
        return player.getDuration();
    }

    public int getCurrentPosition() {
        return player.getCurrentPosition();
    }

    public void seekTo(int i) {
        player.seekTo(i);
    }

    public boolean isPlaying() {
        return player.isPlaying();
    }

    public int getBufferPercentage() {
        return 0;
    }

    public boolean canPause() {
        return true;
    }

    public boolean canSeekBackward() {
        return true;
    }

    public boolean canSeekForward() {
        return true;
    }

    @Override
    public int getAudioSessionId() {
        return 0;
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {

    }
}
