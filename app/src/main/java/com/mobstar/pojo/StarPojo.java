package com.mobstar.pojo;

import com.mobstar.utils.TimeUtility;

import java.io.Serializable;

public class StarPojo implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private String StarID, StarName;
    private String ProfileImage, StarredDate, ProfileCover, Rank, Stats, IsMyStar;

    public String getIsMyStar() {
        return IsMyStar;
    }

    public void setIsMyStar(String isMyStar) {
        IsMyStar = isMyStar;
    }

    //for talent view hide/show
    public boolean isChecked = false;

    public String getStarID() {
        return StarID;
    }

    public void setStarID(String starID) {
        StarID = starID;
    }

    public String getStarName() {
        return StarName;
    }

    public void setStarName(String starName) {
        StarName = starName;
    }

    public String getProfileImage() {
        return ProfileImage;
    }

    public void setProfileImage(String profileImage) {
        ProfileImage = profileImage;
    }

    public String getProfileCover() {
        return ProfileCover;
    }

    public void setProfileCover(String profileCover) {
        ProfileCover = profileCover;
    }

    public String getRank() {
        return Rank;
    }

    public void setRank(String rank) {
        Rank = rank;
    }

    public String getStats() {
        return Stats;
    }

    public void setStats(String stats) {
        Stats = stats;
    }

    public String getStarredDate() {
        String tempDate = null;
        tempDate = TimeUtility.getStringTime(TimeUtility.getDiffTime(StarredDate));
        return tempDate + "";
    }

    public void setStarredDate(String starredDate) {
        StarredDate = starredDate;
    }

}
