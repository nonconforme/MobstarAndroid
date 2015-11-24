package com.mobstar.api.new_api_model.response;

import com.mobstar.api.new_api_model.EntryP;
import com.mobstar.api.responce.BaseResponse;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by lipcha on 20.11.15.
 */
public class EntryResponse extends BaseResponse {

    private long timestamp;
    private ArrayList<EntryP> entries;
    private boolean next;

    private void  fromEntryResponse(final EntryResponse entryResponse){
        timestamp = entryResponse.getTimestamp();
        entries = entryResponse.getEntries();
        next = entryResponse.hasNextPage();
    }

    @Override
    public void configure(JSONObject jsonObject) throws JSONException {
        super.configure(jsonObject);
        fromEntryResponse(getGson().fromJson(jsonObject.toString(), EntryResponse.class));
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public ArrayList<EntryP> getEntries() {
        return entries;
    }

    public void setEntries(ArrayList<EntryP> entries) {
        this.entries = entries;
    }

    public boolean hasNextPage() {
        return next;
    }

    public void setNext(boolean next) {
        this.next = next;
    }
}
