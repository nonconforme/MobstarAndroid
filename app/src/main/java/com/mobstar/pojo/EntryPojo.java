package com.mobstar.pojo;

import com.mobstar.api.responce.BaseResponse;
import com.mobstar.utils.TimeUtility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

public class EntryPojo extends BaseResponse implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String ID, UserID, TotalComments,TotalViews, Category, Type, Name, Description, Created, Modified, VideoLink, ImageLink, AudioLink, UpVotesCount, DownvotesCount, Rank, Language, Deleted, Filetype;
	private String ProfileImage, IsMyStar,IAmStar,UserName, UserDisplayName, ProfileCover, Tagline,isVotedByYou="",Bio,SubCategory="",Height="",Age="";
	private ArrayList<String> arrayTags = new ArrayList<String>();
	private String splitVideoId;

	private String VideoThumb;
	private boolean isFeed=true;

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
        String tempDate = TimeUtility.getStringTime(TimeUtility.getDiffTime(Created));
        return tempDate + "";

//		String tempDate = null;
//		Calendar today = Calendar.getInstance();
//
//		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//		formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
//		Date gmtTime = null;
//
//		try {
//			gmtTime = formatter.parse(Created);// catch exception
//
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//
//		if (gmtTime != null) {
//			Calendar thatDay = Calendar.getInstance();
//			thatDay.setTime(gmtTime);
//
//
//			long diff = (today.getTimeInMillis() - thatDay.getTimeInMillis()) / 1000;
//
//
//			tempDate = Utility.GetStringTime(diff);
//		}
//		return tempDate + "";
	}

    public String getCreatedString () {
        return Created;
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
	
	public void setIsFeed(boolean isFeeds){
		isFeed=isFeeds;
	}
	
	public boolean getIsFeed(){
		return isFeed;
	}
	
	public void setSubCategry(String subCategory){
		SubCategory=subCategory;
	}
	
	public String getSubCategory(){
		return SubCategory;
	}
	
	public void setHeight(String height){
		Height=height;
	}
	
	public String getHeight(){
		return Height;
	}
	
	public void setAge(String age){
		Age=age;
	}
	
	public String getAge(){
		return Age;
	}

	public String getSplitVideoId() {
		return splitVideoId;
	}

	public void setSplitVideoId(String splitVideoId) {
		this.splitVideoId = splitVideoId;
	}

	@Override
	public void configure(JSONObject jsonObject) throws JSONException {

		if (jsonObject.has("user")) {
			JSONObject jsonObjUser = jsonObject.getJSONObject("user");
			if (jsonObjUser.has("id"))
				setUserID(jsonObjUser.getString("id"));
			if(jsonObjUser.has("userName"))
				setUserName(jsonObjUser.getString("userName"));
			if(jsonObjUser.has("displayName"))
				setUserDisplayName(jsonObjUser.getString("displayName"));
			if(jsonObjUser.has("profileImage"))
				setProfileImage(jsonObjUser.getString("profileImage"));
			if(jsonObjUser.has("profileCover"))
				setProfileCover(jsonObjUser.getString("profileCover"));
			if(jsonObjUser.has("tagLine"))
				setTagline(jsonObjUser.getString("tagLine"));
			if (jsonObjUser.has("isMyStar")) {
				setIsMyStar(jsonObjUser.getString("isMyStar"));
			}
			if(jsonObjUser.has("iAmStar")){
				setIAmStar(jsonObjUser.getString("iAmStar"));
			}

		}

		if (jsonObject.has("userId"))
			setUserID(jsonObject.getString("userId"));
		if (jsonObject.has("userName"))
			setUserName(jsonObject.getString("userName"));
		if (jsonObject.has("profileImage"))
			setProfileImage(jsonObject.getString("profileImage"));
		if (jsonObject.has("profileCover"))
			setProfileCover(jsonObject.getString("profileCover"));



		if (jsonObject.has("splitVideoId"))
			setSplitVideoId(jsonObject.getString("splitVideoId"));

		if (jsonObject.has("id"))
			setID(jsonObject.getString("id"));

		if(jsonObject.has("subcategory"))
			setSubCategry(jsonObject.getString("subcategory"));


		if(jsonObject.has("age"))
			setAge(jsonObject.getString("age"));


		if(jsonObject.has("height"))
			setHeight(jsonObject.getString("height"));

		if (jsonObject.has("category"))
			setCategory(jsonObject.getString("category"));
		if (jsonObject.has("type"))
			setType(jsonObject.getString("type"));
		if (jsonObject.has("name"))
			setName(jsonObject.getString("name"));
		if (jsonObject.has("description"))
			setDescription(jsonObject.getString("description"));
		if (jsonObject.has("created"))
			setCreated(jsonObject.getString("created"));
		if (jsonObject.has("modified"))
			setModified(jsonObject.getString("modified"));
		if (jsonObject.has("upVotes"))
			setUpVotesCount(jsonObject.getString("upVotes"));
		if (jsonObject.has("downVotes"))
			setDownvotesCount(jsonObject.getString("downVotes"));
		if (jsonObject.has("rank"))
			setRank(jsonObject.getString("rank"));
		if (jsonObject.has("language"))
			setLanguage(jsonObject.getString("language"));
		if (jsonObject.has("deleted"))
			setDeleted(jsonObject.getString("deleted"));
		if (jsonObject.has("totalComments"))
			setTotalComments(jsonObject.getString("totalComments"));

		if(jsonObject.has("totalviews")){
			setTotalViews(jsonObject.getString("totalviews"));
		}


		if (jsonObject.has("videoThumb")) {
			setVideoThumb(jsonObject.getString("videoThumb"));
		}
		if (jsonObject.has("tags")) {
			JSONArray jsonArrayTags = jsonObject.getJSONArray("tags");
			for (int j = 0; j < jsonArrayTags.length(); j++) {
				addTags(jsonArrayTags.getString(j));
			}
		}

		if (!jsonObject.has("entryFiles")) {
			// Log.v(Constant.TAG,
			// "entryFiles not exist in ID " +
			// entryPojo.getID());
		} else {
			final JSONArray jsonArrayFiles = jsonObject.getJSONArray("entryFiles");
			for (int j = 0; j < jsonArrayFiles.length(); j++) {
				JSONObject jsonObjFile = jsonArrayFiles.getJSONObject(j);

				if (getType().equalsIgnoreCase("image")) {
					if (jsonObjFile.has("filePath"))
						setImageLink(jsonObjFile.getString("filePath"));
					if (jsonObjFile.has("fileType"))
					setFiletype(jsonObjFile.getString("fileType"));

					// Log.v(Constant.TAG,
					// "Image "+jsonObjFile.getString("filePath"));
				} else if (getType().equalsIgnoreCase("audio")) {
					if (j == 0) {
						if (jsonObjFile.has("filePath"))
							setAudioLink(jsonObjFile.getString("filePath"));
						if (jsonObjFile.has("fileType"))
							setFiletype(jsonObjFile.getString("fileType"));
					} else if (j == 1) {

						setImageLink(jsonObjFile.getString("filePath"));
						setFiletype(jsonObjFile.getString("fileType"));
					}
				} else if (getType().equalsIgnoreCase("video")) {
					setVideoLink(jsonObjFile.getString("filePath"));
					setFiletype(jsonObjFile.getString("fileType"));
				}
			}

			// arrEntryPojos.add(entryPojo);
//			arrEntryPojosParent.add(entryPojo);
		}
	}
}
