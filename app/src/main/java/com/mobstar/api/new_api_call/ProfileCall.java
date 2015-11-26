package com.mobstar.api.new_api_call;

import android.content.Context;

import com.mobstar.R;
import com.mobstar.api.ApiConstant;
import com.mobstar.api.ConnectCallback;
import com.mobstar.api.RestClient;
import com.mobstar.api.new_api_model.response.CategoryResponse;
import com.mobstar.api.new_api_model.response.ProfileResponse;
import com.mobstar.api.new_api_model.response.StarsResponse;
import com.mobstar.api.new_api_model.response.SuccessResponse;
import com.mobstar.api.new_api_model.response.UserSettingsResponse;
import com.mobstar.api.new_api_model.response.WhoToFollowResponse;
import com.mobstar.api.responce.ContinentResponse;
import com.mobstar.pojo.ContinentsPojo;
import com.mobstar.utils.Utility;
import static com.mobstar.api.ApiConstant.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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

    public static final void followUsers(final Context context, final String strsArr, final ConnectCallback<SuccessResponse> connectCallback){
        final HashMap<String, String> params = new HashMap<>();
        params.put("stars" , strsArr);
        RestClient.getInstance(context).postRequest(USER_FOLLOW, params, connectCallback);

    }

    public static final void getWhoToFollowUsers(final Context context, final ConnectCallback<WhoToFollowResponse> connectCallback){
        RestClient.getInstance(context).getRequest(WHO_TO_FOLLOW, null, connectCallback);
    }

    public static final void follow(final Context context, final String userId, ConnectCallback<SuccessResponse> connectCallback){
        RestClient.getInstance(context).postRequest(FOLLOW + userId, null, connectCallback);
    }

    public static final void unFollow(final Context context, final String userId, final ConnectCallback<SuccessResponse> connectCallback){
        RestClient.getInstance(context).postRequest(UNFOLLOW + userId, null, connectCallback);
    }

    public static final void getCategories(final Context context, final ConnectCallback<CategoryResponse> connectCallback){
        RestClient.getInstance(context).getRequest(CATEGORIES, null, connectCallback);
    }

    public static final void setUserFilters(final Context context, final List<Integer> continentFilter, final List<Integer> categoryFilter, final ConnectCallback<SuccessResponse> connectCallback){
        final HashMap<String, String> params = new HashMap<>();
        if (continentFilter != null && continentFilter.size() > 0)
            params.put("continentFilter", toString(continentFilter));
        if (categoryFilter != null && categoryFilter.size() > 0)
            params.put("categoryFilter", toString(categoryFilter));
        RestClient.getInstance(context).putRequest(USER_SETTINGS, params, connectCallback);

    }

    private static final String toString(final List<Integer> intList){
        String str = "";
        for (int i = 0; i < intList.size(); i ++){
            if (str.equals(""))
                str = Integer.toString(intList.get(i));
            else str = str + "," + intList.get(i);
        }
        return str;
    }
}
