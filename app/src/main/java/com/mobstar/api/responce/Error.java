package com.mobstar.api.responce;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by lipcha on 19.11.15.
 */
public class Error {

    private String message;
    private int code;

    public Error(final JSONObject jsonObject){
        try {
            if (jsonObject.has("message"))
                message = jsonObject.getString("message");
            if (jsonObject.has("code"))
                code = jsonObject.getInt("code");
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getMessage() {
        return message;
    }

    public int getCode() {
        return code;
    }
}
