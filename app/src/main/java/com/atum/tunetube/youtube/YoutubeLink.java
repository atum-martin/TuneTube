package com.atum.tunetube.youtube;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by atum-martin on 15/05/2017.
 */

public class YoutubeLink {
    private String videoId;
    private String title;

    public YoutubeLink(String videoId, String title){

        this.videoId = videoId;
        this.title = title;
    }

    public String getTrackName(){
        if(!title.contains("-"))
            return title;
        String track = title.substring(title.indexOf("-")+1).trim();
        int specialIdx = getSpecialIndex(track);
        if(specialIdx != 0)
            track = track.substring(0,getSpecialIndex(track));
        return track.trim();
    }

    public List<String> getArtists(){
        List<String> artists = new LinkedList<String>();
        if(!title.contains("-"))
            return artists;
        String primaryArtist = title.substring(0,title.indexOf("-")).trim();
        if(primaryArtist.contains("&")){
            String[] splits = primaryArtist.split("&");

            artists.add(splits[0].trim());
            artists.add(splits[1].trim());
        } else {
            artists.add(primaryArtist.trim());
        }
        String featuringArtist = getFeatureingArtist();
        if(featuringArtist != null)
            artists.add(featuringArtist);
        return artists;
    }

    public String getFeatureingArtist() {
        String[] feat = new String[]{"ft", "feat", "featuring"};
        for(String featuring : feat) {
            if (title.contains(featuring)) {
                int index = title.indexOf(featuring)+featuring.length();
                String artist = title.substring(index);
                //featuring starts at the space
                artist = artist.substring(artist.indexOf(" ")+1);
                //at this point we want to look for commas and & symbols to make a list
                int specialIndex = getSpecialIndex(artist);
                artist = artist.substring(0,specialIndex);
                return artist.trim();
            }
        }
        return null;
    }

    private int getSpecialIndex(String string) {
        int index = -1;
        for(char c : string.toCharArray()){
            index++;
            if(c != ' ' && !Character.isLetterOrDigit(c) && !characterExpcetion(c)){
                if(index == 0)
                    continue;
                return index;
            }
        }
        return string.length();
    }

    private boolean characterExpcetion(char c) {
        char[] exceptions = new char[]{'\'', '’',};
        for(char exception : exceptions){
            if(c == exception)
                return true;
        }
        return false;
    }
}
