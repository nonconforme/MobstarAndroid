package com.mobstar.home.split.ffmpeg;

import android.app.Activity;

import com.mobstar.R;
import com.netcompss.ffmpeg4android.GeneralUtils;

/**
 * Created by Kesedi on 11.08.2015.
 */
public class CropBackground extends BaseBackground {
//

    public CropBackground(Activity act, String _commandStr, AfterDoneBackground afterDoneBackground) {
        super(act);
        this.afterDoneBackground = afterDoneBackground;

//        commandStr = "ffmpeg -y -i "+pathLeft+" -strict experimental -vf crop=153:307:100:0 -s 154x308 -vcodec mpeg4 "+ pathResult;
        title = activity.getString(R.string.crop_title);
        complexCommand = GeneralUtils.utilConvertToComplex(_commandStr);
    }

}