package com.mobstar.api.new_api_call;

import com.mobstar.api.new_api_model.response.EntryResponse;
import com.mobstar.api.responce.Error;

/**
 * Created by lipcha on 20.11.15.
 */
public class EntryCall {

    public static final void getEntry(){

    }

    public interface EntryCallback{
        void onEntrySuccess(final EntryResponse entryResponse);
        void onFaillure();
        void onServerError(Error error);
    }

}
