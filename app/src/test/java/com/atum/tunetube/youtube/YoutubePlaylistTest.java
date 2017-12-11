package com.atum.tunetube.youtube;

import junit.framework.Assert;

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
        //assert playlist is 20 or more in size
        Assert.assertTrue(results.size() > 20);
    }
}
