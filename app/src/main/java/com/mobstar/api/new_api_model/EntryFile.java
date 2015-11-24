package com.mobstar.api.new_api_model;

import com.mobstar.api.responce.BaseResponse;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by lipcha on 20.11.15.
 */
public class EntryFile {

    private String path;
    private String source;
    private String quality;

    public String getPath() {
        return path;
    }

    public String getSource() {
        return source;
    }

    public String getQuality() {
        return quality;
    }
}
