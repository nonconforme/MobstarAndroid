package com.mobstar.api.new_api_call;

import android.content.Context;

import com.mobstar.api.ConnectCallback;
import com.mobstar.api.RestClient;
import com.mobstar.api.new_api_model.response.EntryResponse;
import com.mobstar.api.new_api_model.response.EntrySingleResponse;

import java.util.HashMap;

import static com.mobstar.api.ApiConstant.*;

/**
 * Created by lipcha on 20.11.15.
 */
public class EntryCall {

    public static final void getEntry(final Context context, final HashMap<String, String> params, ConnectCallback<EntryResponse> connectCallback){
        RestClient.getInstance(context).getRequest(GET_ENTRY, params, connectCallback);
    }

    public static final void voteUp(final Context context, final String entryId, final ConnectCallback<EntrySingleResponse> connectCallback){
        RestClient.getInstance(context).postRequest(VOTE_UP + entryId, null, connectCallback);
    }

    public static final void voteDown(final Context context, final String entryId, final ConnectCallback<EntrySingleResponse> connectCallback){
        RestClient.getInstance(context).postRequest(VOTE_DOWN + entryId, null, connectCallback);
    }

    public static final void updateViewCounts(final Context context, final String entryId, final ConnectCallback<EntrySingleResponse> connectCallback){
        RestClient.getInstance(context).postRequest(UPDATE_VIEW_COUNTS + entryId, null, connectCallback);
    }

}
