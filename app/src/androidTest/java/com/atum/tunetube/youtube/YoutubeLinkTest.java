package com.atum.tunetube.youtube;

import android.os.Environment;

import org.junit.Assert;
import org.junit.Test;

import java.util.List;


/**
 * Created by atum-martin on 18/05/2017.
 */


public class YoutubeLinkTest {
    /*@Before
    public void setupMocks(){
        mockStatic(Environment.class);
        when(Environment.getExternalStoragePublicDirectory("")).thenReturn(new File("/"));
    }*/

    @Test
    public void testYoutubeTrackHotlink(){

        YoutubeLink link = new YoutubeLink("/watch?v=28GpKacWLWI", "Abstract - Neverland (ft. Ruth B) (Prod. Blulake)");
        Assert.assertNotNull(link.getYoutubeTitle());
        Assert.assertNotNull(link.toString());
        Assert.assertNotNull(link.getVideoId());
        Assert.assertNotNull(link.getYoutubeUrl());
        Assert.assertNotNull(link.getYoutubeUrls());
        Assert.assertTrue(link.getRelatedItems().size() > 1);
    }

}
