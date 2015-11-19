package com.mobstar.api.new_api_model.response;

import com.mobstar.api.responce.BaseResponse;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by lipcha on 19.11.15.
 */
public class SuccessResponse extends BaseResponse {

    private String success = "";

    @Override
    public void configure(JSONObject jsonObject) throws JSONException {
        super.configure(jsonObject);
        if (jsonObject.has("success"))
            success = jsonObject.getString("success");
    }

    public String getSuccess() {
        return success;
    }
}
