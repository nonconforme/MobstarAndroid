package com.mobstar.pojo;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import android.text.format.DateUtils;

import com.mobstar.utils.Utility;

public class MessageThreadPojo implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String ThreadId,messageId,message,senderId,senderprofileImage,senderdisplayName,senderUserName,messageReceived,messageRead;

	public String getMessageRead() {
		return messageRead;
	}
	public void setMessageRead(String messageRead) {
		this.messageRead = messageRead;
	}
	public String getThreadId() {
		return ThreadId;
	}
	public void setThreadId(String threadId) {
		ThreadId = threadId;
	}
	public String getMessage() {
		return message;
	}
	public String getMessageId() {
		return messageId;
	}
	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getSenderId() {
		return senderId;
	}
	public void setSenderId(String senderId) {
		this.senderId = senderId;
	}
	public String getSenderprofileImage() {
		return senderprofileImage;
	}
	public void setSenderprofileImage(String senderprofileImage) {
		this.senderprofileImage = senderprofileImage;
	}
	public String getSenderdisplayName() {
		return senderdisplayName;
	}
	public void setSenderdisplayName(String senderdisplayName) {
		this.senderdisplayName = senderdisplayName;
	}
	public String getSenderUserName() {
		return senderUserName;
	}
	public void setSenderUserName(String senderUserName) {
		this.senderUserName = senderUserName;
	}
	public void setMessageReceived(String messageReceived) {
		this.messageReceived = messageReceived;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	public String getCreated() {

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date testDate = null;
		try {
			testDate = sdf.parse(messageReceived);
		}catch(Exception ex){
			ex.printStackTrace();
		}

		SimpleDateFormat formatter = new SimpleDateFormat("h:mm a");
		String time = formatter.format(testDate);

		SimpleDateFormat monthFormatter = new SimpleDateFormat("MMM dd");
		String month = monthFormatter.format(testDate);

		//		String Time=month+" at "+time;
		return time + "";

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
		tempDate=Utility.GetDifferenceTime(diff);

		if(tempDate.equalsIgnoreCase("")){
			SimpleDateFormat formatter1 = new SimpleDateFormat("dd-MM-yyyy");
			tempDate=formatter1.format(gmtTime);
		}

		return tempDate + "";
	}
}
