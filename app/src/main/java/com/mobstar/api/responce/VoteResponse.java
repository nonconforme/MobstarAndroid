package com.mobstar.api.responce;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by lipcha on 09.10.15.
 */
public class VoteResponse extends BaseResponse {

    private String message;

    @Override
    public void configure(JSONObject jsonObject) throws JSONException {
        if (jsonObject == null)
            return;
        if (jsonObject.has("notice"))
            message = jsonObject.getString("notice");
    }

    public String getMessage() {
        return message;
    }
}
