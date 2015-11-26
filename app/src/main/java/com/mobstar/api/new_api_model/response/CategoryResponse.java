package com.mobstar.api.new_api_model.response;

import com.google.gson.reflect.TypeToken;
import com.mobstar.api.new_api_model.Category;
import com.mobstar.api.responce.BaseResponse;
import org.json.JSONException;
import org.json.JSONObject;
import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Created by lipcha on 26.11.15.
 */
public class CategoryResponse extends BaseResponse {

    private ArrayList<Category> categories;

    @Override
    public void configure(JSONObject jsonObject) throws JSONException {
        super.configure(jsonObject);
        final Type listType = new TypeToken<ArrayList<Category>>() {
        }.getType();
        categories = getGson().fromJson(jsonObject.getJSONArray("jsonarr").toString(), listType);
    }

    public ArrayList<Category> getCategories() {
        return categories;
    }

    private void sortCategories(){

    }
}
