package com.mobstar.utils.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by vasia on 01.09.15.
 */
public class HeadsetPlugReceiver extends BroadcastReceiver {

    public static final String HEADSET_PLUG_ACTION = "android.intent.action.HEADSET_PLUG";

    private OnHeadsetPlugListener onHeadsetPlugListener;

    public void setOnHeadsetPlugListener(final OnHeadsetPlugListener _onHeadsetPlugListener){
        onHeadsetPlugListener = _onHeadsetPlugListener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (!intent.getAction().equals(Intent.ACTION_HEADSET_PLUG))
            return;
        final boolean connectedHeadphones = (intent.getIntExtra("state", 0) == 1);
        final boolean connectedMicrophone = (intent.getIntExtra("microphone", 0) == 1) && connectedHeadphones;
        final String headsetName = intent.getStringExtra("name");
        if (connectedHeadphones)
            onHeadsetPlugListener.onHeadsetConnect(connectedMicrophone, headsetName);
        else onHeadsetPlugListener.onHeadsetDisconnect();
    }

    public interface OnHeadsetPlugListener{
        void onHeadsetConnect(final boolean connectedMicrophone, final String headsetName);
        void onHeadsetDisconnect();
    }
}
