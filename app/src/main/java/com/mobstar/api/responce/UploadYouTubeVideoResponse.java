package com.mobstar.api.responce;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by lipcha on 04.11.15.
 */
public class UploadYouTubeVideoResponse extends BaseResponse {

    private String entryId;

    @Override
    public void configure(JSONObject jsonObject) throws JSONException {
        if (jsonObject.has("entry_id"))
            entryId = jsonObject.getString("entry_id");
    }

    public void setEntryId(String entryId) {
        this.entryId = entryId;
    }
}
