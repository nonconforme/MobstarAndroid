package com.mobstar.api.new_api_model.response;

import com.mobstar.api.new_api_model.Profile;
import com.mobstar.api.responce.BaseResponse;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by lipcha on 24.11.15.
 */
public class ProfileResponse extends BaseResponse {

    private Profile profile;

    @Override
    public void configure(JSONObject jsonObject) throws JSONException {
        super.configure(jsonObject);
        profile = getGson().fromJson(jsonObject.toString(), Profile.class);
    }

    public Profile getProfile() {
        return profile;
    }
}
