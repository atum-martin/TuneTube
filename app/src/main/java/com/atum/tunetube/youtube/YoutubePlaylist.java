package com.atum.tunetube.youtube;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by atum-martin on 13/05/2017.
 */
public class YoutubePlaylist {

    public static List<YoutubeLink> parsePlaylist(String url){
        List<String> content = YoutubeHttp.getSingleton().openUrl(url);
        List<YoutubeLink> links = new LinkedList<>();
        for(String line : content){
            if(line.contains("yt-lockup-title")){
                YoutubeLink link = ParseYoutubeLink.parseHtml(line);
                if(link != null)
                    links.add(link);
            }
        }
        return links;
    }
}
