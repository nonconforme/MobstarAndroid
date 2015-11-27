package com.mobstar.api.new_api_model;

/**
 * Created by lipcha on 24.11.15.
 */
public class Star {

    private long staredDate;
    private int rank;
    private int stat;
    private Profile profile;

    private boolean checked;

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public long getStaredDate() {
        return staredDate;
    }

    public int getRank() {
        return rank;
    }

    public int getStat() {
        return stat;
    }

    public Profile getProfile() {
        return profile;
    }
}
