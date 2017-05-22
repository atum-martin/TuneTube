package com.atum.tunetube.youtube;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by atum-martin on 22/05/2017.
 */

public class YoutubeAutocomplete {

    /**
     * The purpose of this code is to return a List of strings representing the suggested autocomplete options for the query specified.
     * @param query
     * @param javascript
     * @return
     */
    public static List<String> extractSuggestions(String query, String javascript){

        String json = extractJson(query,javascript);
        List<String> output = new LinkedList<>();
        if(json == null)
            return output;
        try {
            JSONArray array = new JSONArray(json);
            for(int i = 0; i < array.length(); i++){
                String term = getSearchTerm(array.getJSONArray(i));
                //System.out.println("suggestion: "+term);
                if(term != null && !term.isEmpty())
                    output.add(term);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return output;
    }

    /**
     * The purpose of this function is to extract the json object from the javascript output of the youtube autocomplete API.
     * @param query
     * @param javascript
     * @return
     */
    private static String extractJson(String query, String javascript) {
        int index = javascript.indexOf(query)+query.length()+2;
        //index returned -1
        if(index == 1+query.length())
            return null;
        String json = javascript.substring(index);
        int endIdx = json.lastIndexOf("]],")+2;
        //index returned -1
        if(endIdx == 1)
            return null;
        json = json.substring(0,endIdx);
        return json;
    }

    private static String getSearchTerm(JSONArray jsonArray) throws JSONException {
        return jsonArray.getString(0);
    }
}
