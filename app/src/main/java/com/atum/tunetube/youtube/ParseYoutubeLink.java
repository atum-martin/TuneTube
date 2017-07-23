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
            String title =  StringEscapeUtils.unescapeHtml4(line.substring(line.indexOf(">")+1,line.indexOf("</a>")));
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
