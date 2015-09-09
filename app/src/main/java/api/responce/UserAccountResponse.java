package api.responce;

import org.json.JSONException;
import org.json.JSONObject;

import api.model.User;

/**
 * Created by lipcha on 09.09.15.
 */
public class UserAccountResponse implements BaseResponse {

    private User user;

    @Override
    public void configure(JSONObject jsonObject) throws JSONException {
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

