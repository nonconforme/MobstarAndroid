package com.mobstar.pojo;

import com.mobstar.utils.TimeUtility;

public class FeedbackPojo {

    private String FeedbackId, EntryName, Thumbnail, LastComment;

    public String getFeedbackId() {
        return FeedbackId;
    }

    public void setFeedbackId(String feedbackId) {
        FeedbackId = feedbackId;
    }

    public String getEntryName() {
        return EntryName;
    }

    public void setEntryName(String entryName) {
        EntryName = entryName;
    }

    public String getThumbnail() {
        return Thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        Thumbnail = thumbnail;
    }

    public String getLastComment() {
        String tempDate = null;
        tempDate = TimeUtility.getStringTime(TimeUtility.getDiffTime(LastComment));
        return tempDate + "";
    }

    public void setLastComment(String lastComment) {
        LastComment = lastComment;
    }
}
