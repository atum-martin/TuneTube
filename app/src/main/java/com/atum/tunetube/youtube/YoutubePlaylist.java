package com.atum.tunetube.youtube;

import org.apache.commons.lang3.StringEscapeUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;

/**
 * Created by atum-martin on 13/05/2017.
 */
public class YoutubePlaylist {

    public void get(YoutubeHttp http){
        List<String> content = http.openUrl("https://www.youtube.com/channel/UC-9-kyTW8ZkZNDHQJ6FgpwQ");
        for(String line : content){
            if(line.contains("yt-lockup-title")){
                int index = line.indexOf("<a");
                line = line.substring(index);
                index = line.indexOf("href=\"");
                line = line.substring(index+6);
                index = line.indexOf("\"");
                String videoId = line.substring(0, index);
                String title =  StringEscapeUtils.unescapeHtml4(line.substring(line.indexOf(">")+1,line.indexOf("</a>")));
                System.out.println(videoId+" "+title);

                YoutubeLink link = new YoutubeLink(videoId,title);
                System.out.println("track: "+link.getTrackName());
                for(String artist : link.getArtists()) {
                    System.out.println("artist: " + artist);
                }
            }
        }
    }
}
