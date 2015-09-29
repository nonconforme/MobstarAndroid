package com.mobstar.api.responce;

import com.mobstar.pojo.ContinentsPojo;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by lipcha on 09.09.15.
 */
public class ContinentResponse extends BaseResponse {

    public static final String KEY_CONTINENT = "userContinent";

    private int continentCode;
//    private String error;

    @Override
    public void configure(JSONObject jsonObject) throws JSONException {
        super.configure(jsonObject);
        if (jsonObject.has(KEY_CONTINENT))
            continentCode = jsonObject.getInt(KEY_CONTINENT);
//        if(jsonObject.has("error"))
//            error = jsonObject.getString("error");
//        if(jsonObject.has("errors"))
//            error = jsonObject.getString("errors");
    }

    public int getContinentCode(){
        return continentCode;
    }

    public ContinentsPojo.Continents getContinent(){

        ContinentsPojo.Continents continents = null;
        switch (continentCode){
            case 0:
                continents = ContinentsPojo.Continents.ALL_WORLD;
                break;
            case 1:
                continents = ContinentsPojo.Continents.AFRICA;
                break;
            case 2:
                continents = ContinentsPojo.Continents.ASIA;
                break;
            case 3:
                continents = ContinentsPojo.Continents.EUROPE;
                break;
            case 4:
                continents = ContinentsPojo.Continents.SOUTH_AMERICA;
                break;
            case 5:
                continents = ContinentsPojo.Continents.OCEANIA;
                break;
            default:
                continents = ContinentsPojo.Continents.NORTH_AMERICA;
                break;

        }
        return continents;
    }

}
