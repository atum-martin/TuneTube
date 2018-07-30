package com.atum.tunetube.youtube;

import android.util.Log;

import com.atum.tunetube.model.PlaylistItem;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.List;

/**
 * Created by mchapman on 11/12/17.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({Log.class})
public class YoutubePlaylistTest {

    @Before
    public void setup(){
        PowerMockito.mockStatic(Log.class);
    }

    @Test
    public void testYoutubePlaylistGet(){
        String url = "https://www.youtube.com/channel/UCCIPrrom6DIftcrInjeMvsQ/videos";
        List<PlaylistItem> results = YoutubePlaylist.parsePlaylist(url);
        //assert playlist is 20 or more in size
        Assert.assertTrue(results.size() > 20);
    }
}
