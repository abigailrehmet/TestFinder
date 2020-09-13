package com.example.testfinder;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class JsonParser {

    private boolean clickedMark = false;

    private HashMap<String,String> parseJsonObject(JSONObject object) {
        //Init hash map
        HashMap<String,String> dataList = new HashMap<>();
        try {
            if (!clickedMark) {
                //Get name from object
                String name = object.getString("name");
                //Get latitude from object
                String latitude = object.getJSONObject("geometry")
                        .getJSONObject("location").getString("lat");
                //Get longitude from object
                String longitude = object.getJSONObject("geometry")
                        .getJSONObject("location").getString("lng");
                String placeid = object.getString("place_id");
                //Put all value in hash map
                dataList.put("name", name);
                dataList.put("lat", latitude);
                dataList.put("lng", longitude);
                dataList.put("placeid", placeid);
            }
            else {
                //get website
                JSONArray reviews = object.getJSONArray("reviews");
                String name = object.getString("name");
                String web = object.getString("website");
                String rating = object.getString("rating");
                String open = object.getJSONObject("opening_hours").getString("open_now");
                String monday = object.getJSONObject("opening_hours").getJSONArray("weekday_text").get(0).toString();
                String tuesday = object.getJSONObject("opening_hours").getJSONArray("weekday_text").get(1).toString();
                String wednesday = object.getJSONObject("opening_hours").getJSONArray("weekday_text").get(2).toString();
                String thursday = object.getJSONObject("opening_hours").getJSONArray("weekday_text").get(3).toString();
                String friday = object.getJSONObject("opening_hours").getJSONArray("weekday_text").get(4).toString();
                String saturday = object.getJSONObject("opening_hours").getJSONArray("weekday_text").get(5).toString();
                String sunday = object.getJSONObject("opening_hours").getJSONArray("weekday_text").get(6).toString();
                dataList.put("name", name);
                dataList.put("website", web);
                dataList.put("yo", "yoyo");
                dataList.put("rating", rating);
                dataList.put("open", open);
                dataList.put("monday", monday);
                dataList.put("tuesday", tuesday);
                dataList.put("wednesday", wednesday);
                dataList.put("thursday", thursday);
                dataList.put("friday", friday);
                dataList.put("saturday", saturday);
                dataList.put("sunday", sunday);

                //SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
                //SharedPreferences.Editor editor = sharedPref.edit();
                //editor.putInt(JSONObject "name");
                //editor.commit();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //Return hash map
        return dataList;
    }
    private List<HashMap<String,String>> parseJsonArray(JSONArray jsonArray) {
        //Initialize hash map list
        List<HashMap<String,String>> dataList = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {

            try {
                //Initialize hash map
                HashMap<String, String> data = parseJsonObject((JSONObject) jsonArray.get(i));
                //Add data in hash map list
                dataList.add(data);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        //Return hash map list
        return dataList;
    }

    public List<HashMap<String,String>> parseResult(JSONObject object) throws JSONException {
        //Initialize json array
        JSONArray jsonArray = null;
        //Get result array
        try {
            jsonArray = object.getJSONArray("results");
        }catch (JSONException e ) {
            clickedMark = true;
            List<HashMap<String, String>> hss = new ArrayList<>();
            hss.add(parseJsonObject(object.getJSONObject("result")));
            return hss;
            //e.printStackTrace();
        }
        //Return array
        return parseJsonArray(jsonArray);
    }
}


///I HAVE WENT TO THE BATHROOM REAL QUICK BRB!!