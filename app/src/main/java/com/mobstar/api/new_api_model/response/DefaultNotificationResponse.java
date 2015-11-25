package com.mobstar.api.new_api_model.response;

import com.mobstar.api.new_api_model.DefaultNotification;
import com.mobstar.api.responce.BaseResponse;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by lipcha on 25.11.15.
 */
public class DefaultNotificationResponse extends BaseResponse {

    private DefaultNotification defaultNotification;

    @Override
    public void configure(JSONObject jsonObject) throws JSONException {
        super.configure(jsonObject);
        defaultNotification = fromJson(jsonObject, DefaultNotification.class);
    }

    public DefaultNotification getDefaultNotification() {
        return defaultNotification;
    }
}
