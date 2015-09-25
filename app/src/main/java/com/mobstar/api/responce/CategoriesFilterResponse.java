package com.mobstar.api.responce;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Alexandr on 10.09.2015.
 */
public class CategoriesFilterResponse extends BaseResponse {

    public static final String KEY_CATEGORIES_FILTER = "categoryFilter";

    private ArrayList<Integer> choosenCategories;
//    private String error="";

    @Override
    public void configure(JSONObject jsonObject) throws JSONException {
        super.configure(jsonObject);
        JSONArray jsonArray;
        choosenCategories = new ArrayList<>();
        if (jsonObject.has(KEY_CATEGORIES_FILTER)) {
            jsonArray = jsonObject.getJSONArray(KEY_CATEGORIES_FILTER);
            if (jsonArray.length()>0) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    choosenCategories.add(jsonArray.getInt(i));
                }
            }
        }

//        if(jsonObject.has("error"))
//            error = jsonObject.getString("error");
//        if(jsonObject.has("errors"))
//            error = jsonObject.getString("errors");
    }

    public ArrayList<Integer> getChoosenCategories() {
        return choosenCategories;
    }

//    public String getError() {
//        return error;
//    }
//
//    public boolean hasError(){
//        return !error.isEmpty();
//    }
}
