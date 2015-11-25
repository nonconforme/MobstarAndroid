package com.mobstar.api.new_api_call;

import android.content.Context;

import com.mobstar.api.ConnectCallback;
import com.mobstar.api.RestClient;
import com.mobstar.api.new_api_model.response.DefaultNotificationResponse;
import com.mobstar.api.new_api_model.response.NotificationCountResponse;

import static com.mobstar.api.ApiConstant.DEFAULT_NOTIFICATION;
import static com.mobstar.api.ApiConstant.NOTIFICATION_COUNT;

/**
 * Created by lipcha on 25.11.15.
 */
public class NotificationCall {

    public static final void getDefaultNotification(final Context context, final ConnectCallback<DefaultNotificationResponse> connectCallback){
        RestClient.getInstance(context).getRequest(DEFAULT_NOTIFICATION, null, connectCallback);
    }

    public static final void getNotificationCount(final Context context, final ConnectCallback<NotificationCountResponse> connectCallback){
        RestClient.getInstance(context).getRequest(NOTIFICATION_COUNT, null, connectCallback);
    }

}
