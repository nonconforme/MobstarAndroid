package com.mobstar.home.new_home_screen.profile;

import java.io.Serializable;

/**
 * Created by lipcha on 23.09.15.
 */
public class UserProfile implements Serializable {

    private String userDisplayName = "";
    private boolean isProfile;
    private String userTagline = "";
    private String userCoverImage;
    private String isMyStar;
    private String userPic;
    private String userName = "";
    private String userId;
    private String entryId;
    private String userFan = "";

    private UserProfile() {
    }

    public void setUserDisplayName(String userDisplayName) {
        this.userDisplayName = userDisplayName;
    }

    public void setIsProfile(boolean isProfile) {
        this.isProfile = isProfile;
    }

    public void setUserTagline(String userTagline) {
        this.userTagline = userTagline;
    }

    public void setUserCoverImage(String userCoverImage) {
        this.userCoverImage = userCoverImage;
    }

    public void setIsMyStar(String isMyStar) {
        this.isMyStar = isMyStar;
    }

    public void setUserPic(String userPic) {
        this.userPic = userPic;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setEntryId(String entryId) {
        this.entryId = entryId;
    }

    public void setUserFan(String userFan){
        this.userFan = userFan;
    }

    public String getUserDisplayName() {
        return userDisplayName;
    }

    public boolean isProfile() {
        return isProfile;
    }

    public String getUserTagline() {
        return userTagline;
    }

    public String getUserCoverImage() {
        return userCoverImage;
    }

    public String getIsMyStar() {
        return isMyStar;
    }

    public String getUserPic() {
        return userPic;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserId() {
        return userId;
    }

    public String getEntryId() {
        return entryId;
    }

    public String getUserFan(){
        return userFan;
    }



    public static Builder newBuilder(){
        return new UserProfile().new Builder();
    }

    public class Builder{

        private Builder() {

        }

        public Builder setUserDisplayName(String userDisplayName) {
            UserProfile.this.setUserDisplayName(userDisplayName);
            return this;
        }

        public Builder setIsProfile(boolean isProfile) {
            UserProfile.this.setIsProfile(isProfile);
            return this;
        }

        public Builder setUserTagline(String userTagline) {
            UserProfile.this.setUserTagline(userTagline);
            return this;
        }

        public Builder setUserCoverImage(String userCoverImage) {
            UserProfile.this.setUserCoverImage(userCoverImage);
            return this;
        }

        public Builder setIsMyStar(String isMyStar) {
            UserProfile.this.setIsMyStar(isMyStar);
            return this;
        }

        public Builder setUserPic(String userPic) {
            UserProfile.this.setUserPic(userPic);
            return this;
        }

        public Builder setUserName(String userName) {
            UserProfile.this.setUserName(userName);
            return this;
        }

        public Builder setUserId(String userId) {
            UserProfile.this.setUserId(userId);
            return this;
        }

        public Builder setEntryId(String entryId) {
            UserProfile.this.setEntryId(entryId);
            return this;
        }

        public Builder setUserFan(String userFan){
            UserProfile.this.setUserFan(userFan);
            return this;
        }

        public UserProfile build(){
            return UserProfile.this;
        }
    }
}
