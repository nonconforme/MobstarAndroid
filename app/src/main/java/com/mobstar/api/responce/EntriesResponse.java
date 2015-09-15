package com.mobstar.api.responce;

import com.mobstar.pojo.EntryPojo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by lipcha on 14.09.15.
 */
public class EntriesResponse implements BaseResponse {

    private ArrayList<EntryPojo> arrEntry;

    public ArrayList<EntryPojo> getArrEntry() {
        return arrEntry;
    }

    @Override
    public void configure(JSONObject jsonObject) throws JSONException {
        arrEntry = new ArrayList<>();
        if (jsonObject.has("entries")){
            final JSONArray entryJsonArray = jsonObject.getJSONArray("entries");
            for (int i = 0; i < entryJsonArray.length(); i++){
                final JSONObject entry = entryJsonArray.getJSONObject(i);
                if (entry.has("entry")){
                    final EntryPojo entryPojo = new EntryPojo();
                    entryPojo.configure(entry.getJSONObject("entry"));
                    arrEntry.add(entryPojo);
                }

            }
        }

    }
}
