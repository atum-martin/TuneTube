package com.atum.tunetube;

import android.os.AsyncTask;

import com.atum.tunetube.task.YoutubeTask;
import com.atum.tunetube.youtube.YoutubeLink;
import com.atum.tunetube.youtube.YoutubeSearch;

import java.util.List;

/**
 * Created by Admin on 07/06/2017.
 */

public class YoutubeAsyncTask extends AsyncTask<YoutubeTask, Integer, Long> {

    private final MainActivity activity;
    private List<YoutubeLink> songs;

    public YoutubeAsyncTask(MainActivity activity){
        this.activity = activity;
    }

    @Override
    protected Long doInBackground(YoutubeTask... tasks) {
        for(YoutubeTask task : tasks){
             songs = YoutubeSearch.getSearchResults("Adele Hello");
        }
        return 0L;
    }

    @Override
    protected void onProgressUpdate(Integer... progress) {

    }

    @Override
    protected void onPostExecute(Long result) {
        activity.getPlayListAdapter().displayPlaylist(songs);
    }
}
