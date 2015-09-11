package com.mobstar.api.model;

import com.mobstar.api.responce.BaseResponse;
import com.mobstar.api.responce.ContinentResponse;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by lipcha on 09.09.15.
 */
public class User extends BaseResponse {

    private int id;
    private boolean twitter;
    private int userContinentId;
    private boolean facebook;
    private Google google;

    public int getUserContinentId(){
        return userContinentId;
    }

    @Override
    public void configure(JSONObject jsonObject) throws JSONException {
        super.configure(jsonObject);
        if (jsonObject.has("id"))
            id = jsonObject.getInt("id");
        if (jsonObject.has(ContinentResponse.KEY_CONTINENT))
            userContinentId = jsonObject.getInt(ContinentResponse.KEY_CONTINENT);
//        if (jsonObject.has("twitter"))
//            twitter = jsonObject.getBoolean("twitter");
//
//        if (jsonObject.has("google")) {
//            google = new Google();
//            google.configure(jsonObject.getJSONObject("google"));
//        }
//        if (jsonObject.has("facebook"))
//            facebook = jsonObject.getBoolean("facebook");
    }
}
