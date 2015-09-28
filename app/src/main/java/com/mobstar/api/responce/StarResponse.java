package com.mobstar.api.responce;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by lipcha on 18.09.15.
 */
public class StarResponse extends BaseResponse {

    private String message;


    @Override
    public void configure(JSONObject jsonObject) throws JSONException {
        super.configure(jsonObject);
        if (jsonObject.has("message"))
            message = jsonObject.getString("message");
    }

    public String getMessage() {
        return message;
    }

}
