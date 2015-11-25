package com.mobstar.api.new_api_call;

import android.content.Context;

import com.mobstar.api.ConnectCallback;
import com.mobstar.api.RestClient;
import com.mobstar.api.new_api_model.response.DefaultNotificationResponse;

import static com.mobstar.api.ApiConstant.DEFAULT_NOTIFICATION;

/**
 * Created by lipcha on 25.11.15.
 */
public class NotificationCall {

    public static final void getDefaultNotification(final Context context, final ConnectCallback<DefaultNotificationResponse> connectCallback){
        RestClient.getInstance(context).getRequest(DEFAULT_NOTIFICATION, null, connectCallback);
    }

}
