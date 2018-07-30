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

import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * Created by mchapman on 11/12/17.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({Log.class})
public class YoutubeSearchTest {

    @Before
    public void setup(){
        PowerMockito.mockStatic(Log.class);
    }

    private boolean grepIgnoreCaseYoutubeLinks(List<PlaylistItem> links, String grepTerm){
        boolean resultsForGrep = false;
        for(PlaylistItem link : links){
            if(link.toString().toLowerCase().contains(grepTerm)){
                resultsForGrep = true;
            }
        }
        return resultsForGrep;
    }

    @Test
    public void obtainSearchResultsTest1(){
        //query term should be unique term not applicable to rapid change in web usage.
        String query = "adele";
        List<PlaylistItem> results = YoutubeSearch.getSearchResults(query);
        boolean resultsForGrep = grepIgnoreCaseYoutubeLinks(results, query);
        //If this assert fails it should indicate no results were found for the query.
        Assert.assertEquals(true, resultsForGrep);
        //returned list should be at least 10 items
        Assert.assertTrue(results.size() >= 10);
    }

    @Test
    public void obtainSearchResultsTest2(){
        //query term should be unique term not applicable to rapid change in web usage.
        String query = "ellie goulding";
        List<PlaylistItem> results = YoutubeSearch.getSearchResults(query);
        boolean resultsForGrep = grepIgnoreCaseYoutubeLinks(results, query);
        //If this assert fails it should indicate no results were found for the query.
        Assert.assertEquals(true, resultsForGrep);
        //returned list should be at least 10 items
        Assert.assertTrue(results.size() >= 10);
    }

    @Test
    public void obtainSearchResultsTest3(){
        //query term should be unique term not applicable to rapid change in web usage.
        String query = "kygo";
        List<PlaylistItem> results = YoutubeSearch.getSearchResults(query);
        boolean resultsForGrep = grepIgnoreCaseYoutubeLinks(results, query);
        //If this assert fails it should indicate no results were found for the query.
        Assert.assertEquals(true, resultsForGrep);
        //returned list should be at least 10 items
        Assert.assertTrue(results.size() >= 10);
    }

}
