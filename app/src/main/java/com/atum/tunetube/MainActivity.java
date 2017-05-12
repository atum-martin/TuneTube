package com.atum.tunetube;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;

import com.atum.tunetube.youtube.YoutubeHttp;
import com.github.axet.vget.VGet;
import com.github.axet.vget.vhs.YouTubeInfo;
import com.github.axet.vget.vhs.YouTubeParser;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TextView mTextMessage;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    mTextMessage.setText(R.string.title_home);

                    //if(true)
                    //    break;
                    new Thread() {
                        public void run(){
                            try
                            {
                                YoutubeHttp http = new YoutubeHttp();
                                //open YoutubeMusic page
                                http.openUrl("https://www.youtube.com/playlist?list=PLqG_Qt4vmaV0x8qTPdcrUyNQ4MglIIxHB");

                                VGet v = new VGet(new URL("https://www.youtube.com/watch?v=5exA_x2P6G8"));
                                /*File downloadDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                                System.out.println(downloadDir);
                                v.setTargetDir(downloadDir);

                                v.download();*/
                                /*playSong(v.getVideo().getSource().toExternalForm());
                                System.out.println("url: "+v.getVideo().getSource().toExternalForm());
                                System.out.println("Finsished downloading");*/

                                YouTubeInfo info = new YouTubeInfo(new URL("https://www.youtube.com/watch?v=5RNePy_awq0"));

                                YouTubeParser parser = new YouTubeParser();

                                List<YouTubeParser.VideoDownload> list = parser.extractLinks(info);
                                boolean playing = false;
                                for (YouTubeParser.VideoDownload d : list) {
                                    if(d.stream instanceof YouTubeInfo.StreamAudio) {
                                        System.out.println("audio only stream: " + d.stream + " " + d.url);
                                        if(!playing){
                                            playing = true;
                                            playSong(d.url.toString());
                                        }
                                    }
                                }
                                for (YouTubeParser.VideoDownload d : list) {
                                    if(!playing){
                                        playing = true;
                                        playSong(d.url.toString());
                                    }
                                    System.out.println(d.stream + " " + d.url);
                                }

                            } catch(
                                    MalformedURLException e)

                            {
                                e.printStackTrace();
                            }
                        }
                    }.start();
                    return true;
                case R.id.navigation_dashboard:
                    mTextMessage.setText(R.string.title_dashboard);
                    return true;
                case R.id.navigation_notifications:
                    mTextMessage.setText(R.string.title_notifications);
                    return true;
            }
            return false;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    private void playSong(String url){
        MediaPlayer mPlayer = null;
        try {
            mPlayer = MediaPlayer.create(this, Uri.parse(url));
            mPlayer.start();
        } catch (Exception e){
            e.printStackTrace();
        }
        mPlayer.start();
    }

}
