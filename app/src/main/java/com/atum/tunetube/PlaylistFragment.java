package com.atum.tunetube;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import com.atum.tunetube.model.PlaylistItem;
import com.atum.tunetube.player.TunePlayer;
import com.atum.tunetube.presentation.PlaylistAdapter;
import com.atum.tunetube.task.YoutubeAsyncTask;
import com.atum.tunetube.task.YoutubeTask;

import java.util.List;

public class PlaylistFragment extends Fragment {

    private SearchView searchMenuItem;
    private PlaylistAdapter playListAdapter;
    private TunePlayer player;
    private List<PlaylistItem> playlist;

    public void setPlayer(TunePlayer player) {
        this.player = player;
    }

    public PlaylistAdapter getPlayListAdapter() {
        return playListAdapter;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {


        return inflater.inflate(R.layout.playlists, parent, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        playListAdapter = new PlaylistAdapter((MainActivity) this.getContext(), view, player);
        searchMenuItem = (SearchView) view.findViewById(R.id.musicsearch);
        playListAdapter.displayPlaylist(playlist);

        searchMenuItem.setOnQueryTextListener(
                new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        searchMenuItem.clearFocus();
                        MainActivity context = (MainActivity) PlaylistFragment.this.getContext();
                        YoutubeTask task = new YoutubeTask("Search", YoutubeTask.Type.SEARCH, context, query);
                        new YoutubeAsyncTask(context).execute(task);
                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String s) {
                        return false;
                    }
                }
        );
    }


    public void setPlaylist(List<PlaylistItem> playlist) {
        this.playlist = playlist;
    }
}
