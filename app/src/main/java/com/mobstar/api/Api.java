package com.mobstar.api;

import android.app.Dialog;
import android.content.Context;

import com.mobstar.R;
import com.mobstar.utils.Constant;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

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

    public static void deleteStarRequest(final Context context, final String userId, final ConnectCallback connectCallback){
        final HashMap<String, String> params = new HashMap<>();
        params.put(Constant.STAR_VALUE, userId);
        RestClient.getInstance(context).deleteRequest(Constant.DELETE_STAR + userId, params, connectCallback);
    }

    public static void addStarRequest(final Context context, final String userId, final ConnectCallback connectCallback){
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

        final HashMap<String, String> params = new HashMap<>();
        params.put(Constant.STAR_VALUE, userId);
        RestClient.getInstance(context).postRequest(Constant.STAR, params, connectCallback);
    }


}
