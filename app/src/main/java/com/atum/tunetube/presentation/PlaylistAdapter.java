package com.atum.tunetube.presentation;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.atum.tunetube.Constants;
import com.atum.tunetube.MainActivity;
import com.atum.tunetube.R;
import com.atum.tunetube.model.PlayableItem;
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
        final PlaylistInternalAdapter listAdapter = new PlaylistInternalAdapter(activity, songs);

        for(PlaylistItem link : songs) {
            listAdapter.add(link);
        }
        mainListView.setAdapter( listAdapter );
    }

    public class PlaylistInternalAdapter extends BaseAdapter implements ListAdapter {
        private final List<PlaylistItem> songs;
        private ArrayList<PlaylistItem> list = new ArrayList<>();
        private Context context;

        public PlaylistInternalAdapter(Context context, List<PlaylistItem> songs) {
            this.context = context;
            this.songs = songs;
        }

        public void add(PlaylistItem item){
            list.add(item);
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int pos) {
            return list.get(pos);
        }

        @Override
        public long getItemId(int pos) {
            return pos;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View view = convertView;
            if (view == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.simplerow, null);
            }

            //Handle TextView and display string from your list
            TextView titleText = (TextView) view.findViewById(R.id.rowTextView);
            titleText.setText(list.get(position).toString());
            titleText.setOnClickListener(new View.OnClickListener(){
                public void onClick(View view){
                    clickItem(position);
                }
            });

            ImageButton addToPlaylistButton = (ImageButton) view.findViewById(R.id.ImageButton01);
            if(!(songs.get(position) instanceof PlayableItem)){
                addToPlaylistButton.setVisibility(View.GONE);
            }
            addToPlaylistButton.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    addTrackToPlaylist(position);
                }
            });

            return view;
        }

        private void addTrackToPlaylist(int position) {
            Log.i(Constants.TAG,"add position to playlist: "+position);
            activity.getPlaylistManager().add(songs.get(position));
        }


        public void clickItem(int itemPosition) {
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
    }

}
