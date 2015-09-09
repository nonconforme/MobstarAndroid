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
                break;
            case 1:
                this.continent = Continents.AFRICA;
                this.imageResurse = R.drawable.africa;
                break;
            case 2:
                this.continent = Continents.ASIA;
                this.imageResurse = R.drawable.asia;
                break;
            case 3:
                this.continent = Continents.EUROPE;
                this.imageResurse = R.drawable.europe;
                break;
            case 4:
                this.continent = Continents.NORTH_AMERICA;
                this.imageResurse = R.drawable.north_america;
                break;
            case 5:
                this.continent = Continents.OCEANIA;
                this.imageResurse = R.drawable.oceania;
                break;
            case 6:
                this.continent = Continents.SOUTH_AMERICA;
                this.imageResurse = R.drawable.south_america;
                break;
        }
    }

    public int getImageResurse() {
        return imageResurse;
    }

    public Continents getContinent() {
        return continent;
    }

    public int getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public enum Continents {
        ALL_WORLD ,AFRICA, ASIA, EUROPE, SOUTH_AMERICA, OCEANIA,NORTH_AMERICA
    }
}
