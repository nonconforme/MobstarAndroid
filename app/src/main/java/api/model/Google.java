package api.model;

import org.json.JSONException;
import org.json.JSONObject;

import api.responce.BaseResponse;

/**
 * Created by lipcha on 09.09.15.
 */
public class Google  implements BaseResponse{

    private String displayName;
    private int id;
    private String userName;
    private String fullname;

    @Override
    public void configure(JSONObject jsonObject) throws JSONException {
        if (jsonObject.has("displayName"))
            displayName = jsonObject.getString("displayName");
        if (jsonObject.has("id"))
            id = jsonObject.getInt("id");
        if (jsonObject.has("userName"))
            userName = jsonObject.getString("userName");
        if (jsonObject.has("fullName"))
            fullname = jsonObject.getString("fullName");
    }
}
