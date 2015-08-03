package com.mobstar.pojo;

import java.io.Serializable;

public class CategoryPojo implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String ID,CategoryName,CategoryDescription;
	private boolean CategoryActive;
	
	public String getID() {
		return ID;
	}

	public void setID(String iD) {
		ID = iD;
	}
	
	public String getCategoryName() {
		return CategoryName;
	}

	public void setCategoryName(String categoryName) {
		CategoryName = categoryName;
	}
	
	public boolean getCategoryActive() {
		return CategoryActive;
	}

	public void setCategoryActive(boolean  categoryActive) {
		CategoryActive =categoryActive;
	}
	
	public String getCategoryDescription() {
		return CategoryDescription;
	}

	public void setCategoryDescription(String categoryDescription) {
		CategoryDescription =categoryDescription;
	}
	
}
