package com.mobstar.api.call;

import android.app.Dialog;
import android.content.Context;

import com.mobstar.AdWordsManager;
import com.mobstar.R;
import com.mobstar.api.ConnectCallback;
import com.mobstar.api.RestClient;
import com.mobstar.utils.Constant;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

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
        final Dialog dialog = new Dialog(context, R.style.DialogAnimationTheme);
        dialog.setContentView(R.layout.dialog_add_star);
        dialog.show();
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                dialog.dismiss();
            }
        };
        timer.schedule(task, 1000);
        HashMap<String,String> params = new HashMap<>();
        params.put(Constant.STAR_VALUE, userID);
        RestClient.getInstance(context).deleteRequest(Constant.DELETE_STAR + userID, params, callback);
//        AdWordsManager.getInstance().sendFollowedUserEvent();
    }

}
