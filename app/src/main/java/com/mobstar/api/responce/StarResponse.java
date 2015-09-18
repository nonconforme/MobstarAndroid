package com.mobstar.api.responce;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by lipcha on 18.09.15.
 */
public class StarResponse implements BaseResponse {

    private String message;
    private String error;


    @Override
    public void configure(JSONObject jsonObject) throws JSONException {
        if (jsonObject.has("message"))
            message = jsonObject.getString("message");
        if(jsonObject.has("error"))
            error = jsonObject.getString("error");
    }

    public String getMessage() {
        return message;
    }

    public String getError() {
        return error;
    }
}
