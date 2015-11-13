package com.mobstar.api.responce;

import com.mobstar.R;
import com.mobstar.pojo.CategoryPojo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by lipcha on 13.10.15.
 */
public class CategoryResponse extends BaseResponse {

    private ArrayList<CategoryPojo> categoryPojos;

    @Override
    public void configure(JSONObject jsonObject) throws JSONException {
        super.configure(jsonObject);
        categoryPojos = new ArrayList<>();
        if (jsonObject == null)
        return;


        if (jsonObject.has("categories")) {

            JSONArray jsonArrayCategories;
            jsonArrayCategories = jsonObject.getJSONArray("categories");

            for (int i = 0; i < jsonArrayCategories.length(); i++) {

                JSONObject jsonObj = jsonArrayCategories.getJSONObject(i);

                if (jsonObj.has("category")) {
                    JSONObject jsonObjCategory = jsonObj.getJSONObject("category");
                    CategoryPojo categoryPojo = new CategoryPojo();
                    categoryPojo.setID(jsonObjCategory.getString("id"));
                    categoryPojo.setCategoryActive(jsonObjCategory.getBoolean("categoryActive"));
                    categoryPojo.setCategoryName(jsonObjCategory.getString("categoryName"));
                    categoryPojo.setCategoryDescription(jsonObjCategory.getString("categoryDescription"));
                    categoryPojos.add(categoryPojo);
                }
            }

        }

    }

    public ArrayList<CategoryPojo> getCategoryPojos() {
        return categoryPojos;
    }
}
