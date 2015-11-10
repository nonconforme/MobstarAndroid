package com.mobstar.api.call;

import android.content.Context;

import com.mobstar.api.ConnectCallback;
import com.mobstar.api.RestClient;
import com.mobstar.api.responce.NotificationResponse;
import com.mobstar.api.responce.NullResponse;
import com.mobstar.utils.Constant;

import java.util.HashMap;

/**
 * Created by lipcha on 09.11.15.
 */
public class NotificationCall {

    public static final void notificationRequest(final Context context, final ConnectCallback<NotificationResponse> connectCallback){
        RestClient.getInstance(context).getRequest(Constant.GET_NOTIFICATION, null, connectCallback);
    }

    public static final void notificationReadRequest(final Context context, final String notificationIds, final ConnectCallback<NullResponse> connectCallback){
        final HashMap<String, String> params = new HashMap<>();
        params.put("notificationIds", notificationIds);
        RestClient.getInstance(context).postRequest(Constant.READ_NOTIFICATION, params, connectCallback);
    }

    public static final void messageReadRequest(final Context context, final String threadId, final ConnectCallback<NullResponse> connectCallback){
        final HashMap<String, String> params = new HashMap<>();
        params.put("threadId", threadId);
        RestClient.getInstance(context).postRequest(Constant.MESSAGE_READ, params, connectCallback);
    }

    public static final void deleteNotificationRequest(final Context context, final String notificationId, final ConnectCallback<NullResponse> connectCallback){
        final HashMap<String, String> params = new HashMap<>();
        params.put("notification", notificationId);
        RestClient.getInstance(context).deleteRequest(Constant.DELETE_NOTIFICATION + notificationId, params, connectCallback);
    }

}
