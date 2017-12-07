package com.atum.tunetube.player;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.VideoView;

import com.atum.tunetube.MainActivity;
import com.atum.tunetube.R;
import com.atum.tunetube.presentation.PlayerController;
import com.atum.tunetube.sql.DatabaseConnection;
import com.atum.tunetube.youtube.YoutubeLink;

import java.io.IOException;
import java.net.URLEncoder;

/**
 * Created by atum-martin on 23/05/2017.
 */

public class TunePlayer implements MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener, PlayTrackListener {

    private final MainActivity context;
    private static final String ENCODING = "UTF-8";
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
        System.out.println("media player prepared");
        mPlayer.setOnCompletionListener(this);
        mPlayer.setOnErrorListener(this);
       // final PlayerController controller = new PlayerController(context.getApplicationContext(), mPlayer, context);
        //mPlayer.setOnPreparedListener(controller);

        LinearLayout root = (LinearLayout) context.findViewById(R.id.container);

        /*ViewTreeObserver vto = root.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new  ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                controller.displayController();
            }
        });*/


        //mPlayer.setVolume(0.1f, 0.1f);

        mPlayer.start();
        System.out.println("media player started");
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

    public MediaPlayer getMediaPlayer() {
        return player;
    }
}
