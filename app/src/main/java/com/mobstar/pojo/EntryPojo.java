package com.mobstar.pojo;

import com.mobstar.utils.TimeUtility;

import java.io.Serializable;
import java.util.ArrayList;

public class EntryPojo implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private String ID, UserID, TotalComments, TotalViews, Category, Type, Name, Description, Created, Modified, VideoLink, ImageLink, AudioLink, UpVotesCount, DownvotesCount, Rank, Language, Deleted, Filetype;
    private String ProfileImage, IsMyStar, IAmStar, UserName, UserDisplayName, ProfileCover, Tagline, isVotedByYou = "", Bio, SubCategory = "", Height = "", Age = "";
    private ArrayList<String> arrayTags = new ArrayList<String>();
    private String VideoThumb;
    private boolean isFeed = true;

    private String StrapLine;


    public String getIAmStar() {
        return IAmStar;
    }

    public void setIAmStar(String iAmStar) {
        this.IAmStar = iAmStar;
    }

    public String getID() {
        return ID;
    }

    public void setID(String iD) {
        ID = iD;
    }

    public String getUserID() {
        return UserID;
    }

    public void setUserID(String userID) {
        UserID = userID;
    }

    public String getCategory() {
        return Category;
    }

    public void setCategory(String category) {
        Category = category;
    }

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getIsVotedByYou() {
        return isVotedByYou;
    }

    public void setIsVotedByYou(String isVoted) {
        isVotedByYou = isVoted;
    }

    public String getCreated() {
        String tempDate = null;
        tempDate = TimeUtility.getStringTime(TimeUtility.getDiffTime(Created));
        return tempDate + "";
    }

    public void setCreated(String created) {
        Created = created;
    }

    public String getModified() {
        return Modified;
    }

    public void setModified(String modified) {
        Modified = modified;
    }

    public String getVideoLink() {
        return VideoLink;
    }

    public void setVideoLink(String videoLink) {
        VideoLink = videoLink;
    }

    public String getImageLink() {
        return ImageLink;
    }

    public void setImageLink(String imageLink) {
        ImageLink = imageLink;
    }

    public String getUpVotesCount() {
        return UpVotesCount;
    }

    public void setUpVotesCount(String upVotesCount) {
        UpVotesCount = upVotesCount;
    }

    public String getDownvotesCount() {
        return DownvotesCount;
    }

    public void setDownvotesCount(String downvotesCount) {
        DownvotesCount = downvotesCount;
    }

    public String getRank() {
        return Rank;
    }

    public void setRank(String rank) {
        Rank = rank;
    }

    public String getLanguage() {
        return Language;
    }

    public void setLanguage(String language) {
        Language = language;
    }

    public String getDeleted() {
        return Deleted;
    }

    public void setDeleted(String deleted) {
        Deleted = deleted;
    }

    public String getFiletype() {
        return Filetype;
    }

    public void setFiletype(String filetype) {
        Filetype = filetype;
    }

    public String getAudioLink() {
        return AudioLink;
    }

    public void setAudioLink(String audioLink) {
        AudioLink = audioLink;
    }

    public ArrayList<String> getArrayTags() {
        return arrayTags;
    }

    public void setArrayTags(ArrayList<String> arrayTags) {
        this.arrayTags = arrayTags;
    }

    public void addTags(String tag) {
        arrayTags.add(tag);
    }

    public String getProfileImage() {
        return ProfileImage;
    }

    public void setProfileImage(String profileImage) {
        ProfileImage = profileImage;
    }

    public String getTotalComments() {
        return TotalComments;
    }

    public void setTotalComments(String totalComments) {
        TotalComments = totalComments;
    }

    public String getTotalViews() {
        return TotalViews;
    }

    public void setTotalViews(String totalViews) {
        TotalViews = totalViews;
    }

    public String getIsMyStar() {
        return IsMyStar;
    }

    public void setIsMyStar(String isMyStar) {
        IsMyStar = isMyStar;
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

    public String getProfileCover() {
        return ProfileCover;
    }

    public void setProfileCover(String profileCover) {
        ProfileCover = profileCover;
    }

    public String getTagline() {
        return Tagline;
    }

    public void setTagline(String tagline) {
        Tagline = tagline;
    }

    public String getBio() {
        return Bio;
    }

    public void setBio(String bio) {
        Bio = bio;
    }

    public String getStrapLine() {
        return StrapLine;
    }

    public void setStrapLine(String strapLine) {
        StrapLine = strapLine;
    }

    public String getVideoThumb() {
        return VideoThumb;
    }

    public void setVideoThumb(String videoThumb) {
        VideoThumb = videoThumb;
    }

    public void setIsFeed(boolean isFeeds) {
        isFeed = isFeeds;
    }

    public boolean getIsFeed() {
        return isFeed;
    }

    public void setSubCategry(String subCategory) {
        SubCategory = subCategory;
    }

    public String getSubCategory() {
        return SubCategory;
    }

    public void setHeight(String height) {
        Height = height;
    }

    public String getHeight() {
        return Height;
    }

    public void setAge(String age) {
        Age = age;
    }

    public String getAge() {
        return Age;
    }

}
