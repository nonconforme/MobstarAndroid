package com.mobstar.home.split.ffmpeg;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.PowerManager;
import android.util.Log;

import com.netcompss.ffmpeg4android.GeneralUtils;
import com.netcompss.ffmpeg4android.Prefs;
import com.netcompss.loader.LoadJNI;

/**
 * Created by Kesedi on 11.08.2015.
 */
public class CropBackground extends AsyncTask<String, Integer, String>
{
    private final Activity _act;
    private final String pathLeft;
    private final String pathResult;
    private final String workFolder;

    public CropBackground(Activity act, String pathLeft, String pathResult) {
        _act = act;
        this.pathLeft=pathLeft;
        this.pathResult=pathResult;
        workFolder = _act.getApplicationContext().getFilesDir().getAbsolutePath() + "/";
    }


    @Override
    protected void onPreExecute() {

    }

    protected String doInBackground(String... paths) {
        Log.i(Prefs.TAG, "doInBackground started...");

        // delete previous log
//        GeneralUtils.deleteFileUtil(workFolder + "/vk.log");

        PowerManager powerManager = (PowerManager)_act.getSystemService(Activity.POWER_SERVICE);
        PowerManager.WakeLock wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "VK_LOCK");
        Log.d(Prefs.TAG, "Acquire wake lock");
        wakeLock.acquire();

//        String commandStr = "ffmpeg -y -i "+pathLeft+" -strict experimental -vf transpose=2 -s 640x480 -r 30 -aspect 3:4 -ab 48000 -ac 2 -ar 22050 -vcodec mpeg4 -b 2097152 "+ pathResult;
//        String commandStr = "ffmpeg -y -i "+pathLeft+" -strict experimental -vf crop=100:100:100:0 -s 640x480 -r 15 -aspect 3:4 -ab 12288 -vcodec mpeg4 "+ pathResult;
        String commandStr = "ffmpeg -y -i "+pathLeft+" -strict experimental -vf crop=153:307:100:0 -s 154x308 -vcodec mpeg4 "+ pathResult;


        LoadJNI vk = new LoadJNI();
        try {

            // complex command
            vk.run(GeneralUtils.utilConvertToComplex(commandStr), workFolder, _act);

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
        return pathResult;
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
    protected void onPostExecute(String result) {
    }

}