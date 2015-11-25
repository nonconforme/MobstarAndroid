package com.mobstar.api.new_api_model;

/**
 * Created by lipcha on 25.11.15.
 */
public class WhoToFollowUser {

    private String id;
    private String displayName;
    private String profileImage;
    private boolean isChecked;


    public String getId() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setIsChecked(boolean isChecked) {
        this.isChecked = isChecked;
    }
}
