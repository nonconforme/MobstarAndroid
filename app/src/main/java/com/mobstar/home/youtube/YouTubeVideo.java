package com.mobstar.home.youtube;

import java.io.Serializable;

/**
 * Created by lipcha on 04.11.15.
 */
public class YouTubeVideo implements Serializable {

    private String title;
    private String thumbUri;
    private String videoUri;

    public YouTubeVideo(VideoData videoData){
        title = videoData.getTitle();
        thumbUri = videoData.getThumbUri();
        videoUri = videoData.getWatchUri();
    }

    public String getTitle() {
        return title;
    }

    public String getThumbUri() {
        return thumbUri;
    }

    public String getVideoUri() {
        return videoUri;
    }
}
