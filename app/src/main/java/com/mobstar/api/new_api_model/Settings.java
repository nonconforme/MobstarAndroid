package com.mobstar.api.new_api_model;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by lipcha on 17.11.15.
 */
public class Settings {
    private String continent;
    private String email;
    private String continentFilter;
    private String categoryFilter;

    private ArrayList<Integer> listSelectedContinents;
    private ArrayList<Integer> listSelectedCategories;

    public String getContinent() {
        return continent;
    }

    public String getEmail() {
        return email;
    }

    public ArrayList<Integer> getCategoryFilter() {
        if (listSelectedCategories == null)
            listSelectedCategories = getSelectedCategories();
        return listSelectedCategories;
    }

    public ArrayList<Integer> getContinentFilter() {
        if (listSelectedContinents == null)
            listSelectedContinents = getSelectedContinent();
        return listSelectedContinents;
    }

    private ArrayList<Integer> getSelectedCategories(){
        ArrayList<Integer> categories = new ArrayList<>();
        if (categoryFilter != null && !categoryFilter.equals("")){
            categories = new ArrayList<>(Arrays.asList(toInteger(categoryFilter.split(","))));
        }
        return categories;
    }

    private ArrayList<Integer> getSelectedContinent(){
        ArrayList<Integer> continents = new ArrayList<>();
        if (continentFilter != null && !continentFilter.equals("")){
            continents = new ArrayList<>(Arrays.asList(toInteger(continentFilter.split(","))));
        }
        return continents;
    }

    private Integer[] toInteger(final String[] str){
        final Integer[] ints = new Integer[str.length];
        for (int i = 0; i < str.length; i ++){
            ints[i] = Integer.parseInt(str[i]);
        }
        return ints;
    }
}
