package com.atum.tunetube.presentation;

import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.atum.tunetube.MainActivity;
import com.atum.tunetube.R;
import com.atum.tunetube.youtube.YoutubeLink;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by atum-martin on 07/06/2017.
 */

public class PlaylistAdapter {

    private MainActivity activity;

    public PlaylistAdapter(MainActivity activity){
        this.activity = activity;
    }

    public void displayPlaylist(List<YoutubeLink> songs){
        ListView mainListView = (ListView) activity.findViewById(R.id.listview);
        // Create ArrayAdapter using the planet list.
        final ArrayAdapter<String> listAdapter = new ArrayAdapter<String>(activity, R.layout.simplerow, new ArrayList<String>());

        for(YoutubeLink link : songs){
            listAdapter.add(link.getYoutubeTitle());
        }

        // Set the ArrayAdapter as the ListView's adapter.
        mainListView.setAdapter( listAdapter );

    }
}
