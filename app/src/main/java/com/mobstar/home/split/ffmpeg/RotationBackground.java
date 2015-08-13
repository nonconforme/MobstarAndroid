package com.mobstar.home.split.ffmpeg;

import android.app.Activity;

import com.mobstar.R;
import com.netcompss.ffmpeg4android.GeneralUtils;

/**
 * Created by Kesedi on 11.08.2015.
 */
public class RotationBackground extends BaseBackground
{

    private final String pathLeft;
    private final String pathResult;
    private final String resolution;

    public RotationBackground(Activity act, String pathLeft, String pathResult, int rotation, String resolution, AfterDoneBackground afterDoneBackground) {
        super(act);
        this.pathLeft=pathLeft;
        this.pathResult = pathResult;
        this.afterDoneBackground = afterDoneBackground;
        this.resolution = resolution;
        title=activity.getString(R.string.rotation_title);
        commandStr = "ffmpeg -y -i "+pathLeft+" -strict experimental -vf transpose=" + rotation + " -s " + resolution + " -r 30 -aspect 3:4 -ab 48000 -ac 2 -ar 22050 -vcodec mpeg4 -b 2097152 "+ pathResult;
        complexCommand = GeneralUtils.utilConvertToComplex(commandStr);
    }

}