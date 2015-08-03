package com.mobstar.pojo;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import com.mobstar.utils.Utility;

public class MessagePojo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String threadId,userId,messageContent,profileImage,userName,displayName,messageReceived,coverImage;
	private ArrayList<ParticipantsPojo> arrParticipants=new ArrayList<ParticipantsPojo>();
	
	public ArrayList<ParticipantsPojo> getArrParticipants() {
		return arrParticipants;
	}

	public void setArrParticipants(ArrayList<ParticipantsPojo> arrParticipants) {
		this.arrParticipants.clear();
		this.arrParticipants.addAll(arrParticipants);
	}

	public String getCoverImage() {
		return coverImage;
	}

	public void setCoverImage(String coverImage) {
		this.coverImage = coverImage;
	}

	private int read,messageGroup;
	
	public int getMessageGroup() {
		return messageGroup;
	}

	public void setMessageGroup(int messageGroup) {
		this.messageGroup = messageGroup;
	}


	public int getRead() {
		return read;
	}

	public void setRead(int read) {
		this.read = read;
	}

	public String getMessageReceived() {

		String tempDate = null;
		Calendar today = Calendar.getInstance();

		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
		Date gmtTime = null;

		try {
			gmtTime = formatter.parse(messageReceived);// catch exception

		} catch (Exception e) {
			e.printStackTrace();
		}

		Calendar thatDay = Calendar.getInstance();
		thatDay.setTime(gmtTime);

		long diff = (today.getTimeInMillis() - thatDay.getTimeInMillis()) / 1000;
		

		tempDate=Utility.GetStringTime(diff);

		return tempDate + "";
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public void setMessageReceived(String messageReceived) {
		this.messageReceived = messageReceived;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getThreadId() {
		return threadId;
	}

	public void setThreadId(String threadId) {
		this.threadId = threadId;
	}

	public String getMessageContent() {
		return messageContent;
	}

	public void setMessageContent(String messageContent) {
		this.messageContent = messageContent;
	}

	public String getProfileImage() {
		return profileImage;
	}

	public void setProfileImage(String profileImage) {
		this.profileImage = profileImage;
	}

}
