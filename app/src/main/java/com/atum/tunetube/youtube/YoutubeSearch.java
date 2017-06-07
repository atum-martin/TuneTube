package com.atum.tunetube.youtube;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by atum-martin on 24/05/2017.
 */

public class YoutubeSearch {

    /**
     * A search functions that returns results from youtubes search API, supports both artists and videos returned.
     * @param query The query you wish to pash to the search function.
     * @return
     */
    public static List<YoutubeLink> getSearchResults(String query){
        YoutubeHttp http = YoutubeHttp.getSingleton();
        String url;
        try {
            url = "https://www.youtube.com/results?search_query="+ URLEncoder.encode(query,"UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return new LinkedList<>();
        }
        List<String> content = http.openUrl(url);
        //http error
        if (content.size() == 0)
            return new LinkedList<>();

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
