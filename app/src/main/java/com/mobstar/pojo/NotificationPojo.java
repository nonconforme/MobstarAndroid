package com.mobstar.pojo;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import com.mobstar.utils.Utility;

public class NotificationPojo {

	private String NotificationID, NotificationContent, NotificationDate, IsRead,NotificationType,EntryId,EntryName,NotificationIcon,MessageGroup,notificationRead;

	public String getNotificationRead() {
		return notificationRead;
	}

	public void setNotificationRead(String notificationRead) {
		this.notificationRead = notificationRead;
	}

	public String getEntryName() {
		return EntryName;
	}

	public void setEntryName(String entryName) {
		EntryName = entryName;
	}

	public String getNotificationID() {
		return NotificationID;
	}

	public void setNotificationID(String notificationID) {
		NotificationID = notificationID;
	}

	public String getMessageGroup() {
		return MessageGroup;
	}

	public void setMessageGroup(String messageGroup) {
		MessageGroup = messageGroup;
	}

	public String getNotificationContent() {
		return NotificationContent;
	}
	
	public void setNotificationIcon(String notificationIcon) {
		NotificationIcon = notificationIcon;
	}

	public String getNotificationIcon() {
		return NotificationIcon;
	}

	public void setNotificationContent(String notificationContent) {
		NotificationContent = notificationContent;
	}

	public String getNotificationDate() {
		String tempDate = null;
		Calendar today = Calendar.getInstance();

		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
		Date gmtTime = null;

		try {
			gmtTime = formatter.parse(NotificationDate);// catch exception

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Calendar thatDay = Calendar.getInstance();
		thatDay.setTime(gmtTime);

		long diff = (today.getTimeInMillis() - thatDay.getTimeInMillis()) / 1000;

		// Log.v(Constant.TAG, "Difference "+diff);

		tempDate=Utility.GetStringTime(diff);

		return tempDate + "";
	}

	public void setNotificationDate(String notificationDate) {
		NotificationDate = notificationDate;
	}

	public String getIsRead() {
		return IsRead;
	}

	public void setIsRead(String isRead) {
		IsRead = isRead;
	}

	public String getNotificationType() {
		return NotificationType;
	}

	public void setNotificationType(String notificationType) {
		NotificationType = notificationType;
	}
	
	public void setEntryId(String entryId){
		EntryId=entryId;
	}
	
	public String getEntryId(){
		return EntryId;
	}
}
