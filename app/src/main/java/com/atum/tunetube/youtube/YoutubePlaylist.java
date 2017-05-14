package com.atum.tunetube.youtube;

import java.util.List;

/**
 * Created by Admin on 13/05/2017.
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
                String title = line.substring(line.indexOf(">")+1,line.indexOf("</a>"));
                System.out.println(videoId+" "+title);
            }
        }
    }
}
