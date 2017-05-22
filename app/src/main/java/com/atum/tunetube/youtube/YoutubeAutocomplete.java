package com.atum.tunetube.youtube;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Admin on 22/05/2017.
 */

public class YoutubeAutocomplete {

    public static List<String> extractSuggestions(String query, String javascript){
        List<String> output = new LinkedList<>();
        int index = javascript.indexOf(query)+query.length()+2;
        //index returned -1
        if(index == 1+query.length())
            return output;
        String json = javascript.substring(index);
        int endIdx = json.indexOf("]],")+2;
        //index returned -1
        if(endIdx == 1)
            return output;
        json = json.substring(0,endIdx);

        try {
            JSONArray array = new JSONArray(json);
            for(int i = 0; i < array.length(); i++){
                String term = getSearchTerm(array.getJSONArray(i));
                System.out.println("suggestion: "+term);
                output.add(term);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        System.out.println(json);
        return output;
    }

    private static String getSearchTerm(JSONArray jsonArray) throws JSONException {
        return jsonArray.getString(0);
    }
}
