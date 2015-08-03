package com.mobstar.pojo;

import java.io.Serializable;

public class WhoToFollowPojo implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String ID,ProfileImage,DisplayName;
	private boolean isChecked;
	
	public String getID() {
		return ID;
	}

	public void setID(String iD) {
		ID = iD;
	}
	
	public String getProfileImage() {
		return ProfileImage;
	}

	public void setProfileImage(String profileImage) {
		ProfileImage = profileImage;
	}
	
	public String getDisplayName() {
		return DisplayName;
	}

	public void setDisplayName(String displayName) {
		DisplayName = displayName;
	}
	
	public void setSelected(boolean checked) {
		isChecked = checked;
	}
	
	public boolean getSelected() {
		return isChecked;
	}

}
