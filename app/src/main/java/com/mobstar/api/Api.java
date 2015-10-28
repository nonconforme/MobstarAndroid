package com.mobstar.api;

import android.content.Context;

import com.mobstar.utils.Constant;

import java.util.HashMap;

/**
 * Created by Alexandr on 28.09.2015.
 */
public class Api {

    public static void sendRequestAddCount(Context mContext, String entryId, String userId, ConnectCallback callback){
        final HashMap<String, String> params = new HashMap<>();
        params.put(Constant.ENTRY_ID_VALUE, entryId);
        params.put(Constant.USER_ID_VALUE, userId);
        RestClient.getInstance(mContext).postRequest(Constant.UPDATE_VIEW_COUNT, params, callback);

    }

    public static final void getUserRequest(final Context context, final String userId, final ConnectCallback callback){
        RestClient.getInstance(context).getRequest(Constant.USER + userId + Constant.PROFILE, null, callback);
    }

    public static final void getMyUserProfile(final Context context, final ConnectCallback callback){
        RestClient.getInstance(context).getRequest(Constant.USER_MY_PROFILE, null, callback);
    }

    public static final void getMessageList(final Context context, final String threadId, final ConnectCallback callback){
        RestClient.getInstance(context).getRequest(Constant.GET_MESSAGE_THREAD+threadId, null, callback);
    }

    public static final void sendRequestMessageThreadReaded(final Context context, final String threadId, final ConnectCallback callback){
        final HashMap<String, String> params = new HashMap<>();
        params.put(Constant.THREAD_ID_VALUE, threadId);
        RestClient.getInstance(context).postRequest(Constant.MESSAGE_READ, params, callback);
    }

    public static final void getChatList(final Context context, final ConnectCallback callback){
        RestClient.getInstance(context).getRequest(Constant.GET_MESSAGE, null, callback);
    }

    public static final void sendMessageChat(final Context context,String threadId,String message, final ConnectCallback callback) {
        final HashMap<String, String> params = new HashMap<>();
//        String[] name = {"thread","message"};
        params.put(Constant.THREAD_VALUE, threadId);
        params.put(Constant.MESSAGE_VALUE, message);
        RestClient.getInstance(context).postRequest(Constant.REPLAY_MESSAGE_THREAD, params, callback);
    }

}
