package com.mobstar.home.split.ffmpeg;

import android.app.Activity;
import android.util.Log;

import com.mobstar.R;
import com.netcompss.ffmpeg4android.GeneralUtils;

/**
 * Created by Kesedi on 11.08.2015.
 */
public class FFTaskBackground extends BaseBackground {


    public FFTaskBackground(Activity act, String _commandStr, final String _title, AfterDoneBackground afterDoneBackground) {
        super(act);
        this.afterDoneBackground = afterDoneBackground;

        title = _title;
        Log.d("tag complex command ", _commandStr);
        complexCommand = GeneralUtils.utilConvertToComplex(_commandStr);
    }

}