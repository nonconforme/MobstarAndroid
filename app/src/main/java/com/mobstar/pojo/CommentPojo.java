package com.mobstar.pojo;

import com.mobstar.utils.TimeUtility;

public class CommentPojo {

    private String CommentID, CommentText, CommentDate, IsDeleted, UserID, UserProfileImage, UserGroup, UserName, UserDisplayName, userFullName;

    public String getCommentID() {
        return CommentID;
    }

    public void setCommentID(String commentID) {
        CommentID = commentID;
    }

    public String getCommentText() {
        return CommentText;
    }

    public void setCommentText(String commentText) {
        CommentText = commentText;
    }

    public void setCommentDate(String commentDate) {
        CommentDate = commentDate;
    }

    public String getIsDeleted() {
        return IsDeleted;
    }

    public void setIsDeleted(String isDeleted) {
        IsDeleted = isDeleted;
    }

    public String getUserID() {
        return UserID;
    }

    public void setUserID(String userID) {
        UserID = userID;
    }

    public String getUserProfileImage() {
        return UserProfileImage;
    }

    public void setUserProfileImage(String userProfileImage) {
        UserProfileImage = userProfileImage;
    }

    public String getUserGroup() {
        return UserGroup;
    }

    public void setUserGroup(String userGroup) {
        UserGroup = userGroup;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getUserDisplayName() {
        return UserDisplayName;
    }

    public void setUserDisplayName(String userDisplayName) {
        UserDisplayName = userDisplayName;
    }

    public String getUserFullName() {
        return userFullName;
    }

    public void setUserFullName(String userFullName) {
        this.userFullName = userFullName;
    }

    public String getCommentDate() {
        String tempDate = TimeUtility.getStringTime(TimeUtility.getDiffTime(CommentDate));
        return tempDate + "";
    }
}
