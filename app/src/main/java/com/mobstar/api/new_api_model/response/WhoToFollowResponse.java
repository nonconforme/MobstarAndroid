package com.mobstar.api.new_api_model.response;

import com.google.gson.reflect.TypeToken;
import com.mobstar.api.new_api_model.WhoToFollowUser;
import com.mobstar.api.responce.BaseResponse;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Created by lipcha on 25.11.15.
 */
public class WhoToFollowResponse extends BaseResponse {
    private ArrayList<WhoToFollowUser> user;

    @Override
    public void configure(JSONObject jsonObject) throws JSONException {
        super.configure(jsonObject);
        if (jsonObject.has("jsonarr")) {
            final Type listType = new TypeToken<ArrayList<WhoToFollowUser>>() {
            }.getType();
            user = getGson().fromJson(jsonObject.getJSONArray("jsonarr").toString(), listType);
        }
    }

    public ArrayList<WhoToFollowUser> getUser() {
        if (user == null)
            return new ArrayList<>();
        return user;
    }
}
