package com.mobstar.pojo;

import com.mobstar.R;

/**
 * Created by Alexandr on 08.09.2015.
 */
public class ContinentsPojo {
    private int imageResurse;
    public Continents continent;
    private int code;
    private String name;


    public ContinentsPojo(String[] all, int position) {
        this.code = position;
        this.name = all[code];
        switch (code){
            case 0:
                this.continent = Continents.ALL_WORLD;
                this.imageResurse = R.drawable.ic_back;
        }
    }

    public enum Continents {
        ALL_WORLD ,AFRICA, ASIA, EUROPE, SUOTH_AMERICA, OCEANIA,NORTH_AMERICA
    }
}
