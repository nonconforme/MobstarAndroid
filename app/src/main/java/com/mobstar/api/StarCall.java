package com.mobstar.api;

import android.content.Context;

import com.mobstar.AdWordsManager;
import com.mobstar.utils.Constant;

import java.util.HashMap;

/**
 * Created by Alexandr on 22.09.2015.
 */
public class StarCall {

    public static void addStarCall(Context context,String userID, ConnectCallback callback){
        HashMap<String,String> params = new HashMap<>();
        params.put(Constant.STAR_VALUE,userID);
        RestClient.getInstance(context).postRequest(Constant.STAR, params, callback);
        AdWordsManager.getInstance().sendFollowedUserEvent();
    }


    public static void delStarCall(Context context,String userID, ConnectCallback callback){
        HashMap<String,String> params = new HashMap<>();
        params.put(Constant.STAR_VALUE, userID);
        RestClient.getInstance(context).deleteRequest(Constant.DELETE_STAR + userID, params, callback);
//        AdWordsManager.getInstance().sendFollowedUserEvent();
    }

}
