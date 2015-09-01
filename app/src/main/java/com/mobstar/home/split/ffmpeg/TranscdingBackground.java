package com.mobstar.home.split.ffmpeg;

import android.app.Activity;
import android.util.Log;

import com.mobstar.R;
import com.netcompss.ffmpeg4android.GeneralUtils;

/**
 * Created by Kesedi on 10.08.2015.
 */
public class TranscdingBackground extends BaseBackground
{
    private final String pathLeft;
    private final String pathRight;
    private final String pathResult;

    public TranscdingBackground(Activity act, String pathLeft, String pathRight, String pathResult, boolean isHeadphonesOn, AfterDoneBackground afterDoneBackground) {
        super(act);
        this.pathLeft=pathLeft;
        this.pathRight=pathRight;
        this.pathResult=pathResult;
        this.afterDoneBackground = afterDoneBackground;
        title=activity.getString(R.string.join_title);
        String audioMixCommand = "";
        if (isHeadphonesOn)
            audioMixCommand = ";amix=inputs=2:duration=first:dropout_transition=3";
        this.complexCommand = new String[]{"ffmpeg", "-y", "-i", pathLeft, "-i", pathRight, "-strict", "experimental",
                "-filter_complex",
                "[0:v:0]pad=iw*2:ih[bg];" +
                        "[bg][1:v:0]overlay=w" +
                audioMixCommand,
                "-s",
                "308x308",
                "-r",
                "30",
                "-b",
                "15496k",
                "-vcodec",
                "mpeg4",
//                "-ab",
//                "48000",
//                "-ac",
//                "2",
//                "-ar",
//                "22050",
                pathResult};
        String res = toStr(complexCommand);
        Log.d("tag complex command: ", complexCommand.toString());
    }

    private String toStr(String[] strArray){
        final StringBuilder builder = new StringBuilder();
        for (String aStrArray : strArray) {
            builder.append(aStrArray);
            builder.append(" ");
        }
        return builder.toString();
    }

}
