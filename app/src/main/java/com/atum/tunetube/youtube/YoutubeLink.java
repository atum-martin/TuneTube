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

    /**
     * Returns all artists in the form of a list that collabrated on a track.
     * @return
     */
    public List<String> getArtists(){
        List<String> artists = new LinkedList<>();
        if(!title.contains("-"))
            return artists;
        String primaryArtist = title.substring(0,title.indexOf("-")).trim();
        artists.addAll(isArtistList(primaryArtist));
        artists.addAll(getFeaturingArtist());
        return artists;
    }

    /**
     * This function takes a string which is presumed to be entirly artists with no special
     * characters and creates and list using various seperators to determine when a new artist
     * begins.
     * @param string The string you wish to be turned into a list of artists.
     * @return A list of artists.
     */
    private List<String> isArtistList(String string){
        List<String> artists = new LinkedList<>();
        String[] parts = string.split(",");
        for(String artist : parts) {
            if (artist.contains("&")) {
                String[] splits = artist.split("&");

                artists.add(splits[0].trim());
                artists.add(splits[1].trim());
            } else {
                artists.add(artist.trim());
            }
        }
        return artists;
    }

    /**
     * Uses the various acronyms to determine if an artist is featuring on the track.
     * Then splits the result to determine if multiple artists are featuring.
     * @return
     */
    public List<String> getFeaturingArtist() {
        List<String> artists = new LinkedList<>();
        String[] feat = new String[]{ "ft.", "ft ", "feat.", "featuring", "feat "};
        for(String featuring : feat) {
            if (title.contains(featuring)) {
                int index = title.indexOf(featuring)+featuring.length();
                String artist = title.substring(index);
                //at this point we want to look for commas and & symbols to make a list

                int specialIndex = getSpecialIndex(artist);
                artist = artist.substring(0,specialIndex).trim();
                artists.addAll(isArtistList(artist));
                break;
                //return artist.trim();
            }
        }
        return artists;
    }

    /**
     * Returns the first index that represents a special character. Its primary function is to spot
     * opening brackets. i.e [ { (
     * @param string
     * @return
     */
    private int getSpecialIndex(String string) {
        int index = -1;
        for(char c : string.toCharArray()){
            index++;
            if(c != ' ' && !Character.isLetterOrDigit(c) && !characterException(c)){
                if(index == 0)
                    continue;
                return index;
            }
        }
        return string.length();
    }

    /**
     * A list of exceptions to the rule from getSpecialIndex, generally represents punctuation marks.
     * @param c
     * @return
     */
    private boolean characterException(char c) {
        char[] exceptions = new char[]{'\'', '’', ',', '&'};
        for(char exception : exceptions){
            if(c == exception)
                return true;
        }
        return false;
    }
}
