package com.mobstar.api.responce;

import com.mobstar.pojo.EntryPojo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by lipcha on 09.10.15.
 */
public class VoteResponse extends BaseResponse {

    private String message;
    private ArrayList<EntryPojo> arrEntry;

    @Override
    public void configure(JSONObject jsonObject) throws JSONException {
        arrEntry = new ArrayList<>();
        if (jsonObject == null)
            return;
        if (jsonObject.has("notice"))
            message = jsonObject.getString("notice");
        if (jsonObject.has("entries")){
            final JSONArray entryJsonArray = jsonObject.getJSONArray("entries");
            for (int i = 0; i < entryJsonArray.length(); i++){
                final JSONObject entry = entryJsonArray.getJSONObject(i);
                if (entry.has("entry")){
                    final EntryPojo entryPojo = new EntryPojo();
                    entryPojo.configure(entry.getJSONObject("entry"));
                    if (entryPojo.getID() != null)
                        arrEntry.add(entryPojo);
                }

            }
        }
    }

    public String getMessage() {
        return message;
    }

    public ArrayList<EntryPojo> getArrEntry(){
        return arrEntry;
    }
}
