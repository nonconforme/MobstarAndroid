package com.mobstar.home.notification;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;

import com.mobstar.R;
import com.mobstar.home.new_home_screen.profile.NewProfileActivity;

/**
 * Created by lipcha on 13.10.15.
 */
public class NotificationEntryActivity extends NewProfileActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void addProfileListFragment() {
        final FragmentManager fragmentManager = getSupportFragmentManager();
        final FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        profileFragment = NotificationEntryFragment.getInstance(user, isNotification);
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
}
