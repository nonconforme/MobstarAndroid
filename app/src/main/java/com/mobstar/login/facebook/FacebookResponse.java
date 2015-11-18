package com.mobstar.login.facebook;

/**
 * Created by lipcha on 18.11.15.
 */
public class FacebookResponse {

    private String id;
    private String birthday;
    private String gender;
    private String email;
    private String name;

    public String getFacebookImageUrl() {
        return  "http://graph.facebook.com/" + id + "/picture?type=large";
    }

    public String getId() {
        return id;
    }

    public String getBirthday() {
        return birthday;
    }

    public String getGender() {
        return gender;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

}
