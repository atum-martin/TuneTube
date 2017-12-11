package com.atum.tunetube.youtube;

import android.os.Environment;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.File;
import java.util.List;

import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;


/**
 * Created by atum-martin on 18/05/2017.
 */

@RunWith(PowerMockRunner.class)
@PrepareForTest({Environment.class})
public class YoutubeLinkTest {
    @Before
    public void setupMocks(){
        mockStatic(Environment.class);
        when(Environment.getExternalStoragePublicDirectory("")).thenReturn(new File("/"));
    }

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

    @Test
    public void multipleArtists1() throws Exception {
        YoutubeLink link = new YoutubeLink("","Jason Derulo - Swalla (feat. Nicki Minaj & Ty Dolla $ign) (Official Music Video)");
        List<String> artists = link.getArtists();
        //should be Ty Dolla $ign
        String[] assertingValues = new String[]{"Jason Derulo","Nicki Minaj","Ty Dolla $ign"};
        List<String> featuredArtists = link.getFeaturingArtist();

        Assert.assertArrayEquals(assertingValues, artists.toArray());
    }

    @Test
    public void testTrack1() throws Exception {
        YoutubeLink link = new YoutubeLink("","Jason Derulo - Swalla (feat. Nicki Minaj & Ty Dolla $ign) (Official Music Video)");
        Assert.assertEquals("Swalla", link.getTrackName());
    }

    @Test
    public void multipleFeatured1() throws Exception {
        YoutubeLink link = new YoutubeLink("","Jason Derulo - Swalla (feat. Nicki Minaj & Ty Dolla $ign) (Official Music Video)");
        List<String> artists = link.getArtists();
        //should be Ty Dolla $ign
        String[] assertingValues = new String[]{"Nicki Minaj","Ty Dolla $ign"};
        List<String> featuredArtists = link.getFeaturingArtist();

        Assert.assertArrayEquals(assertingValues, featuredArtists.toArray());
    }

    @Test
    public void multipleArtists2() throws Exception {
        YoutubeLink link = new YoutubeLink("","Major Lazer - Run Up (feat. PARTYNEXTDOOR & Nicki Minaj) (Official Lyric Video)");
        List<String> artists = link.getArtists();
        String[] assertingValues = new String[]{"Major Lazer","PARTYNEXTDOOR","Nicki Minaj"};
        List<String> featuredArtists = link.getFeaturingArtist();

        Assert.assertArrayEquals(assertingValues, artists.toArray());
    }

    @Test
    public void multipleFeatured2() throws Exception {
        YoutubeLink link = new YoutubeLink("","Major Lazer - Run Up (feat. PARTYNEXTDOOR & Nicki Minaj) (Official Lyric Video)");
        List<String> artists = link.getArtists();
        String[] assertingValues = new String[]{"PARTYNEXTDOOR","Nicki Minaj"};
        List<String> featuredArtists = link.getFeaturingArtist();

        Assert.assertArrayEquals(assertingValues, featuredArtists.toArray());
    }

    @Test
    public void testTrack2() throws Exception {
        YoutubeLink link = new YoutubeLink("","Major Lazer - Run Up (feat. PARTYNEXTDOOR & Nicki Minaj) (Official Lyric Video)");
        Assert.assertEquals("Run Up", link.getTrackName());
    }

    @Test
    public void multipleArtists3() throws Exception {
        YoutubeLink link = new YoutubeLink("","Beyoncé - Hold Up");
        List<String> artists = link.getArtists();
        String[] assertingValues = new String[]{"Beyoncé",};
        List<String> featuredArtists = link.getFeaturingArtist();

        Assert.assertArrayEquals(assertingValues, artists.toArray());
    }

    @Test
    public void testTrack3() throws Exception {
        YoutubeLink link = new YoutubeLink("","Beyoncé - Hold Up");
        Assert.assertEquals("Hold Up", link.getTrackName());
    }


    @Test
    public void multipleArtists4() throws Exception {
        YoutubeLink link = new YoutubeLink("","Clean Bandit - Rockabye ft. Sean Paul & Anne-Marie [Official Video]");
        List<String> artists = link.getArtists();
        String[] assertingValues = new String[]{"Clean Bandit","Sean Paul","Anne-Marie"};
        List<String> featuredArtists = link.getFeaturingArtist();

        Assert.assertArrayEquals(assertingValues, artists.toArray());
    }

    @Test
    public void multipleFeatured4() throws Exception {
        YoutubeLink link = new YoutubeLink("","Clean Bandit - Rockabye ft. Sean Paul & Anne-Marie [Official Video]");
        List<String> artists = link.getArtists();
        String[] assertingValues = new String[]{"Sean Paul","Anne-Marie"};
        List<String> featuredArtists = link.getFeaturingArtist();

        Assert.assertArrayEquals(assertingValues, featuredArtists.toArray());
    }

    @Test
    public void testTrack4() throws Exception {
        YoutubeLink link = new YoutubeLink("","Clean Bandit - Rockabye ft. Sean Paul & Anne-Marie [Official Video]");
        Assert.assertEquals("Rockabye", link.getTrackName());
    }

    @Test
    public void multipleArtists5() throws Exception {
        YoutubeLink link = new YoutubeLink("","Fifth Harmony - Worth It ft. Kid Ink");
        List<String> artists = link.getArtists();
        String[] assertingValues = new String[]{"Fifth Harmony","Kid Ink"};
        List<String> featuredArtists = link.getFeaturingArtist();

        Assert.assertArrayEquals(assertingValues, artists.toArray());
    }

    @Test
    public void multipleFeatured5() throws Exception {
        YoutubeLink link = new YoutubeLink("","Fifth Harmony - Worth It ft. Kid Ink");
        List<String> artists = link.getArtists();
        String[] assertingValues = new String[]{"Kid Ink"};
        List<String> featuredArtists = link.getFeaturingArtist();

        Assert.assertArrayEquals(assertingValues, featuredArtists.toArray());
    }


    @Test
    public void testTrack5() throws Exception {
        YoutubeLink link = new YoutubeLink("","Fifth Harmony - Worth It ft. Kid Ink");
        Assert.assertEquals("Worth It", link.getTrackName());
    }
}
