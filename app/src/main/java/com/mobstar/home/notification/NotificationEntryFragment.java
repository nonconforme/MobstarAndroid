package com.mobstar.home.notification;

import android.os.Bundle;
import android.view.View;

import com.mobstar.home.new_home_screen.VideoListBaseFragment;
import com.mobstar.home.new_home_screen.profile.NewProfileActivity;
import com.mobstar.home.new_home_screen.profile.UserProfile;
import com.mobstar.utils.Constant;

import java.util.HashMap;

/**
 * Created by lipcha on 13.10.15.
 */
public class NotificationEntryFragment extends VideoListBaseFragment {

    private UserProfile user;
    private boolean isNotification = false;

    public static final NotificationEntryFragment getInstance(final UserProfile userData, final boolean isNotification){
        final NotificationEntryFragment profileFragment = new NotificationEntryFragment();
        final Bundle args = new Bundle();
        args.putSerializable(NewProfileActivity.USER, userData);
        args.putBoolean(NewProfileActivity.IS_NOTIFICATION, isNotification);
        profileFragment.setArguments(args);
        return profileFragment;
    }

    @Override
    protected void getArgs() {
        final Bundle args = getArguments();
        if (args.containsKey(NewProfileActivity.USER))
            user = (UserProfile) args.getSerializable(NewProfileActivity.USER);
        if (args.containsKey(NewProfileActivity.IS_NOTIFICATION))
            isNotification = args.getBoolean(NewProfileActivity.IS_NOTIFICATION);

    }

    @Override
    protected void getEntryRequest(int pageNo) {
        final HashMap<String, String> params = new HashMap<>();
        if (user != null && user.getUserId() != null)
            params.put("user", user.getUserId());
        params.put("page", Integer.toString(pageNo));
        textNoData.setVisibility(View.GONE);
        if (isNotification)
            getEntry(Constant.GET_ENTRY + user.getEntryId() + Constant.INFO, null, 1);
        else
            getEntry(Constant.MIX_ENTRY, params, pageNo);
    }

}
