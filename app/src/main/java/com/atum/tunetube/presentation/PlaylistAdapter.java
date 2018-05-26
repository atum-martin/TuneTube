package com.atum.tunetube.presentation;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.atum.tunetube.MainActivity;
import com.atum.tunetube.R;
import com.atum.tunetube.task.YoutubeAsyncTask;
import com.atum.tunetube.model.PlaylistItem;
import com.atum.tunetube.player.PlayTrackListener;
import com.atum.tunetube.task.YoutubeTask;
import com.atum.tunetube.youtube.YoutubeLink;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by atum-martin on 07/06/2017.
 */

public class PlaylistAdapter {

    private MainActivity activity;
    private PlayTrackListener listener;

    public PlaylistAdapter(MainActivity activity, PlayTrackListener listener){
        this.activity = activity;
        this.listener = listener;
    }

    public void displayPlaylist(final List<PlaylistItem> songs){
        ListView mainListView = (ListView) activity.findViewById(R.id.listview);
        final ArrayAdapter<PlaylistItem> listAdapter = new ArrayAdapter<>(activity, R.layout.simplerow, new ArrayList<PlaylistItem>());

        for(PlaylistItem link : songs){
            listAdapter.add(link);
        }
        mainListView.setAdapter( listAdapter );
        mainListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> listView, View itemView, int itemPosition, long itemId)
            {
                PlaylistItem item = songs.get(itemPosition);
                //Determine if the item position is another playlist or a track.
                if(item != null && item instanceof YoutubeTask){
                    //If link is a playlist open that playlist.
                    YoutubeTask task = (YoutubeTask) item;
                    new YoutubeAsyncTask(activity).execute(task);
                } else if(item != null && item instanceof YoutubeLink){
                    YoutubeLink link = (YoutubeLink) item;
                    //If link is a track play it.
                    listener.playTrack(link);
                }
            }
        });

    }

}
