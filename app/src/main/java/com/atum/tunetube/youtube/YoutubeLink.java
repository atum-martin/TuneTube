package com.atum.tunetube.youtube;

import android.support.annotation.NonNull;

import com.atum.tunetube.model.PlayableItem;
import com.atum.tunetube.model.PlaylistItem;
import com.atum.tunetube.sql.DatabaseConnection;
import com.atum.tunetube.util.FileUtils;
import com.github.axet.vget.vhs.YouTubeInfo;
import com.github.axet.vget.vhs.YouTubeParser;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by atum-martin on 15/05/2017.
 */

public class YoutubeLink extends PlaylistItem implements PlayableItem {

    private static final String[] feat = new String[]{ "ft.", "ft ", "feat.", "featuring", "feat "};

    //video ID represents a string similar to the following: /watch?v=rDWuqrJAyGw
    private String videoId;
    private String title;
    private transient List<YouTubeParser.VideoDownload> youtubeUrls = null;
    private List<YoutubeLink> relatedItems = new LinkedList<>();

    public YoutubeLink(String videoId, String title){
        super(title);
        this.videoId = videoId;
        this.title = title;
    }

    public List<YouTubeParser.VideoDownload> getYoutubeUrls(){
        //Attempt 3 times to get the youtube embedded info.
        for(int attempt = 0; attempt < 3; attempt++) {
            if (youtubeUrls == null || youtubeUrls.size() == 0)
                populateYoutubeUrls();
        }
        return youtubeUrls;
    }

    private void populateYoutubeUrls(){
        youtubeUrls = new LinkedList<>();

        //Look for a cached copy of the track on disk first.
        YouTubeParser.VideoDownload videoCachedCopy = FileUtils.checkForLocalCachedCopy(title);
        if(videoCachedCopy != null)
            youtubeUrls.add(videoCachedCopy);

        //If we already have a list of URLs don't perform the fetch again.
        if(youtubeUrls.size() > 0)
            return;
        YouTubeInfo info = null;
        try {
            System.out.println("getYoutubeUrl: "+getYoutubeUrl());
            info = new YouTubeInfo(new URL(getYoutubeUrl()));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        YouTubeParser parser = new YouTubeParser();
        List<YouTubeParser.VideoDownload> list;
        try {
            list = parser.extractLinks(info);
        } catch (Exception e){
            e.printStackTrace();
            return;
        }
        //Video only content is useless to the app, filter this out.
        for(YouTubeParser.VideoDownload track : list){
            if(!(track.stream instanceof YouTubeInfo.StreamVideo))
                youtubeUrls.add(track);
        }

        relatedItems = info.getRelatedItems();

        //perform a sort on the list, this organizes the list in order of highest quality audio.
        //Preferring audio without video.
        Collections.sort(youtubeUrls);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public String getTrackName(){
        if(!title.contains("-"))
            return title;
        String track = title.substring(title.indexOf("-")+1).trim();
        //When obtaining the track name, you want to end the string at featuring or the first symbol you encounter.
        int specialIdx = getSpecialIndex(track);
        int getFeatIdx = getFeaturingIndex(track);
        if(getFeatIdx != -1 && getFeatIdx < specialIdx)
            specialIdx = getFeatIdx;
        if(specialIdx != 0){
            track = track.substring(0,specialIdx);
        }
        return track.trim();
    }

    private int getFeaturingIndex(String track) {
        for(String featuring : feat) {
            int idx = track.indexOf(featuring);
            if (idx != -1) {
                return idx;
            }
        }
        return -1;
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
        char[] exceptions = new char[]{'\'', '’', ',', '&', '-', '$'};
        for(char exception : exceptions){
            if(c == exception)
                return true;
        }
        return false;
    }

    public String getYoutubeUrl() {
        return "https://www.youtube.com" + videoId;
    }

    public String getYoutubeTitle() {
        return title;
    }

    public String getVideoId() {
        return videoId;
    }

    public List<YoutubeLink> getRelatedItems () { return relatedItems; }

    @Override
    public boolean equals(Object other){
        YoutubeLink link = (YoutubeLink) other;
        return this.title.equals(link.title);
    }

    @Override
    public String toString(){
        return getYoutubeTitle();
    }

    @Override
    public String getUrl() {
        getYoutubeUrls();
        DatabaseConnection.getInstance().updatePlaytime(this);
        if(getYoutubeUrls().size() == 0) {
            return null;
        }
        return getYoutubeUrls().get(0).url.toString();
    }

    @Override
    public String getTitle() {
        return title;
    }


    @Override
    public int hashCode(){
        return title.hashCode();
    }
}
