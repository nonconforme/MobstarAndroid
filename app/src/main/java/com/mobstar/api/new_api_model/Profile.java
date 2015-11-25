package com.mobstar.api.new_api_model;

import java.io.Serializable;

/**
 * Created by lipcha on 17.11.15.
 */
public class Profile implements Serializable{
    private String id;
    private String fullName;
    private String displayName;
    private String coverImage;
    private String profileImage;
    private String bio;
    private String tagline;
    private boolean iAmStar;
    private boolean isMyStar;

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

    public boolean isiAmStar() {
        return iAmStar;
    }

    public boolean isMyStar() {
        return isMyStar;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public void setCoverImage(String coverImage) {
        this.coverImage = coverImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public void setTagline(String tagline) {
        this.tagline = tagline;
    }

    public void setiAmStar(boolean iAmStar) {
        this.iAmStar = iAmStar;
    }

    public void setIsMyStar(boolean isMyStar) {
        this.isMyStar = isMyStar;
    }
}
