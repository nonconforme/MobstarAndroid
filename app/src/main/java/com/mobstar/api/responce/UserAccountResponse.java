package com.mobstar.api.responce;

import com.mobstar.api.model.User;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by lipcha on 09.09.15.
 */
public class UserAccountResponse extends BaseResponse {

    private User user;

    @Override
    public void configure(JSONObject jsonObject) throws JSONException {
        super.configure(jsonObject);
        if (jsonObject.has("user")){
            final JSONObject userObject = jsonObject.getJSONObject("user");
            user = new User();
            user.configure(userObject);
        }
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}

