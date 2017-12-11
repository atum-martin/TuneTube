package com.atum.tunetube.youtube;

import org.junit.Test;

import java.util.List;

/**
 * Created by mchapman on 11/12/17.
 */

public class YoutubePlaylistTest {
    @Test
    public void testYoutubePlaylistGet(){
        String url = "https://www.youtube.com/channel/UCCIPrrom6DIftcrInjeMvsQ/videos";
        List<YoutubeLink> results = YoutubePlaylist.parsePlaylist(url);
        for(YoutubeLink result : results){
            System.out.println("playlist result: "+result.getYoutubeTitle());
        }
    }
}
