package com.mobstar.api.new_api_model;

import java.io.Serializable;

/**
 * Created by lipcha on 20.11.15.
 */
public class EntryP  implements Serializable{

    private Entry entry;
    private Profile user;

    public Entry getEntry() {
        return entry;
    }

    public Profile getUser() {
        return user;
    }

    public EntryFile getEntryFile(int index){
        return entry.getFiles().get(index);
    }

    public String getType(){
        return getEntryFile(0).getType();
    }
}
