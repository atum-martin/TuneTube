package com.atum.tunetube.player;

import android.os.AsyncTask;

import com.atum.tunetube.model.PlayableItem;
import com.atum.tunetube.youtube.YoutubeLink;

import java.io.IOException;

/**
 * Created by atum-martin on 08/06/2017.
 */

public class PlayTrackAsync extends AsyncTask<PlayableItem, Integer, Long> {

    private final TunePlayer player;

    public PlayTrackAsync(TunePlayer player){
        this.player = player;
    }

    @Override
    protected Long doInBackground(PlayableItem... links) {
        for(PlayableItem link : links){
            try {
                player.setUrl(link.getUrl(), link.getTitle());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return 0L;
    }
}
