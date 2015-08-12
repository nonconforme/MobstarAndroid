package com.mobstar.home.split.ffmpeg;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.PowerManager;
import android.util.Log;

import com.netcompss.ffmpeg4android.Prefs;
import com.netcompss.loader.LoadJNI;

/**
 * Created by Kesedi on 10.08.2015.
 */
public class TranscdingBackground extends AsyncTask<String, Integer, Integer>
{
    private final Activity _act;
    private final String pathLeft;
    private final String pathRight;
    private final String pathResult;
    private final String workFolder;

    public TranscdingBackground(Activity act, String pathLeft, String pathRight, String pathResult) {
        _act = act;
        this.pathLeft=pathLeft;
        this.pathRight=pathRight;
        this.pathResult=pathResult;
        workFolder = _act.getApplicationContext().getFilesDir().getAbsolutePath() + "/";
    }


    @Override
    protected void onPreExecute() {

    }

    protected Integer doInBackground(String... paths) {
        Log.i(Prefs.TAG, "doInBackground started...");

        // delete previous log
//        GeneralUtils.deleteFileUtil(workFolder + "/vk.log");

        PowerManager powerManager = (PowerManager)_act.getSystemService(Activity.POWER_SERVICE);
        PowerManager.WakeLock wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "VK_LOCK");
        Log.d(Prefs.TAG, "Acquire wake lock");
        wakeLock.acquire();

        String[] complexCommand = {"ffmpeg","-y" ,"-i", pathLeft,"-i", pathRight, "-strict","experimental",
                "-filter_complex",
                "[0:v:0]pad=iw*2:ih[bg];" +
                        "[bg][1:v:0]overlay=w",

                "-s",
                "308x308",
                "-r",
                "30",
                "-b",
                "15496k",
                "-vcodec",
                "mpeg4",
                "-ab",
                "48000",
                "-ac",
                "2",
                "-ar",
                "22050",
                pathResult};


        LoadJNI vk = new LoadJNI();
        try {

            // complex command
            vk.run(complexCommand, workFolder, _act);

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
        return Integer.valueOf(0);
    }

    protected void onProgressUpdate(Integer... progress) {
    }

    @Override
    protected void onCancelled() {
        Log.i(Prefs.TAG, "onCancelled");
        //progressDialog.dismiss();
        super.onCancelled();
    }


    @Override
    protected void onPostExecute(Integer result) {
    }

}
