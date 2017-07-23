package com.atum.tunetube.youtube;

import org.apache.commons.lang3.StringEscapeUtils;

/**
 * Created by atum-martin on 24/05/2017.
 */

public class ParseYoutubeLink {

    public static YoutubeLink parseHtml(String line){
        if(line.contains("yt-lockup-title") || line.contains("content-link")){
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
            return link;
        }
        return null;
    }
}
