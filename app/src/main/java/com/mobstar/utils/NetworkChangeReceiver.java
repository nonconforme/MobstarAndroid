package com.mobstar.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by vasia on 31.08.15.
 */
public class NetworkChangeReceiver extends BroadcastReceiver {

    public static final String CONNECTIVITY_CHANGE =  "android.net.conn.CONNECTIVITY_CHANGE";
    public static final String WIFI_STATE_CHANGED =  "android.net.wifi.WIFI_STATE_CHANGED";

    private OnNetworkChangeListener networkChangeListener;

    public void setNetworkChangeListener(final OnNetworkChangeListener _networkChangeListener){
        networkChangeListener = _networkChangeListener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Utility.isNetworkAvailable(context)){
            networkChangeListener.onInternetConnect();
        }else {
            networkChangeListener.onInternetDisconnect();
        }
    }
}
