package com.mobstar.home.new_home_screen.profile;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.mobstar.BaseActivity;
import com.mobstar.EditProfileActivity;
import com.mobstar.R;
import com.mobstar.api.ConnectCallback;
import com.mobstar.api.RestClient;
import com.mobstar.api.responce.StarResponse;
import com.mobstar.utils.Constant;
import com.mobstar.utils.Utility;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by lipcha on 21.09.15.
 */
public class NewProfileActivity extends BaseActivity implements View.OnClickListener {

    private String UserID,EntryId, UserName = "", UserPic = "", IsMyStar = "", IAmStar= "", UserDisplayName = "", UserCoverImage = "", UserTagline = "",UserBio="", UserFan="";
    private boolean isNotfiedUser = false;
    private boolean isProfile = false;
    private SharedPreferences preferences;
    private TextView textUserName;
    private TextView imgFollow;
    private ImageView imgMsg;
    private FrameLayout fragmentContainer;
    private TextView btnEdit;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_activity_profile);
        preferences = getSharedPreferences(Constant.MOBSTAR_PREF, Activity.MODE_PRIVATE);
        getBundleData();
        findViews();
        setupViews();
        setListeners();
        addProfileListFragment();
        if (UserID.equals(preferences.getString("userid", "0"))) {
            Utility.SendDataToGA("UserProfile Screen", NewProfileActivity.this);
        } else {
            Utility.SendDataToGA("OtherProfile Screen", NewProfileActivity.this);
        }



    }

    private void findViews(){
        textUserName = (TextView) findViewById(R.id.textUserName);
        imgFollow = (TextView) findViewById(R.id.imgFollow);
        imgMsg = (ImageView) findViewById(R.id.imgMsg);
        btnEdit = (TextView) findViewById(R.id.btnEdit);
        fragmentContainer = (FrameLayout) findViewById(R.id.fragmentContainer);
    }

    private void setupViews(){
        textUserName.setText(UserName);

        if (UserID.equals(preferences.getString("userid", "0"))) {
            btnEdit.setVisibility(View.VISIBLE);
            imgFollow.setVisibility(View.INVISIBLE);
            imgMsg.setVisibility(View.GONE);
        } else if (IsMyStar!=null && !IsMyStar.equalsIgnoreCase("0")) {
            btnEdit.setVisibility(View.GONE);
            imgFollow.setBackground(getResources().getDrawable(R.drawable.yellow_btn));
            imgFollow.setText(getString(R.string.following));
            imgFollow.setVisibility(View.VISIBLE);
            imgMsg.setVisibility(View.VISIBLE);
        } else {
            btnEdit.setVisibility(View.GONE);
            imgFollow.setBackground(getResources().getDrawable(R.drawable.yellow_btn));
            imgFollow.setText(getString(R.string.follow));
            imgFollow.setVisibility(View.VISIBLE);
            imgMsg.setVisibility(View.VISIBLE);
        }
    }

    private void setListeners(){
        textUserName.setOnClickListener(this);
        imgFollow.setOnClickListener(this);
        btnEdit.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.textUserName:
                onBackPressed();
                break;
            case R.id.imgFollow:
                onClickFollow();
                break;
            case R.id.btnEdit:
                startEditProfileActivity();
                break;

        }
    }

    private void addProfileListFragment(){
        final FragmentManager fragmentManager = getSupportFragmentManager();
        final FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        final UserProfileData userData = UserProfileData.newBuilder()
                .setUserDisplayName(UserDisplayName)
                .setIsProfile(isProfile)
                .setUserTagline(UserTagline)
                .setUserCoverImage(UserCoverImage)
                .setIsMyStar(IsMyStar)
                .setUserPic(UserPic)
                .setUserName(UserName)
                .setUserId(UserID)
                .setEntryId(EntryId)
                .build();
        final ProfileFragment profileFragment = ProfileFragment.getInstance(userData);
        fragmentTransaction.replace(R.id.fragmentContainer, profileFragment);
        fragmentTransaction.commit();
    }

    private void startEditProfileActivity(){
        final Intent intent = new Intent(this, EditProfileActivity.class);
        intent.putExtra("UserID", UserID);
        intent.putExtra("UserName", UserName);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    private void onClickFollow(){
        if (!IsMyStar.equalsIgnoreCase("0")) {
            deleteStarRequest();
        } else {
           addStarRequest();
        }
    }

    private void deleteStarRequest() {
        final HashMap<String, String> params = new HashMap<>();
        params.put("star", UserID);
        Utility.ShowProgressDialog(this, getString(R.string.loading));
        RestClient.getInstance(this).deleteRequest(Constant.DELETE_STAR + UserID, params, new ConnectCallback<StarResponse>() {
            @Override
            public void onSuccess(StarResponse object) {
                Utility.HideDialog(NewProfileActivity.this);
                final String error = object.getError();
                if (error == null) {
//                    if (onChangeEntryListener != null)
//                        onChangeEntryListener.onFollowEntry(UserID, "0");
                    IsMyStar="0";
                    imgFollow.setBackground(getResources().getDrawable(R.drawable.yellow_btn));
                    imgFollow.setText(getString(R.string.follow));
                    imgFollow.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(String error) {
                Utility.HideDialog(NewProfileActivity.this);
            }
        });
    }

    private void addStarRequest() {
        final HashMap<String, String> params = new HashMap<>();
        params.put("star", UserID);
        Utility.ShowProgressDialog(this, getString(R.string.loading));
        showStarDialog();
        RestClient.getInstance(this).postRequest(Constant.STAR, params, new ConnectCallback<StarResponse>() {

            @Override
            public void onSuccess(StarResponse object) {
                Utility.HideDialog(NewProfileActivity.this);
                if (object.getError() == null) {
//                    if (onChangeEntryListener != null)
//                        onChangeEntryListener.onFollowEntry(UserID, "1");
                }
            }

            @Override
            public void onFailure(String error) {
                Utility.HideDialog(NewProfileActivity.this);
            }
        });
    }

    private void showStarDialog() {
        final Dialog dialog = new Dialog(this, R.style.DialogAnimationTheme);
        dialog.setContentView(R.layout.dialog_add_star);
        dialog.show();
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                dialog.dismiss();
            }
        };
        timer.schedule(task, 1000);
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
