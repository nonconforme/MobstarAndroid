package com.mobstar.api.responce;

import com.mobstar.pojo.EntryPojo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by lipcha on 14.09.15.
 */
public class EntriesResponse extends BaseResponse {

    private ArrayList<EntryPojo> arrEntry;
    private String next;
    private String previous;

    public ArrayList<EntryPojo> getArrEntry() {
        return arrEntry;
    }

    public boolean hasNextPage() {
        return next != null && next.length() != 0;
    }

    @Override
    public void configure(JSONObject jsonObject) throws JSONException {
        super.configure(jsonObject);
        arrEntry = new ArrayList<>();
        if (jsonObject.has("entries")){
            final JSONArray entryJsonArray = jsonObject.getJSONArray("entries");
            for (int i = 0; i < entryJsonArray.length(); i++){
                final JSONObject entry = entryJsonArray.getJSONObject(i);
                if (entry.has("entry")){
                    final EntryPojo entryPojo = new EntryPojo();
                    entryPojo.configure(entry.getJSONObject("entry"));
                    if(entryPojo.getID() == null)
                        entryPojo.setID(" ");
                    if (entryPojo.getID() != null || entryPojo.getCategory().equalsIgnoreCase("onlyprofile"))
                        arrEntry.add(entryPojo);
                }

            }
        }else if (jsonObject.has("votes")){
            final JSONArray entryJsonArray = jsonObject.getJSONArray("votes");
            for (int i = 0; i < entryJsonArray.length(); i++){
                final JSONObject entry = entryJsonArray.getJSONObject(i);
                if (entry.has("vote")){
                    final JSONObject vote = entry.getJSONObject("vote");
                    if (vote.has("entry")){
                        final EntryPojo entryPojo = new EntryPojo();
                        entryPojo.configure(vote.getJSONObject("entry"));
                        if (entryPojo.getID() != null)
                            arrEntry.add(entryPojo);
                    }
                }

            }
        }

        if (jsonObject.has("users")){
            final JSONArray jsonArray = jsonObject.getJSONArray("users");
            for (int i = 0; i < jsonArray.length(); i++){
                final EntryPojo entryPojo = new EntryPojo();
                entryPojo.configure(jsonArray.getJSONObject(i));
                entryPojo.setCategory("onlyprofile");
                entryPojo.setID(" ");
                arrEntry.add(entryPojo);
            }
        }

        if (jsonObject.has("next"))
            next = jsonObject.getString("next");
        if (jsonObject.has("previous"))
            previous = jsonObject.getString("previous");

    }
}
