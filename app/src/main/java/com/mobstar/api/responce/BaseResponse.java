package com.mobstar.api.responce;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by lipcha on 09.09.15.
 */
public abstract class BaseResponse {

    private Error error;

    public void configure(JSONObject jsonObject) throws JSONException {
        if(jsonObject.has("error"))
            error = new Error(jsonObject.getJSONObject("error"));
    }

    public String getErrorMessage() {
        if (error == null)
            return null;
        return error.getMessage();
    }

    public Error getError() {
        return error;
    }

    public boolean hasError(){
        return error != null;
    }

    protected Gson getGson(){
        return new Gson();
    }

    protected <T> T fromJson(final JSONObject jsonObject, Class<T> obj){
        return getGson().fromJson(jsonObject.toString(), obj);
    }

}
