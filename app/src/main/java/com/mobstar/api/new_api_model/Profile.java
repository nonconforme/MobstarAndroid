package com.mobstar.api.new_api_model;

/**
 * Created by lipcha on 17.11.15.
 */
public class Profile {
    private String id;
    private String fullName;
    private String displayName;
    private String coverImage;
    private String profileImage;
    private String bio;
    private String tagline;

    public String getId() {
        return id;
    }

    public String getFullName() {
        return fullName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getCoverImage() {
        return coverImage;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public String getBio() {
        return bio;
    }

    public String getTagline() {
        return tagline;
    }
}
