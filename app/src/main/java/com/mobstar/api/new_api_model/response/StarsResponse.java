package com.mobstar.api.new_api_model.response;

import com.mobstar.api.new_api_model.Star;
import com.mobstar.api.responce.BaseResponse;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by lipcha on 24.11.15.
 */
public class StarsResponse extends BaseResponse {

    private ArrayList<Star> stars;
    private boolean next;

    private void fromThis(StarsResponse starsResponse){
        stars = starsResponse.getStars();
        next = starsResponse.isNext();
    }

    @Override
    public void configure(JSONObject jsonObject) throws JSONException {
        super.configure(jsonObject);
        fromThis(fromJson(jsonObject, StarsResponse.class));
    }

    public ArrayList<Star> getStars() {
        return stars;
    }

    public boolean isNext() {
        return next;
    }
}
