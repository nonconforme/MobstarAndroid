package com.mobstar.home.new_home_screen.profile;

import android.app.Activity;
import android.app.Dialog;
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
import com.mobstar.api.call.StarCall;
import com.mobstar.api.responce.StarResponse;
import com.mobstar.api.responce.UserProfileResponse;
import com.mobstar.home.new_home_screen.VideoListBaseFragment;
import com.mobstar.upload.MessageActivity;
import com.mobstar.utils.Constant;
import com.mobstar.utils.Utility;
import com.squareup.picasso.Picasso;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by lipcha on 21.09.15.
 */
public class NewProfileActivity extends BaseActivity implements View.OnClickListener {

    public static final int REFRESH_USER        = 23;
    public static final String USER             = "user";
    public static final String IS_ENTRY_ID_API  = "is_entry_id_api";
    public static final String ID               = "id";
    public static final String IS_NOTIFICATION  = "is notification";

    private SharedPreferences preferences;
    protected TextView textUserName;
    protected TextView imgFollow;
    protected ImageView imgMsg;
    protected TextView btnEdit;
    protected UserProfile user;
    protected VideoListBaseFragment profileFragment;
    private String iAmStar;
    protected boolean isNotification = false;

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
        if (user != null && user.getUserId().equals(preferences.getString("userid", "0"))) {
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

    protected void setupViews(){
        textUserName.setText(user.getUserName());

        if (user.getUserId().equals(preferences.getString("userid", "0"))) {
            btnEdit.setVisibility(View.VISIBLE);
            imgFollow.setVisibility(View.GONE);
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

        if(iAmStar != null && iAmStar.length() > 0 && iAmStar.equalsIgnoreCase("1") && user.getIsMyStar().equalsIgnoreCase("1") ){
            Picasso.with(this).load(R.drawable.msg_act_btn).into(imgMsg);
        }
        else{
            Picasso.with(this).load(R.drawable.msg_btn).into(imgMsg);
        }
    }

    protected void setListeners(){
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
                if (iAmStar != null && iAmStar.length() > 0 && iAmStar.equalsIgnoreCase("1") && user.getIsMyStar().equalsIgnoreCase("1")) {
                    startMessageActivity();
                }else startMessageErrorDialog();
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
        intent.putExtra("isDisableCompose", true);
        startActivity(intent);
    }

    private void startMessageErrorDialog(){
        final Dialog dialog = new Dialog(this, R.style.DialogAnimationTheme);
        dialog.setContentView(R.layout.message_error_dialog);
        dialog.show();

        final Timer timer = new Timer();
        final TimerTask task = new TimerTask() {

            @Override
            public void run() {
                dialog.dismiss();
            }
        };
        timer.schedule(task, 3000);
    }


    protected void addProfileListFragment(){
        final FragmentManager fragmentManager = getSupportFragmentManager();
        final FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        profileFragment = ProfileFragment.getInstance(user, isNotification);
        fragmentTransaction.replace(R.id.fragmentContainer, profileFragment);
        fragmentTransaction.commit();
    }

    private void startEditProfileActivity(){
        final Intent intent = new Intent(this, EditProfileActivity.class);
        intent.putExtra("UserID", user.getUserId());
        intent.putExtra("UserName", user.getUserName());
        startActivityForResult(intent, REFRESH_USER);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    private void onClickFollow() {
        if (user != null)
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
        StarCall.delStarCall(this, user.getUserId(), new ConnectCallback<StarResponse>() {
            @Override
            public void onSuccess(StarResponse object) {
                Utility.HideDialog(NewProfileActivity.this);
                final String error = object.getError();
                if (error == null || error.equals("")) {
                    if (profileFragment != null)
                        getProfileFragment().onFollowEntry(user.getUserId(), "0");
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
        StarCall.addStarCall(this, user.getUserId(), new ConnectCallback<StarResponse>() {

            @Override
            public void onSuccess(StarResponse object) {
                Utility.HideDialog(NewProfileActivity.this);
                if (object.getError() == null || object.getError().equals("")) {
                    if (profileFragment != null)
                        getProfileFragment().onFollowEntry(user.getUserId(), "1");
                    setIsMyStar("1");
                }
            }

            @Override
            public void onFailure(String error) {
                Utility.HideDialog(NewProfileActivity.this);
            }
        });
    }

    protected void getBundleData(){
        final Bundle extras = getIntent().getExtras();
        if (extras != null) {
            if (extras.containsKey(USER))
                user = (UserProfile) extras.getSerializable(USER);
            if (extras.containsKey(IS_NOTIFICATION))
                isNotification = extras.getBoolean(IS_NOTIFICATION);
        }
    }

    @Override
    public void onBackPressed() {
        setResult(101);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK)
            return;
        switch (requestCode){
            case REFRESH_USER:
                getUserRequest();
                break;
        }
    }

    private void getUserRequest(){
        Utility.ShowProgressDialog(this, getString(R.string.loading));
        Api.getMyUserProfile(this, new ConnectCallback<UserProfileResponse>() {

            @Override
            public void onSuccess(UserProfileResponse object) {
                Utility.HideDialog(NewProfileActivity.this);
                if (profileFragment != null && object != null && object.getUserProfile().size() != 0)
                    getProfileFragment().setUserProfile(object.getUserProfile().get(0));
            }

            @Override
            public void onFailure(String error) {
                Utility.HideDialog(NewProfileActivity.this);
            }
        });
    }

    private ProfileFragment getProfileFragment(){
        return (ProfileFragment) profileFragment;
    }
}
