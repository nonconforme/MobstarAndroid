package com.mobstar.api.responce;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;

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
