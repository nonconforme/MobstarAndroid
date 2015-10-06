package com.mobstar.home.split.ffmpeg;

import android.graphics.Rect;
import android.media.MediaMetadataRetriever;
import android.util.Log;

import com.mobstar.home.split.position_variants.PositionVariant;

/**
 * Created by vasia on 01.09.15.
 */
public class FFCommandCreator {

    public static String getCropAndRotationComplexCommand(final String fileInPath, final String fileOutPath, final Rect rect, final PositionVariant positionVariant){
        final StringBuilder stringBuilder = new StringBuilder();
        stringBuilder
                .append("ffmpeg -y -i ")
                .append(fileInPath)
                .append(" -strict experimental -vf crop=")
                .append(rect.width() - 1)
                .append(":")
                .append(rect.height()- 1)
                .append(":")
                .append(rect.left)
                .append(":")
                .append(rect.top);
        final int rotation = getVideoRotation(fileInPath);
        if (rotation != 0){
            stringBuilder
                    .append(",transpose=")
                    .append(rotation);
        }
        stringBuilder
                .append(" -s ")
                .append(getOutputVideoSizeString(positionVariant))
                .append(" -metadata:s:v rotate=0 -vcodec mpeg4 ")
                .append(fileOutPath);
        final String complexCommand = stringBuilder.toString();
        return complexCommand;
    }

    public static String getMergeVideoCommandString(String pathLeft, String pathRight, String pathResult, boolean isHeadphonesOn, PositionVariant positionVariant) {

        String videoMergeCommand = "";
        String audioMixCommand = "";
        switch (positionVariant){
            case ORIGIN_LEFT:
                videoMergeCommand  = "[bg][0:v:0]overlay=w;[1:v:0]pad=iw*2:ih[bg]";
                break;
            case ORIGIN_RIGHT:
                videoMergeCommand  = "[0:v:0]pad=iw*2:ih[bg];[bg][1:v:0]overlay=w";
                break;
            case ORIGIN_RIGHT_TOP:
                videoMergeCommand = "[bg][1:v:0]overlay=w;[0:v:1]scale=iw*2:ih*2[bg]";
                break;
            case ORIGIN_FULLSCREEN:
                videoMergeCommand = "[bg][0:v:1]overlay=w;[1:v:0]scale=iw*2:ih*2[bg]";
                break;
            case ORIGIN_TOP:
                videoMergeCommand = "[bg][0:v:0]overlay=0:main_h/2;[1:v:0]pad=iw:ih*2[bg]";
                break;
            case ORIGIN_BOTTOM:
                videoMergeCommand = "[0:v:0]pad=iw:ih*2[bg];[bg][1:v:0]overlay=0:main_h/2";
                break;
        }

        if (isHeadphonesOn)
            audioMixCommand = ";amix=inputs=2:duration=first:dropout_transition=3";
        final StringBuilder stringBuilder = new StringBuilder();
        stringBuilder
                .append("ffmpeg -y -i ")
                .append(pathLeft)
                .append(" -i ")
                .append(pathRight)
                .append(" -strict experimental -filter_complex ")
                .append(videoMergeCommand)
                .append(audioMixCommand)
                .append(" -s 308x308 -r 30 -b 15496k -vcodec mpeg4 -shortest ")
                .append(pathResult);

        return stringBuilder.toString();
    }


    public static String getOutputVideoSizeString(final PositionVariant positionVariant){
        String outSize = "";
        switch (positionVariant){
            case ORIGIN_LEFT:
            case ORIGIN_RIGHT:
                outSize = "153x306";
                break;
            case ORIGIN_RIGHT_TOP:
                outSize = "153x153";
                break;
            case ORIGIN_FULLSCREEN:
                outSize = "306x306";
                break;
            case ORIGIN_TOP:
            case ORIGIN_BOTTOM:
                outSize = "306x153";
                break;
        }
        return outSize;
    }

    private static int getVideoRotation(final String _videoFilePath){
        MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
        mediaMetadataRetriever.setDataSource(_videoFilePath);
        String orientationStr = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION);
        if (orientationStr == null)
            return 0;
        int rotation = 0;
        switch (Integer.parseInt(orientationStr)) {
            case 0:
                rotation = 0;
                break;
            case 90:
                rotation = 1;
                break;
            case 270:
                rotation = 2;
                break;
        }
        return rotation;
    }

}
