package com.mobstar.api.responce;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Alexandr on 09.09.2015.
 */
public class ContinentFilterResponse extends BaseResponse {

    public static final String KEY_CONTINENT_FILTER = "continentFilter";

    private ArrayList<Integer> choosenContinents;
//    private String error="";

    @Override
    public void configure(JSONObject jsonObject) throws JSONException {
        super.configure(jsonObject);
        JSONArray jsonArray;
        choosenContinents = new ArrayList<>();
        if (jsonObject.has(KEY_CONTINENT_FILTER)) {
            jsonArray = jsonObject.getJSONArray(KEY_CONTINENT_FILTER);
            if (jsonArray.length()>0) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    choosenContinents.add(jsonArray.getInt(i));
                }
            }
        }
//
//        if(jsonObject.has("error"))
//            error = jsonObject.getString("error");
//        if(jsonObject.has("errors"))
//            error = jsonObject.getString("errors");
    }

    public ArrayList<Integer> getChoosenContinents() {
        return choosenContinents;
    }

//    public String getError() {
//        return error;
//    }
//
//    public boolean hasError(){
//        return !error.isEmpty();
//    }
}
