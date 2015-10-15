package com.mobstar.home.notification;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;

import com.mobstar.R;
import com.mobstar.home.new_home_screen.VideoListBaseFragment;
import com.mobstar.home.new_home_screen.profile.NewProfileActivity;
import com.mobstar.home.new_home_screen.profile.UserProfile;

/**
 * Created by lipcha on 13.10.15.
 */
public class SingleEntryActivity extends NewProfileActivity {

    private boolean isEntryIdAPI = false;
    private String id;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void addProfileListFragment() {
        final FragmentManager fragmentManager = getSupportFragmentManager();
        final FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        profileFragment = SingleEntryFragment.getInstance(user, isNotification, isEntryIdAPI, id);
        fragmentTransaction.replace(R.id.fragmentContainer, profileFragment);
        fragmentTransaction.commit();
    }

    @Override
    protected void setupViews() {
        imgFollow.setVisibility(View.GONE);
        imgMsg.setVisibility(View.GONE);
        btnEdit.setVisibility(View.GONE);
        textUserName.setText(getString(R.string.back));
    }

    @Override
    protected void setListeners() {
        textUserName.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.textUserName:
                onBackPressed();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        setResult(101);
        finish();
    }

    @Override
    protected void getBundleData() {
        super.getBundleData();
        final Bundle extras = getIntent().getExtras();
        if (extras != null) {
            if (extras.containsKey(IS_ENTRY_ID_API))
                isEntryIdAPI = extras.getBoolean(IS_ENTRY_ID_API);
            if (extras.containsKey(ID))
                id = extras.getString(ID);
        }
    }
}
