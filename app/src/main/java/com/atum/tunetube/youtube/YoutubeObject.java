package com.atum.tunetube.youtube;

import android.util.Log;

import com.atum.tunetube.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;

public class YoutubeObject {

    private JSONObject json;
    private static final String PLAYER_URL = "/watch?v=";

    public YoutubeObject(JSONObject json){
        this.json = json;
    }

    private JSONObject getResultsRenderer() throws JSONException {
        JSONObject twoColumnWatchNextResults = json.getJSONObject("contents");
        if(!twoColumnWatchNextResults.isNull("twoColumnSearchResultsRenderer"))
            twoColumnWatchNextResults = twoColumnWatchNextResults.getJSONObject("twoColumnSearchResultsRenderer");
        if(!twoColumnWatchNextResults.isNull("twoColumnBrowseResultsRenderer"))
            twoColumnWatchNextResults = twoColumnWatchNextResults.getJSONObject("twoColumnBrowseResultsRenderer");
        return twoColumnWatchNextResults;
    }

    public LinkedList<YoutubeLink> getYoutubeMedia() throws JSONException {
        LinkedList<YoutubeLink> outputVideos = new LinkedList<>();
        JSONArray videos = getYoutubeMediaContentsJson();
        if(videos == null)
            return outputVideos;

        for(int i = 0; i < videos.length(); i++){
            JSONObject video = videos.getJSONObject(i);
            YoutubeLink youtubeTrack = parseYoutubeRenderer(video);
            if(youtubeTrack != null)
                outputVideos.add(youtubeTrack);
        }

        return outputVideos;
    }

    public String getContinuationToken() throws JSONException {
        JSONObject twoColumnWatchNextResults = getResultsRenderer();

        if(!twoColumnWatchNextResults.isNull("itemSectionRenderer")) {
            return twoColumnWatchNextResults
                    .getJSONObject("itemSectionRenderer")
                    .getJSONArray("continuations")
                    .getJSONObject(0)
                    .getJSONObject("nextContinuationData")
                    .getJSONObject("continuation")
                    .toString();
        }
        return "";
    }

    private JSONArray getYoutubeMediaContentsJson() throws JSONException {

        JSONObject twoColumnWatchNextResults = getResultsRenderer();

        //search results
        if(!twoColumnWatchNextResults.isNull("primaryContents")) {
            return twoColumnWatchNextResults
                    .getJSONObject("primaryContents")
                    .getJSONObject("sectionListRenderer")
                    .getJSONArray("contents")
                    .getJSONObject(0)
                    .getJSONObject("itemSectionRenderer")
                    .getJSONArray("contents");
        }
        //Recommended tracks/videos
        if(!twoColumnWatchNextResults.isNull("secondaryResults")) {
            return twoColumnWatchNextResults
                    .getJSONObject("secondaryResults")
                    .getJSONObject("secondaryResults")
                    .getJSONArray("results");
        }
        //playlists
        if(!twoColumnWatchNextResults.isNull("tabs")) {
            return twoColumnWatchNextResults
                    .getJSONArray("tabs")
                    .getJSONObject(1)
                    .getJSONObject("tabRenderer")
                    .getJSONObject("content")
                    .getJSONObject("sectionListRenderer")
                    .getJSONArray("contents")
                    .getJSONObject(0)
                    .getJSONObject("itemSectionRenderer")
                    .getJSONArray("contents")
                    .getJSONObject(0)
                    .getJSONObject("gridRenderer")
                    .getJSONArray("items");
        }
        return null;
    }

    private YoutubeLink parseYoutubeRenderer(JSONObject renderer) throws JSONException {
        JSONObject videoRenderer;
        if(!renderer.isNull("videoRenderer")){
            videoRenderer = renderer.getJSONObject("videoRenderer");
        } else if(!renderer.isNull("compactVideoRenderer")) {
            videoRenderer = renderer.getJSONObject("compactVideoRenderer");
        } else if(!renderer.isNull("gridVideoRenderer")) {
            videoRenderer = renderer.getJSONObject("gridVideoRenderer");
        } else if(!renderer.isNull("channelRenderer")){
            //use case for an artists channel appears in results rather than a track/video.
            return null;
        } else {
            return null;
        }

        String videoId = PLAYER_URL + videoRenderer.getString("videoId");
        String title = videoRenderer.getJSONObject("title").getString("simpleText");
        Log.i(Constants.YOUTUBE_TAG,"contructing json video: "+videoId +" "+title);
        return new YoutubeLink(videoId,title);
    }
}
