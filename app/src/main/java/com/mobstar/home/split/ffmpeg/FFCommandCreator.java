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
                .append(" -metadata:s:v rotate=0 -vcodec mpeg4 ")
                .append(fileOutPath);
        final String complexCommand = stringBuilder.toString();
        return complexCommand;
    }

    public static String getMergeVideoCommandString(String pathLeft, String pathRight, String pathResult, boolean isHeadphonesOn, PositionVariant positionVariant) {

        String firstVideoPath = pathRight;
        String secondVideoPath = pathLeft;
        switch (positionVariant){
            case ORIGIN_LEFT:

                break;
            case ORIGIN_RIGHT:
                firstVideoPath = pathLeft;
                secondVideoPath = pathRight;
                break;
            case ORIGIN_RIGHT_TOP:

                break;
            case ORIGIN_FULLSCREEN:

                break;
            case ORIGIN_TOP:

                break;
            case ORIGIN_BOTTOM:

                break;

        }

        String audioMixCommand = "";
        if (isHeadphonesOn)
            audioMixCommand = ";amix=inputs=2:duration=first:dropout_transition=3";
        final StringBuilder stringBuilder = new StringBuilder();
        stringBuilder
                .append("ffmpeg -y -i ")
                .append(firstVideoPath)
                .append(" -i ")
                .append(secondVideoPath)
                .append(" -strict experimental -filter_complex ")
//                        оригінал справа, з камери - зліва
                .append("[0:v:0]pad=iw*2:ih[bg];")
                .append("[bg][1:v:0]overlay=w")
//                        оригінал справа знизу на пів екрана б з камери - зліва
//                .append("pad=iw*2:ih[bg];")
//                .append("overlay=w:150")

//                .append("[0:v]setpts=PTS-STARTPTS, pad=iw*2:ih[bg];")
//                .append("[1:v]setpts=PTS-STARTPTS[fg];")
//                .append("[bg][fg]overlay=w")

//                .append("[0:v]pad=iw*2:ih[bg];")
//                .append("[1:v][bg][fg]overlay=w")

                .append(audioMixCommand)
                .append(" -s 308x308 -r 30 -b 15496k -vcodec mpeg4 ")
                .append(pathResult);

        return stringBuilder.toString();
    }

//    final StringBuilder stringBuilder = new StringBuilder();
//    stringBuilder
//            .append("ffmpeg -y -i ")
//            .append(pathLeft)
//    .append(" -i ")
//    .append(pathRight)
//    .append(" -strict experimental -filter_complex ")
//    .append("[0:v:0]pad=iw*2:ih[bg];")
//    .append("[bg][1:v:0]overlay=w")
//    .append(audioMixCommand)
//    .append(" -s 308x308 -r 30 -b 15496k -vcodec mpeg4 ")
//    .append(pathResult);

    public static String getOutputVideoSizeString(final PositionVariant positionVariant){
        String outSize = "";
        switch (positionVariant){
            case ORIGIN_LEFT:
            case ORIGIN_RIGHT:
                outSize = "153x306";
                break;
            case ORIGIN_RIGHT_TOP:
                outSize = "100x100";
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
