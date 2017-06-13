package com.atum.tunetube.player;

import android.os.AsyncTask;

import com.atum.tunetube.youtube.YoutubeLink;

import java.io.IOException;

/**
 * Created by atum-martin on 08/06/2017.
 */

public class PlayTrackAsync extends AsyncTask<YoutubeLink, Integer, Long> {

    private final TunePlayer player;

    public PlayTrackAsync(TunePlayer player){
        this.player = player;
    }

    @Override
    protected Long doInBackground(YoutubeLink... links) {
        for(YoutubeLink link : links){
            player.getDBConnection().updatePlaytime(link);
            try {
                player.setUrl(link.getYoutubeUrls().get(0).url.toString(), link.getYoutubeTitle());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return 0L;
    }
}
