package com.mobstar.api.responce;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by lipcha on 09.09.15.
 */
public interface BaseResponse {

    void configure(JSONObject jsonObject) throws JSONException;

}
