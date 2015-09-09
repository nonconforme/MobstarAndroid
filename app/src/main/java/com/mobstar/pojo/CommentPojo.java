package com.mobstar.pojo;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import com.mobstar.utils.Utility;

public class CommentPojo {

	private String CommentID, CommentText, CommentDate, IsDeleted, UserID, UserProfileImage,UserGroup, UserName, UserDisplayName, userFullName;

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

		String tempDate = null;
		Calendar today = Calendar.getInstance();
		
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
		Date gmtTime = null;

		try {
			gmtTime = formatter.parse(CommentDate);// catch exception

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Calendar thatDay = Calendar.getInstance();
		thatDay.setTime(gmtTime);

		long diff = (today.getTimeInMillis() - thatDay.getTimeInMillis()) / 1000;

		//Log.v(Constant.TAG, "Difference "+diff);
		
		tempDate=Utility.GetStringTime(diff);

		return tempDate + "";
	}
}
