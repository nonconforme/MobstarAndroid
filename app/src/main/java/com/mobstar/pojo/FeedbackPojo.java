package com.mobstar.pojo;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import com.mobstar.utils.Utility;

public class FeedbackPojo {

	private String FeedbackId,EntryName,Thumbnail,LastComment;

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
		Calendar today = Calendar.getInstance();
		
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
		Date gmtTime = null;

		try {
			gmtTime = formatter.parse(LastComment);// catch exception

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

	public void setLastComment(String lastComment) {
		LastComment = lastComment;
	}
}
