package com.mobstar.api.new_api_call;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.os.Build;

import com.mobstar.api.ApiConstant;
import com.mobstar.api.ConnectCallback;
import com.mobstar.api.RestClient;
import com.mobstar.api.new_api_model.SocialType;
import com.mobstar.api.new_api_model.response.DefaultNotificationResponse;
import com.mobstar.api.new_api_model.response.LoginResponse;
import com.mobstar.api.new_api_model.response.SuccessResponse;
import com.mobstar.api.new_api_model.response.WelcomeVideoResponse;
import com.mobstar.utils.Utility;

import java.util.HashMap;
import java.util.List;

import static com.mobstar.api.ApiConstant.*;

/**
 * Created by lipcha on 17.11.15.
 */
public class AuthCall {

    public static final void signUpMail(
            final Context context,
            final String email,
            final String fullName,
            final String displayName,
            final String password,
            final ConnectCallback<LoginResponse> connectCallback){

        final HashMap<String, String> params = new HashMap<>();
        params.put("email", email);
        params.put("fullName", fullName);
        params.put("displayName", displayName);
        params.put("password", password);
        params.put("deviceToken", Utility.getRegistrationId(context));
        params.put("deviceType", GOOGLE_DEVICE_TYPE);
        RestClient.getInstance(context).postRequest(SIGN_UP, params, connectCallback);
    }

    public static final void signInMail(
            final Context context,
            final String email,
            final String password,
            final ConnectCallback<LoginResponse> connectCallback){
        final HashMap<String, String> params = new HashMap<>();
        params.put("email", email);
        params.put("password", password);
        params.put("deviceToken", Utility.getRegistrationId(context));
        params.put("deviceType", GOOGLE_DEVICE_TYPE);
        RestClient.getInstance(context).postRequest(SIGN_IN, params, connectCallback);
    }

    public static final void signSocial(
            final Context context,
            final String displayName,
            final String fullName,
            final String socialId,
            final SocialType socialType,
            final ConnectCallback<LoginResponse> connectCallback){
        final HashMap<String, String> params = new HashMap<>();
        params.put("deviceType", "google");
        params.put("deviceToken", Utility.getRegistrationId(context));
        params.put("displayName", displayName);
        params.put("fullName", fullName);
        params.put("socialId", socialId);
        params.put("socialType", socialType.toString());
        RestClient.getInstance(context).postRequest(SIGN_SOCIAL, params, connectCallback);
    }


    public static final void getWelcomeVideo(final Context context, final ConnectCallback<WelcomeVideoResponse> connectCallback){
        RestClient.getInstance(context).getRequest(ApiConstant.WELCOME, null, connectCallback);
    }

    public static final void postForgotPassword(final Context context, final String email, final ConnectCallback<SuccessResponse> connectCallback){
        final HashMap<String, String> params = new HashMap<>();
        params.put("email", email);
        RestClient.getInstance(context).postRequest(FORGOT_PASSWORD, params, connectCallback);
    }

    public static final void sendUserAnalytic(final Context context, final ConnectCallback<SuccessResponse> connectCallback){
        final String packageToCheck = "com.mobstar";
        int versionCode = 0;
        final String deviceName = Utility.getDeviceName();

        final List<PackageInfo> packages = context.getPackageManager().getInstalledPackages(0);
        for (int i=0; i<packages.size(); i++) {
            PackageInfo p = packages.get(i);
            if (p.packageName.contains(packageToCheck)) {
                versionCode = p.versionCode;
            }
        }

        final HashMap<String, String> params = new HashMap<>();
        params.put("deviceName", deviceName);
        params.put("platform", "Android");
        params.put("osVersion", Build.VERSION.RELEASE);
        params.put("appVersion", Integer.toString(versionCode));
        RestClient.getInstance(context).postRequest(USER_ANALYTIC, params, connectCallback);
    }


}
