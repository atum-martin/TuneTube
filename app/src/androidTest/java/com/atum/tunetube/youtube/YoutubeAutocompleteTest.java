package com.atum.tunetube.youtube;

import org.junit.Test;

/**
 * Created by Admin on 22/05/2017.
 */

public class YoutubeAutocompleteTest {

    @Test
    public void test1(){
        String query = "hello";
        String content = "google.sbox.p50 && google.sbox.p50([\"hello\",[[\"hello\",0],[\"hello darkness my old friend\",0],[\"hello neighbour\",0],[\"hello neighbor\",0],[\"helloitsamie\",0],[\"hello kitty\",0],[\"hello adele lyrics\",0],[\"hello remix\",0],[\"hello lionel richie\",0],[\"hello neighbour alpha 4\",0]],{\"a\":\"Lvf6Hi5daaFxtBkeUF1gcJvSBlqXT1Km2SxanhRaUTSY4yvetGkMKClf9M9FDTyQ8E8uEnTyzucdbBscG0d5ONc1tsVRActG8tvWRfGYQW\",\"j\":\"q\",\"k\":1,\"q\":\"75rNKiv6I-tPLZzz5epKkP_V0ws\"}])";
        YoutubeAutocomplete.extractSuggestions(query, content);
    }
}
