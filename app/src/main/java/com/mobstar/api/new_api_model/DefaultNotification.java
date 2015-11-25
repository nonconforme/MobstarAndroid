package com.mobstar.api.new_api_model;

import java.io.Serializable;

/**
 * Created by lipcha on 25.11.15.
 */
public class DefaultNotification implements Serializable {

    private String image;
    private String title;
    private String description;
    private int id;
    private boolean showSystem;

    public String getImage() {
        return image;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public int getId() {
        return id;
    }

    public boolean isShowSystem() {
        return showSystem;
    }
}
