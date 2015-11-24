package com.mobstar.api.new_api_call;

import android.content.Context;

import com.mobstar.R;
import com.mobstar.api.ApiConstant;
import com.mobstar.api.ConnectCallback;
import com.mobstar.api.RestClient;
import com.mobstar.api.new_api_model.response.ProfileResponse;
import com.mobstar.api.new_api_model.response.StarsResponse;
import com.mobstar.api.new_api_model.response.SuccessResponse;
import com.mobstar.api.new_api_model.response.UserSettingsResponse;
import com.mobstar.api.responce.ContinentResponse;
import com.mobstar.pojo.ContinentsPojo;
import com.mobstar.utils.Utility;
import static com.mobstar.api.ApiConstant.*;

import java.util.HashMap;

/**
 * Created by lipcha on 19.11.15.
 */
public class ProfileCall {

    public static final void postUserContinent(final Context context, ContinentsPojo.Continents continents, ConnectCallback<SuccessResponse> connectCallback){
        Utility.ShowProgressDialog(context, context.getString(R.string.loading));
        final HashMap<String, String> params = new HashMap<>();
        params.put(ContinentResponse.KEY_CONTINENT, Integer.toString(continents.ordinal()));
        RestClient.getInstance(context).postRequest(ApiConstant.SETTING_USER_CONTINENT, params, connectCallback);
    }

    public static final void getUserProfile(final Context context, final String userId, final int page, final ConnectCallback<ProfileResponse> connectCallback){
        final HashMap<String, String> params = new HashMap<>();
        params.put("page", Integer.toString(page));
        RestClient.getInstance(context).getRequest(USER + userId + PROFILE, params, connectCallback);
    }

    public static final void getUserSettings(final Context context, final ConnectCallback<UserSettingsResponse> connectCallback){
        RestClient.getInstance(context).getRequest(USER_SETTINGS, null, connectCallback);

    }

    public static final void getUserStars(final Context context, final int page, final ConnectCallback<StarsResponse> connectCallback){
        final HashMap<String, String> params = new HashMap<>();
        params.put("page", Integer.toString(page));

    }

    public static final void getUserStaredBy(final Context context, final int page, final ConnectCallback<StarsResponse> connectCallback){
        final HashMap<String, String> params = new HashMap<>();
        params.put("page", Integer.toString(page));
        RestClient.getInstance(context).getRequest(USER_STARED_BY, params, connectCallback);
    }
}
