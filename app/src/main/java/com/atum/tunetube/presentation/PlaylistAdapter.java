package com.atum.tunetube.presentation;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.atum.tunetube.MainActivity;
import com.atum.tunetube.R;
import com.atum.tunetube.model.PlaylistItem;
import com.atum.tunetube.player.PlayTrackListener;
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

    public void displayPlaylist(final List<YoutubeLink> songs){
        ListView mainListView = (ListView) activity.findViewById(R.id.listview);
        final ArrayAdapter<YoutubeLink> listAdapter = new ArrayAdapter<>(activity, R.layout.simplerow, new ArrayList<YoutubeLink>());

        for(YoutubeLink link : songs){
            listAdapter.add(link);
        }
        mainListView.setAdapter( listAdapter );
        mainListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> listView, View itemView, int itemPosition, long itemId)
            {
                listener.playTrack(songs.get(itemPosition));
            }
        });

    }

    public void displayPlaylists(final List<PlaylistItem> items){
        ListView mainListView = (ListView) activity.findViewById(R.id.listview);
        final ArrayAdapter<PlaylistItem> listAdapter = new ArrayAdapter<>(activity, R.layout.simplerow, new ArrayList<PlaylistItem>());

        for(PlaylistItem item : items){
            listAdapter.add(item);
        }
        mainListView.setAdapter( listAdapter );
        mainListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> listView, View itemView, int itemPosition, long itemId)
            {
                //listener.playTrack(items.get(itemPosition));
            }
        });
    }
}
