package com.atum.tunetube.youtube;

import org.apache.commons.lang3.StringEscapeUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;

/**
 * Created by atum-martin on 24/05/2017.
 */

public class ParseYoutubeLink {

    private static final String PLAYERURL = "/watch?v=";

    public static YoutubeLink[] parseHtml(String line){
        //Youtube have a new desktop UI, this has changed from primarily using HTML to using json.
        if(line.contains("ytInitialData")){
            return parseJson(line);
        }
        //Youtube old UI.
        if(line.contains("yt-lockup-title") || line.contains("content-link")){
            System.out.println("parsing video: "+line);
            int index = line.indexOf("<a");
            line = line.substring(index);
            index = line.indexOf("href=\"");
            line = line.substring(index+6);
            index = line.indexOf("\"");
            String videoId = line.substring(0, index);
            if(videoId.startsWith("/user")){
                //TODO:
                return null;
            }
            int titleIdx = line.indexOf(">");
            int titleEndIdx = line.indexOf("</a>");
            String title = "";
            if(titleIdx != line.length() && titleEndIdx != -1) {
                title = StringEscapeUtils.unescapeHtml4(line.substring(titleIdx + 1, titleEndIdx));
            } else {
                title = line.substring(line.indexOf("title=\"")+7);
                title = StringEscapeUtils.unescapeHtml4(title.substring(0, title.indexOf("\"")));
            }
            YoutubeLink link = new YoutubeLink(videoId,title);

            System.out.println(videoId+" "+title);
            System.out.println("track: "+link.getTrackName());
            for(String artist : link.getArtists()) {
                System.out.println("artist: " + artist);
            }
            return new YoutubeLink[]{ link };
        }
        return new YoutubeLink[]{};
    }

    private static YoutubeLink[] parseJson(String line) {
        //Obtain the json string only. it starts at "ytInitialData" and ends at a semi-colon.
        line = line.substring(line.indexOf("ytInitialData"));
        if(!line.contains("="))
            return new YoutubeLink[]{};
        line = line.substring(line.indexOf("=")+1);
        //line = line.substring(0,line.indexOf(";"));
        LinkedList<YoutubeLink> outputVideos = new LinkedList<>();
        try {
            JSONObject json = new JSONObject(line);
            JSONArray videos = json.getJSONObject("contents")
                    .getJSONObject("twoColumnSearchResultsRenderer")
                    .getJSONObject("primaryContents")
                    .getJSONObject("sectionListRenderer")
                    .getJSONArray("contents")
                    .getJSONObject(0)
                    .getJSONObject("itemSectionRenderer")
                    .getJSONArray("contents");


            for(int i = 0; i < videos.length(); i++){
                JSONObject video = videos.getJSONObject(i);
                if(video.isNull("videoRenderer"))
                    continue;
                JSONObject videoRenderer = video.getJSONObject("videoRenderer");

                String videoId = PLAYERURL + videoRenderer.getString("videoId");
                String title = videoRenderer.getJSONObject("title").getString("simpleText");
                System.out.println("contructing json video: "+videoId +" "+title);

                outputVideos.add(new YoutubeLink(videoId,title));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return outputVideos.toArray(new YoutubeLink[outputVideos.size()]);
    }
}
