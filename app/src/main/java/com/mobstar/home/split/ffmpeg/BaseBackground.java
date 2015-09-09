package com.mobstar.home.split.ffmpeg;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.util.Log;

import com.netcompss.ffmpeg4android.Prefs;
import com.netcompss.ffmpeg4android.ProgressCalculator;
import com.netcompss.loader.LoadJNI;

/**
 * Created by Kesedi on 13.08.2015.
 */
public class BaseBackground {
    private static final String LOG_TAG = BaseBackground.class.getName();

    protected  Activity activity;
    protected  String workFolder;
    protected  AfterDoneBackground afterDoneBackground;
    private LoadJNI vk;
    protected String commandStr;
    private final int STOP_TRANSCODING_MSG = -1;
    private final int FINISHED_TRANSCODING_MSG = 0;
    public ProgressDialog progressBar;
    private String vkLogPath = null;
    protected String title;
    protected String[] complexCommand;

    private boolean onCancel = false;

    public BaseBackground(Activity activity){
        this.activity = activity;
        workFolder = activity.getApplicationContext().getFilesDir().getAbsolutePath() + "/";
        vkLogPath = workFolder + "vk.log";
        Log.d(LOG_TAG,"vkLogPath="+vkLogPath);
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Log.i(Prefs.TAG, "Handler got message");
            if (progressBar != null) {
                progressBar.dismiss();

                // stopping the transcoding native
                if (msg.what == STOP_TRANSCODING_MSG) {
                    Log.i(Prefs.TAG, "Got cancel message, calling fexit");
                    onCancel = true;
                    vk.fExit(activity);


                }
            }
        }
    };



    private void runTranscodingUsingLoader() {
        Log.i(Prefs.TAG, "runTranscodingUsingLoader started...");

        PowerManager powerManager = (PowerManager) activity.getSystemService(Activity.POWER_SERVICE);
        PowerManager.WakeLock wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "VK_LOCK");
        Log.d(Prefs.TAG, "Acquire wake lock");
        wakeLock.acquire();

        vk = new LoadJNI();
        try {

            // complex command
            vk.run(complexCommand, workFolder, activity);

//            GeneralUtils.copyFileToFolder(vkLogPath,workFolder);

        } catch (Throwable e) {
            Log.e(Prefs.TAG, "vk run exeption.", e);
        }
        finally {
            if (wakeLock.isHeld())
                wakeLock.release();
            else{
                Log.i(Prefs.TAG, "Wake lock is already released, doing nothing");
            }
        }
        Log.i(Prefs.TAG, "doInBackground finished");
    }

    public void runTranscoding() {
        progressBar = new ProgressDialog(activity);
        progressBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressBar.setTitle(title);
        progressBar.setMessage("Press the cancel button to end the operation");
        progressBar.setMax(100);
        progressBar.setProgress(0);

        progressBar.setCancelable(false);
        progressBar.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                handler.sendEmptyMessage(STOP_TRANSCODING_MSG);
            }
        });

        progressBar.show();

        new Thread() {
            public void run() {
                Log.d(Prefs.TAG,"Worker started");
                try {
                    //sleep(5000);
                    runTranscodingUsingLoader();
                    handler.sendEmptyMessage(FINISHED_TRANSCODING_MSG);
                    BaseBackground.this.activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (onCancel)
                                afterDoneBackground.onCancel();
                            else
                                afterDoneBackground.onAfterDone();
                        }
                    });

                } catch(Exception e) {
                    Log.e("threadmessage",e.getMessage());
                }
            }
        }.start();

        // Progress update thread
        new Thread() {
            ProgressCalculator pc = new ProgressCalculator(vkLogPath);
            public void run() {
                Log.d(Prefs.TAG,"Progress update started");
                int progress = -1;
                try {
                    while (true) {
                        sleep(300);
                        progress = pc.calcProgress();
                        if (progress != 0 && progress < 100) {
                            progressBar.setProgress(progress);
                        }
                        else if (progress == 100) {
                            Log.i(Prefs.TAG, "==== progress is 100, exiting Progress update thread");
                            pc.initCalcParamsForNextInter();

                            break;
                        }
                    }

                } catch(Exception e) {
                    Log.e("threadmessage",e.getMessage());
                }
            }
        }.start();
    }

}
