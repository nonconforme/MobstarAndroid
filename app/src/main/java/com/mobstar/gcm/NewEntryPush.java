package com.mobstar.gcm;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Alexandr on 24.10.2015.
 */
public class NewEntryPush implements Serializable {
    private int id;
    private int category;
    private int continent;
    private long timeUpload;
    private int userId;
//    "continent":2,"timeUpload":123456789,"id":7865,"category":1


    public static ArrayList<NewEntryPush> getList(String jsonArrayEnry) {
        ArrayList<NewEntryPush> arrayList = new ArrayList<>();
        JSONObject object;
        try {
            JSONArray arrayEntry = new JSONArray(jsonArrayEnry);
            for (int i = 0; i<arrayEntry.length(); i++){
                object = new JSONObject(arrayEntry.get(i).toString());
                NewEntryPush newEntryPush = new NewEntryPush();
                newEntryPush.id = object.getInt("id");
                newEntryPush.userId = object.getInt("userId");
                newEntryPush.category = object.getInt("category");
                newEntryPush.continent = object.getInt("continent");
                newEntryPush.timeUpload = object.getLong("timeUpload");
                arrayList.add(newEntryPush);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


        return arrayList;
    }

    public int getId() {
        return id;
    }

    public int getCategory() {
        return category;
    }

    public int getContinent() {
        return continent;
    }

    public long getTimeUpload() {
        return timeUpload;
    }

    public int getUserId() {
        return userId;
    }
}
