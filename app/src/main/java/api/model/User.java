package api.model;

import org.json.JSONException;
import org.json.JSONObject;

import api.responce.BaseResponse;

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
    public void configure(JSONObject jsonObject) throws JSONException {
        if (jsonObject.has("id"))
            id = jsonObject.getInt("id");
        if (jsonObject.has("twitter"))
            twitter = jsonObject.getBoolean("twitter");
        if (jsonObject.has("userContinent"))
            userContinentId = jsonObject.getInt("userContinent");
        if (jsonObject.has("google")) {
            google = new Google();
            google.configure(jsonObject.getJSONObject("google"));
        }
        if (jsonObject.has("facebook"))
            facebook = jsonObject.getBoolean("facebook");
    }
}
