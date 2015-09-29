package com.mobstar.api.responce;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by lipcha on 09.09.15.
 */
public abstract class BaseResponse {

    protected String error="";
    public void configure(JSONObject jsonObject) throws JSONException {
        if(jsonObject.has("error"))
            error = jsonObject.getString("error");
        if(jsonObject.has("errors"))
            error = jsonObject.getString("errors");
    }

    public String getError() {
        return error;
    }

    public boolean hasError(){
        return !error.isEmpty();
    }

}
