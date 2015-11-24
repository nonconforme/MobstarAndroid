package com.mobstar.api.new_api_model.response;

import com.mobstar.api.new_api_model.Settings;
import com.mobstar.api.responce.BaseResponse;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by lipcha on 24.11.15.
 */
public class UserSettingsResponse extends BaseResponse {

    private Settings settings;

    @Override
    public void configure(JSONObject jsonObject) throws JSONException {
        super.configure(jsonObject);
        settings = fromJson(jsonObject, Settings.class);
    }
}
