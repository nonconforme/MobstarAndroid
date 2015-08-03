package com.mobstar.pojo;

public class TopTalentPojo {

	private String UserID, UserName, UserDisplayName;
	private String ProfileImage, Rank,IsMyStar;

	public String getRank() {
		return Rank;
	}

	public void setRank(String rank) {
		Rank = rank;
	}

	public String getUserID() {
		return UserID;
	}

	public void setUserID(String userID) {
		UserID = userID;
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

	public String getProfileImage() {
		return ProfileImage;
	}

	public void setProfileImage(String profileImage) {
		ProfileImage = profileImage;
	}
	
	public void setIsMyStar(String star){
		IsMyStar=star;
	}
	
	public String getIsMyStar(){
		return IsMyStar;
	}

}
