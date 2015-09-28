package com.mobstar.home.new_home_screen.profile;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mobstar.BaseActivity;
import com.mobstar.EditProfileActivity;
import com.mobstar.R;
import com.mobstar.api.Api;
import com.mobstar.api.ConnectCallback;
import com.mobstar.api.responce.StarResponse;
import com.mobstar.upload.MessageActivity;
import com.mobstar.utils.Constant;
import com.mobstar.utils.Utility;
import com.squareup.picasso.Picasso;

/**
 * Created by lipcha on 21.09.15.
 */
public class NewProfileActivity extends BaseActivity implements View.OnClickListener {

    public static final String USER = "user";

    private SharedPreferences preferences;
    private TextView textUserName;
    private TextView imgFollow;
    private ImageView imgMsg;
    private TextView btnEdit;
    private UserProfile user;
    private ProfileFragment profileFragment;
    private String iAmStar;

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
        if (user.getUserId().equals(preferences.getString("userid", "0"))) {
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
    }

    private void setupViews(){
        textUserName.setText(user.getUserName());

        if (user.getUserId().equals(preferences.getString("userid", "0"))) {
            btnEdit.setVisibility(View.VISIBLE);
            imgFollow.setVisibility(View.INVISIBLE);
            imgMsg.setVisibility(View.GONE);
        } else if (user.getIsMyStar() != null && !user.getIsMyStar().equalsIgnoreCase("0")) {
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

        if(iAmStar != null && iAmStar.length() > 0 && iAmStar.equalsIgnoreCase("1")){
            Picasso.with(this).load(R.drawable.msg_act_btn).into(imgMsg);
        }
        else{
            Picasso.with(this).load(R.drawable.msg_btn).into(imgMsg);
        }
    }

    private void setListeners(){
        textUserName.setOnClickListener(this);
        imgFollow.setOnClickListener(this);
        btnEdit.setOnClickListener(this);
        imgMsg.setOnClickListener(this);
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
            case R.id.imgMsg:
                if (iAmStar != null && iAmStar.length() > 0 && iAmStar.equalsIgnoreCase("1")) {
                    startMessageActivity();
                }
                break;
        }
    }

    public void setIAmStar(final String _iAmStar){
        iAmStar = _iAmStar;
        setupViews();
    }

    private void startMessageActivity(){
        final Intent intent=new Intent(this, MessageActivity.class);
        intent.putExtra("recipent", user.getUserId());
        intent.putExtra("isDisableCompose",true);
        startActivity(intent);

    }

    private void addProfileListFragment(){
        final FragmentManager fragmentManager = getSupportFragmentManager();
        final FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        profileFragment = ProfileFragment.getInstance(user);
        fragmentTransaction.replace(R.id.fragmentContainer, profileFragment);
        fragmentTransaction.commit();
    }

    private void startEditProfileActivity(){
        final Intent intent = new Intent(this, EditProfileActivity.class);
        intent.putExtra("UserID", user.getUserId());
        intent.putExtra("UserName", user.getUserName());
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    private void onClickFollow(){
        if (!user.getIsMyStar().equalsIgnoreCase("0")) {
            deleteStarRequest();
        } else {
           addStarRequest();
        }
    }

    public void setIsMyStar(final String star){
        user.setIsMyStar(star);
        setupViews();
    }

    private void deleteStarRequest() {
        Utility.ShowProgressDialog(this, getString(R.string.loading));
        Api.deleteStarRequest(this, user.getUserId(), new ConnectCallback<StarResponse>() {
            @Override
            public void onSuccess(StarResponse object) {
                Utility.HideDialog(NewProfileActivity.this);
                final String error = object.getError();
                if (error == null || error.equals("")) {
                    if (profileFragment != null)
                        profileFragment.onFollowEntry(user.getUserId(), "0");
                    setIsMyStar("0");
                }
            }

            @Override
            public void onFailure(String error) {
                Utility.HideDialog(NewProfileActivity.this);
            }
        });
    }

    private void addStarRequest() {
        Utility.ShowProgressDialog(this, getString(R.string.loading));
        Api.addStarRequest(this, user.getUserId(), new ConnectCallback<StarResponse>() {

            @Override
            public void onSuccess(StarResponse object) {
                Utility.HideDialog(NewProfileActivity.this);
                if (object.getError() == null || object.getError().equals("")) {
                    if (profileFragment != null)
                        profileFragment.onFollowEntry(user.getUserId(), "1");
                    setIsMyStar("1");
                }
            }

            @Override
            public void onFailure(String error) {
                Utility.HideDialog(NewProfileActivity.this);
            }
        });
    }

    private void getBundleData(){
        final Bundle extras = getIntent().getExtras();
        if (extras != null) {
            if (extras.containsKey(USER))
                user = (UserProfile) extras.getSerializable(USER);
        }
    }

    @Override
    public void onBackPressed() {
        setResult(101);
        finish();
    }
}
