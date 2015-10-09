package com.mobstar.login;

import com.facebook.model.GraphUser;

/**
 * Created by Alexandr on 02.10.2015.
 */
public class FbAccount {
    public String id ="";
    public String email ="";
    public String name ="";
    public String birthday ="";
    public String gender ="";
    public String firstName ="";
    public FbAccount(GraphUser user) {
        if (user.getId()!=null) id = user.getId();
        if (user.getProperty("email").toString()!=null) email = user.getProperty("email").toString();
        if (user.getName()!=null) name = user.getName();
        if (user.getBirthday()!=null) birthday = user.getBirthday();
        if (user.getProperty("gender").toString()!=null) gender =  user.getProperty("gender").toString();
        if (user.getFirstName()!=null) firstName = user.getFirstName();

    }
}
