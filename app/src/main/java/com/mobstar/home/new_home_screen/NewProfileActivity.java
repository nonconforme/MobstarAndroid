package com.mobstar.home.new_home_screen;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.PersistableBundle;

import com.mobstar.BaseActivity;
import com.mobstar.R;
import com.mobstar.utils.Constant;
import com.mobstar.utils.Utility;

/**
 * Created by lipcha on 21.09.15.
 */
public class NewProfileActivity extends BaseActivity {

    private String UserID,EntryId, UserName = "", UserPic = "", IsMyStar = "", IAmStar= "", UserDisplayName = "", UserCoverImage = "", UserTagline = "",UserBio="", UserFan="";
    private boolean isNotfiedUser = false;
    private boolean isProfile = false;
    private SharedPreferences preferences;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        preferences = getSharedPreferences(Constant.MOBSTAR_PREF, Activity.MODE_PRIVATE);
        getBundleData();
        findViews();

        if (UserID.equals(preferences.getString("userid", "0"))) {
            Utility.SendDataToGA("UserProfile Screen", NewProfileActivity.this);
        } else {
            Utility.SendDataToGA("OtherProfile Screen", NewProfileActivity.this);
        }

    }

    private void findViews(){

    }

    private void getBundleData(){
        Bundle extras = getIntent().getExtras();
        if (extras != null) {

            if (extras.containsKey("EntryId")) {
                EntryId = extras.getString("EntryId");
                isNotfiedUser=true;

                UserID = preferences.getString("userid", "0");
            }
            if (extras.containsKey("UserID")) {
                UserID = extras.getString("UserID");
            }
            if (extras.containsKey("UserName")) {
                UserName = extras.getString("UserName");
            }
            if (extras.containsKey("UserPic")) {
                UserPic = extras.getString("UserPic");
            }
            if (extras.containsKey("IsMyStar")) {
                IsMyStar = extras.getString("IsMyStar");
            }
            if (extras.containsKey("UserDisplayName")) {
                UserDisplayName = extras.getString("UserDisplayName");
            }
            if (extras.containsKey("UserCoverImage")) {
                UserCoverImage = extras.getString("UserCoverImage");
            }
            if (extras.containsKey("UserTagline")) {
                UserTagline = extras.getString("UserTagline");
            }
            if (extras.containsKey("isProfile")) {
                isProfile = extras.getBoolean("isProfile", false);
            }

        }
    }
}
