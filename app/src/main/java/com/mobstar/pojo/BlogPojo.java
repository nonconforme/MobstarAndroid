package com.mobstar.pojo;

import java.io.Serializable;

public class BlogPojo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String ID="",BlogTitle="",BlogHeader="",BlogImage="",Description="",CreatedAt="";
	
	public String getID() {
		return ID;
	}

	public void setID(String iD) {
		ID = iD;
	}
	
	public String getBlogTitle() {
		return BlogTitle;
	}

	public void setBlogTitle(String blogTitle) {
		BlogTitle = blogTitle;
	}
	
	public String getBlogHeader() {
		return BlogHeader;
	}

	public void setBlogHeader(String blogHeader) {
		BlogHeader = blogHeader;
	}
	
	public String getBlogImage() {
		return BlogImage;
	}

	public void setBlogImage(String blogImage) {
		BlogImage = blogImage;
	}
	
	public String getDescription() {
		return Description;
	}

	public void setDescription(String des) {
		Description = des;
	}
	
	public String getCreatedAt() {
		return CreatedAt;
	}

	public void setCreatedAt(String date) {
		CreatedAt = date;
	}

}
