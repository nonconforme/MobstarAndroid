package com.mobstar.api.new_api_model;

import com.google.gson.annotations.SerializedName;
import com.mobstar.utils.TimeUtility;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by lipcha on 20.11.15.
 */
public class Entry implements Serializable {

    private String id;
    @SerializedName("user_id")
    private long userId;
    @SerializedName("category_id")
    private int categiryId;
    private String name;
    private String type;
    private int status;
    @SerializedName("totalviews")
    private int totalViews;
    private String language;
    private String tagline;
    @SerializedName("up_votes")
    private int upVotes;
    @SerializedName("total_comments")
    private int totalComents;
    @SerializedName("down_votes")
    private int downVots;
    private int rank;
    @SerializedName("vouted_by_you")
    private boolean voutedByYou;
    private long created;

    private ArrayList<EntryFile> files;

    public String getId() {
        return id;
    }

    public long getUserId() {
        return userId;
    }

    public long getCategiryId() {
        return categiryId;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public int getStatus() {
        return status;
    }

    public int getTotalViews() {
        return totalViews;
    }

    public String getLanguage() {
        return language;
    }

    public String getTagline() {
        return tagline;
    }

    public int getUpVotes() {
        return upVotes;
    }

    public int getTotalComents() {
        return totalComents;
    }

    public int getDownVots() {
        return downVots;
    }

    public int getRank() {
        return rank;
    }

    public boolean isVoutedByYou() {
        return voutedByYou;
    }

    public long getCreated() {
        return created;
    }

    public ArrayList<EntryFile> getFiles() {
        return files;
    }

    public String getCreatedAgo(){
        return TimeUtility.getStringTime((System.currentTimeMillis() - created) / 1000);
    }

    public String getSubcategory(){
        return "";
    }

    public String getHeight(){
        return "";
    }

    public String getAge(){
        return "";
    }

    public ArrayList<String> getTags(){
        return new ArrayList<>();
    }


}
