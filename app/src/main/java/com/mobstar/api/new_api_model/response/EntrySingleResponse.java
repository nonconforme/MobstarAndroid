package com.mobstar.api.new_api_model.response;

import com.mobstar.api.new_api_model.EntryP;
import com.mobstar.api.responce.BaseResponse;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by lipcha on 26.11.15.
 */
public class EntrySingleResponse extends BaseResponse {

    private EntryP entry;

    @Override
    public void configure(JSONObject jsonObject) throws JSONException {
        super.configure(jsonObject);
        entry = fromJson(jsonObject, EntryP.class);
    }

    public EntryP getEntry() {
        return entry;
    }
}
