package com.atum.tunetube;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;

import com.atum.tunetube.dummy.DummyContent;
import com.atum.tunetube.player.TunePlayer;
import com.atum.tunetube.youtube.YoutubeHttp;
import com.atum.tunetube.youtube.YoutubeLink;
import com.atum.tunetube.youtube.YoutubePlaylist;
import com.atum.tunetube.youtube.YoutubeSearch;
import com.github.axet.vget.VGet;
import com.github.axet.vget.vhs.YouTubeInfo;
import com.github.axet.vget.vhs.YouTubeParser;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
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

                   // Intent i = new Intent( MainActivity.this, VideoFragment.class);
                   // startActivity(i);

                    //if(true)
                    //    break;
                    new Thread() {
                        public void run(){
                            /*try
                            {*/
                                List<YoutubeLink> songs = YoutubeSearch.getSearchResults("Adele Hello");
                                for(YoutubeLink link : songs){
                                    System.out.println(link.getTrackName()+" "+link.getYoutubeUrl());
                                }
                                //playSong(songs.get(0).getYoutubeUrls().get(0).url.toString());
                                TunePlayer player = new TunePlayer(MainActivity.this);
                                int index = -1;
                            String youtubeUrl = "https://www.youtube.com/watch?v=FvSdjFju2g0ac";
                            for(YouTubeParser.VideoDownload s : songs.get(0).getYoutubeUrls()){
                                System.out.println("decoded: "+s.url.toString());
                            }
                                while(++index < 10) {
                                    try {
                                        /*System.out.println("playing song: "+songs.get(0).getTrackName()+" "+songs.get(0).getYoutubeUrl());
                                        for(YouTubeParser.VideoDownload link : songs.get(0).getYoutubeUrls()){
                                            System.out.println(link.stream.getClass()+" url: "+link.url.toString());
                                        }
                                        System.out.println("playing song google: "+songs.get(0).getYoutubeUrls().get(index).url.toString());
                                        player.setUrl(songs.get(0).getYoutubeUrls().get(index).url.toString());*/

                                        System.out.println("playing song: "+songs.get(0).getTrackName()+" "+songs.get(0).getYoutubeUrl());
                                        System.out.println("playing song google: "+songs.get(0).getYoutubeUrls().get(index).url.toString());
                                        File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                                        System.out.println(dir.getAbsolutePath()+"/output.m3u");
                                        URL website = new URL(songs.get(0).getYoutubeUrls().get(index).url.toString());
                                        ReadableByteChannel rbc = Channels.newChannel(website.openStream());
                                        FileOutputStream fos = new FileOutputStream(dir.getAbsolutePath()+"/output.m3u");
                                        fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
                                        break;
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    } catch(NullPointerException e){

                                        e.printStackTrace();
                                    }
                                }
                                //player.setNextUrl(songs.get(1).getYoutubeUrls().get(0).url.toString());


                                YoutubePlaylist playlist = new YoutubePlaylist();
                                //open YoutubeMusic page
                                //playlist.parsePlaylist("https://www.youtube.com/channel/UC-9-kyTW8ZkZNDHQJ6FgpwQ");
                                //playSong
                                //if(true)
                                //    return;

                                //VGet v = new VGet(new URL("https://www.youtube.com/watch?v=5exA_x2P6G8"));
                                /*File downloadDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                                System.out.println(downloadDir);
                                v.setTargetDir(downloadDir);

                                v.download();*/
                                /*playSong(v.getVideo().getSource().toExternalForm());
                                System.out.println("url: "+v.getVideo().getSource().toExternalForm());
                                System.out.println("Finsished downloading");*/

                                /*YouTubeInfo info = new YouTubeInfo(new URL("https://www.youtube.com/watch?v=5RNePy_awq0"));

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
                                }*/

                            /*} catch(MalformedURLException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }*/
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
