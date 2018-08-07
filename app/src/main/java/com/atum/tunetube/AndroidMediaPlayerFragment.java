package com.atum.tunetube;

import java.util.concurrent.TimeUnit;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import com.atum.tunetube.player.TunePlayer;

public class AndroidMediaPlayerFragment extends Fragment {

    private MediaPlayer mediaPlayer;
    private TunePlayer player;
    public TextView songName, duration;
    private double timeElapsed = 0, finalTime = 0;
    private Handler durationHandler = new Handler();
    private SeekBar seekbar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {


        return inflater.inflate(R.layout.mediaplayer_layout, parent, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        initializeViews(view);
    }

    public void initializeViews(View view){
        songName = (TextView) view.findViewById(R.id.songName);
        player = MainActivity.getInstance().getPlayer();
        mediaPlayer = player.getMediaPlayer();
        finalTime = mediaPlayer.getDuration();
        duration = (TextView) view.findViewById(R.id.songDuration);
        seekbar = (SeekBar) view.findViewById(R.id.seekBar);
        songName.setText("Sample_Song.mp3");

        seekbar.setMax((int) finalTime);
        seekbar.setClickable(true);
        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int progress = seekBar.getProgress();
                Log.i(Constants.MEDIA_TAG, "Moving seekbar to: "+progress +" duration: "+mediaPlayer.getDuration());
                synchronized (seekbar) {
                    mediaPlayer.pause();
                    mediaPlayer.seekTo(progress);
                    //mediaPlayer.start();
                }
            }
        });
        timeElapsed = mediaPlayer.getCurrentPosition();
        seekbar.setProgress((int) timeElapsed);
        durationHandler.postDelayed(updateSeekBarTime, 100);
    }

    //handler to change seekBarTime
    private Runnable updateSeekBarTime = new Runnable() {
        public void run() {
            //get current position
            timeElapsed = mediaPlayer.getCurrentPosition();
            //set seekbar progress
            synchronized (seekbar) {
                seekbar.setProgress((int) timeElapsed);
            }
            //set time remaing
            double timeRemaining = finalTime - timeElapsed;
            duration.setText(String.format("%d min, %d sec", TimeUnit.MILLISECONDS.toMinutes((long) timeRemaining), TimeUnit.MILLISECONDS.toSeconds((long) timeRemaining) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) timeRemaining))));

            //repeat yourself that again in 100 miliseconds
            durationHandler.postDelayed(this, 250);
        }
    };

    public void play(View view) {
        if(player.getMediaPlayer() != null && !player.getMediaPlayer().isPlaying()){
            player.getMediaPlayer().start();
        }
    }

    public void pause(View view) {
        mediaPlayer.pause();
    }

    public void forward(View view) {
        MainActivity.getInstance().getPlayer().playNextTrack();
    }

    public void rewind(View view){

    }

}