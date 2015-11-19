package com.mobstar.api.new_api_call;

import android.content.Context;

import com.mobstar.R;
import com.mobstar.api.ApiConstant;
import com.mobstar.api.ConnectCallback;
import com.mobstar.api.RestClient;
import com.mobstar.api.new_api_model.response.SuccessResponse;
import com.mobstar.api.responce.ContinentResponse;
import com.mobstar.pojo.ContinentsPojo;
import com.mobstar.utils.Utility;

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
}
