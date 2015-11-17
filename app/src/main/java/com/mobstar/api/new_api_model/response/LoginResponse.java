package com.mobstar.api.new_api_model.response;

import com.mobstar.api.new_api_model.Login;
import com.mobstar.api.responce.BaseResponse;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by lipcha on 17.11.15.
 */
public class LoginResponse extends BaseResponse{

    private Login login;

    @Override
    public void configure(JSONObject jsonObject) throws JSONException {
        super.configure(jsonObject);
        login = getGson().fromJson(jsonObject.toString(), Login.class);
    }
}
