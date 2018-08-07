package com.atum.tunetube.task;

import android.os.AsyncTask;

import com.atum.tunetube.MainActivity;
import com.atum.tunetube.model.PlaylistItem;
import com.atum.tunetube.task.YoutubeTask;
import com.atum.tunetube.youtube.YoutubeLink;
import com.atum.tunetube.youtube.YoutubeSearch;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by atum-martin on 07/06/2017.
 */

public class YoutubeAsyncTask extends AsyncTask<YoutubeTask, Integer, Long> {

    private final MainActivity activity;
    private List<PlaylistItem> songs = new LinkedList<>();

    public YoutubeAsyncTask(MainActivity activity){
        this.activity = activity;
    }

    @Override
    protected Long doInBackground(YoutubeTask... tasks) {
        for(YoutubeTask task : tasks){
            List<PlaylistItem> tracks = task.execute();
            songs.addAll(tracks);
        }
        return 0L;
    }

    @Override
    protected void onProgressUpdate(Integer... progress) {

    }

    @Override
    protected void onPostExecute(Long result) {
        activity.displayPlaylist(songs);
    }
}
