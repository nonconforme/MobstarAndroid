package com.mobstar.api.new_api_call;

import android.content.Context;

import com.mobstar.api.ConnectCallback;
import com.mobstar.api.RestClient;
import com.mobstar.api.new_api_model.response.EntryResponse;
import java.util.HashMap;

import static com.mobstar.api.ApiConstant.*;

/**
 * Created by lipcha on 20.11.15.
 */
public class EntryCall {
    public static final void getEntry(final Context context, final HashMap<String, String> params, ConnectCallback<EntryResponse> connectCallback){
        RestClient.getInstance(context).getRequest(GET_ENTRY, params, connectCallback);

    }

}
