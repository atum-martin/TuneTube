package com.atum.tunetube.youtube;

import android.util.Log;

import com.atum.tunetube.Constants;

import org.junit.Assert;
import org.junit.Test;

import java.util.List;

/**
 * Created by Admin on 22/05/2017.
 */

public class YoutubeAutocompleteTest {

    @Test
    public void test1(){
        String query = "hello";
        String content = "google.sbox.p50 && google.sbox.p50([\"hello\",[[\"hello\",0],[\"hello darkness my old friend\",0],[\"hello neighbour\",0],[\"hello neighbor\",0],[\"helloitsamie\",0],[\"hello kitty\",0],[\"hello adele lyrics\",0],[\"hello remix\",0],[\"hello lionel richie\",0],[\"hello neighbour alpha 4\",0]],{\"a\":\"Lvf6Hi5daaFxtBkeUF1gcJvSBlqXT1Km2SxanhRaUTSY4yvetGkMKClf9M9FDTyQ8E8uEnTyzucdbBscG0d5ONc1tsVRActG8tvWRfGYQW\",\"j\":\"q\",\"k\":1,\"q\":\"75rNKiv6I-tPLZzz5epKkP_V0ws\"}])";
        List<String> results = YoutubeAutocomplete.extractSuggestions(query, content);

        String[] expected = new String[]{"hello", "hello darkness my old friend", "hello neighbour", "hello neighbor", "helloitsamie", "hello kitty", "hello adele lyrics", "hello remix", "hello lionel richie", "hello neighbour alpha 4"};
        Assert.assertArrayEquals(expected, results.toArray());
    }

    @Test
    public void test2(){
        String query = "alyssa reid game";
        String content = "google.sbox.p50 && google.sbox.p50([\"alyssa reid game\",[[\"alyssa reid game\",35,[39]],[\"alyssa reid the game lyrics\",0,[22,30]],[\"alyssa reid the game ft snoop dogg\",0,[22,30]],[\"alyssa reid the game remix\",0,[22,30]],[\"alyssa reid - the game - official\",0,[22,30]],[\"alyssa reid the game live\",0,[22,30]],[\"alyssa reid the game cover\",0,[22,30]],[\"alyssa reid the game radio\",0,[22,30]],[\"alyssa reid the game ft snoop dogg lyrics\",0,[22,30]],[\"alyssa reid the game album\",0,[22,30]]],{\"a\":\"txCo0WgwCRzWVG5Ng5Bh6NGZJIqAy2BOov1XjSo8cXRvScL6EPzdvoq7dwr3FW7UOM7weDX9mPy7Beawhe8dOXbmyTySJv9wtNzzMUSHMaS5KjDtvtM\",\"j\":\"33\",\"k\":1,\"q\":\"d19gxcgKmnUlAhXymQsaiRnttK8\"}])";
        List<String> results = YoutubeAutocomplete.extractSuggestions(query, content);

        String[] expected = new String[]{"alyssa reid game", "alyssa reid the game lyrics", "alyssa reid the game ft snoop dogg", "alyssa reid the game remix", "alyssa reid - the game - official", "alyssa reid the game live", "alyssa reid the game cover", "alyssa reid the game radio", "alyssa reid the game ft snoop dogg lyrics", "alyssa reid the game album"};
        Assert.assertArrayEquals(expected, results.toArray());
    }

    @Test
    public void test3(){
        List<String> results = YoutubeAutocomplete.getAutocompleteSuggestions("alyssa reid game");
        for(String result : results){
            Log.i(Constants.YOUTUBE_TAG,"test3: "+result);
        }
        Assert.assertTrue(results.size() > 0);
    }

    @Test
    public void test4(){
        List<String> results = YoutubeAutocomplete.getAutocompleteSuggestions("hello");
        Assert.assertTrue(results.size() > 0);
    }


}
