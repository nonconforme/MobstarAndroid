package com.mobstar.home.split.ffmpeg;

import android.app.Activity;
import android.util.Log;

import com.mobstar.R;
import com.mobstar.home.split.position_variants.PositionVariant;
import com.netcompss.ffmpeg4android.GeneralUtils;

/**
 * Created by Kesedi on 10.08.2015.
 */
public class TranscdingBackground extends BaseBackground
{
    private final String pathLeft;
    private final String pathRight;
    private final String pathResult;

    public TranscdingBackground(Activity act, String pathLeft, String pathRight, String pathResult, boolean isHeadphonesOn, final PositionVariant positionVariant, AfterDoneBackground afterDoneBackground) {
        super(act);
        this.pathLeft=pathLeft;
        this.pathRight=pathRight;
        this.pathResult=pathResult;
        this.afterDoneBackground = afterDoneBackground;
        title=activity.getString(R.string.join_title);
        final String command = FFCommandCreator.getMergeVideoCommandString(pathLeft, pathRight, pathResult, isHeadphonesOn, positionVariant);
        complexCommand = GeneralUtils.utilConvertToComplex(command);
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
