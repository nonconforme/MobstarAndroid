package com.mobstar.api.new_api_model.response;

import com.mobstar.api.responce.BaseResponse;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by lipcha on 25.11.15.
 */
public class NotificationCountResponse extends BaseResponse {

    private int notifications;

    @Override
    public void configure(JSONObject jsonObject) throws JSONException {
        super.configure(jsonObject);
        if (jsonObject.has("notifications"))
            notifications = jsonObject.getInt("notifications");
    }

    public int getNotificationsCount() {
        return notifications;
    }
}
