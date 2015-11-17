package com.mobstar.api.new_api_call;

import android.content.Context;

import com.mobstar.api.ConnectCallback;
import com.mobstar.api.RestClient;
import com.mobstar.api.new_api_model.SocialType;
import com.mobstar.api.new_api_model.response.LoginResponse;

import java.util.HashMap;
import static com.mobstar.api.ApiConstant.*;

/**
 * Created by lipcha on 17.11.15.
 */
public class LoginCall {

    public static final void signUpMail(
            final Context context,
            final String email,
            final String fullName,
            final String displayName,
            final String password,
            final String deviceToken,
            final ConnectCallback<LoginResponse> connectCallback){

        final HashMap<String, String> params = new HashMap<>();
        params.put("email", email);
        params.put("fullName", fullName);
        params.put("displayName", displayName);
        params.put("password", password);
        params.put("deviceToken", deviceToken);
        params.put("deviceType", GOOGLE_DEVICE_TYPE);
        RestClient.getInstance(context).postRequest(BASE_SERVER_URL + SIGN_UP, params, connectCallback);
    }

    public static final void signInMail(
            final Context context,
            final String email,
            final String password,
            final String deviceToken,
            final ConnectCallback connectCallback){
        final HashMap<String, String> params = new HashMap<>();
        params.put("email", email);
        params.put("password", password);
        params.put("deviceToken", deviceToken);
        params.put("deviceType", GOOGLE_DEVICE_TYPE);
        RestClient.getInstance(context).postRequest(BASE_SERVER_URL + SIGN_IN, params, connectCallback);
    }

    public static final void signSocial(
            final Context context,
            final String deviceToken,
            final String displayName,
            final String fullName,
            final String socialId,
            final SocialType socialType,
            final ConnectCallback<LoginResponse> connectCallback){
        final HashMap<String, String> params = new HashMap<>();
        params.put("deviceType", "google");
        params.put("deviceToken", deviceToken);
        params.put("displayName", displayName);
        params.put("fullName", fullName);
        params.put("socialId", socialId);
        params.put("socialType", socialType.toString());
        RestClient.getInstance(context).postRequest(BASE_SERVER_URL + SIGN_SOCIAL, params, connectCallback);
    }
}
