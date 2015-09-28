package com.mobstar.api;

import android.content.Context;

import com.mobstar.utils.Constant;

import java.util.HashMap;

/**
 * Created by Alexandr on 28.09.2015.
 */
public class Api {
    public static void sendRequstAddCount(Context mContext, String entryId, String userId,ConnectCallback callback){
        final HashMap<String, String> params = new HashMap<>();
        params.put(Constant.ENTRY_ID_VALUE, entryId);
        params.put(Constant.USER_ID_VALUE, userId);
        RestClient.getInstance(mContext).postRequest(Constant.UPDATE_VIEW_COUNT, params, callback);

    }
}
