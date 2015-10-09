package com.mobstar.api.responce;

import com.mobstar.home.new_home_screen.profile.UserProfile;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by lipcha on 08.10.15.
 */
public class UserProfileResponse extends BaseResponse {

    private ArrayList<UserProfile> userProfiles;

    public ArrayList<UserProfile> getUserProfile(){
        return userProfiles;
    }

    @Override
    public void configure(JSONObject jsonObject) throws JSONException {
        userProfiles = new ArrayList<>();
        if (jsonObject.has("users")){
            final JSONArray users = jsonObject.getJSONArray("users");
            for(int i = 0; i < users.length(); i++){
                final UserProfile user = UserProfile.newBuilder().build();
                user.configure(users.getJSONObject(i));
                userProfiles.add(user);
            }
        }

    }
}
