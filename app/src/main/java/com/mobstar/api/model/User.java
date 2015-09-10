package com.mobstar.api.model;

import com.mobstar.api.responce.BaseResponse;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by lipcha on 09.09.15.
 */
public class User implements BaseResponse {

    private int id;
    private boolean twitter;
    private int userContinentId;
    private boolean facebook;
    private Google google;

    public int getUserContinentId(){
        return userContinentId;
    }

    @Override
    public void configure(JSONObject jsonObject) {
        if (jsonObject.has("id"))
            try {
                id = jsonObject.getInt("id");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        if (jsonObject.has("twitter")) {
            try {
                twitter = jsonObject.getBoolean("twitter");
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        if (jsonObject.has("userContinent"))
            try {
                userContinentId = jsonObject.getInt("userContinent");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        if (jsonObject.has("google")) {
            google = new Google();
            try {
                google.configure(jsonObject.getJSONObject("google"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if (jsonObject.has("facebook"))
            try {
                facebook = jsonObject.getBoolean("facebook");
            } catch (JSONException e) {
                e.printStackTrace();
            }
    }
}
