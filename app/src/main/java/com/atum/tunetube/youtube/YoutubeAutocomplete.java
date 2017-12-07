package com.atum.tunetube.youtube;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by atum-martin on 22/05/2017.
 */

public class YoutubeAutocomplete {

    /**
     * Returns a list of suggestions based on the query provided from youtubes autocomplete backend.
     * @param query The search term you want to use to query youube.
     * @return A list of auto-completed suggestion strings
     */
    public static List<String> getAutocompleteSuggestions(String query){
        YoutubeHttp http = YoutubeHttp.getSingleton();
        String url;
        try {
            url = "https://clients1.google.com/complete/search?client=youtube&hl=en&gl=us&sugexp=ytd2_arm_1&gs_rn=23&gs_ri=youtube&ds=yt&cp="+query.length()+"&gs_id=33&q="+ URLEncoder.encode(query,"UTF-8")+"&callback=google.sbox.p50";
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return new LinkedList<>();
        }
        List<String> content = http.openUrl(url);
        //http error
        if (content.size() == 0)
            return new LinkedList<>();
        //output should only be 1 line long.
        String javascript = content.get(0);
        return extractSuggestions(query, javascript);
    }

    /**
     * The purpose of this code is to return a List of strings representing the suggested autocomplete options for the query specified from the javascript supplied as a parameter.
     * @param query The term you want to be autocompleted.
     * @param javascript The javascript returned from YouTube's webserver.
     * @return A list of suggested strings
     */
    public static List<String> extractSuggestions(String query, String javascript){

        //returns the json that is contained within the javascript block.
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
     * @param query The term you want to be autocompleted.
     * @param javascript The javascript returned from YouTube's webserver.
     * @return The Json object that has been extract from the javascript.
     */
    private static String extractJson(String query, String javascript) {

        String pattern = "(?<="+query+"\",).*]]";
        Pattern jsonPattern = Pattern.compile(pattern);
        Matcher decodeFunctionNameMatch = jsonPattern.matcher(javascript);
        decodeFunctionNameMatch.find();
        return decodeFunctionNameMatch.group();


        /*int index = javascript.indexOf(query)+query.length()+2;
        //index returned -1
        if(index == 1+query.length())
            return null;
        String json = javascript.substring(index);
        int endIdx = json.lastIndexOf("]],")+2;
        //index returned -1
        if(endIdx == 1)
            return null;
        json = json.substring(0,endIdx);
        return json;*/
    }

    private static String getSearchTerm(JSONArray jsonArray) throws JSONException {
        return jsonArray.getString(0);
    }
}
