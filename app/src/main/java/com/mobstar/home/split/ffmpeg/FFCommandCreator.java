package com.mobstar.home.split.ffmpeg;

import android.graphics.Rect;
import android.media.MediaMetadataRetriever;

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
                .append(rect.right - rect.left - 1)
                .append(":")
                .append(rect.bottom - rect.top - 1)
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
                .append(" -metadata:s:v rotate=0")
                .append(" -vcodec mpeg4 ")
                .append(fileOutPath);
        final String complexCommand = stringBuilder.toString();
        return complexCommand;
    }

    public static String getOutputVideoSizeString(final PositionVariant positionVariant){
        String outSize = "";
        switch (positionVariant){
            case ORIGIN_LEFT:
            case ORIGIN_RIGHT:
                outSize = "154x308";
                break;
            case ORIGIN_RIGHT_TOP:
                outSize = "100x100";
                break;
            case ORIGIN_FULLSCREEN:
                outSize = "308x308";
                break;
            case ORIGIN_TOP:
            case ORIGIN_BOTTOM:
                outSize = "308x154";
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
