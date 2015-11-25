package com.mobstar.api.new_api_model;

import java.io.Serializable;

/**
 * Created by lipcha on 20.11.15.
 */
public class EntryFile implements Serializable{

    private long id;
    private String name;
    private String type;
    private String source;
    private String quality;
    private String thumbnail;
    private String path;



    public String getPath() {
        return path;
    }

    public String getSource() {
        return source;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public String getQuality() {
        return quality;
    }
}
