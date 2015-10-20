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
public class SingleEntryFragment extends VideoListBaseFragment {

    private UserProfile user;
    private boolean isNotification = false;
    private boolean isEntryIdAPI = false;
    private String id;

    public static final SingleEntryFragment getInstance(final UserProfile userData, final boolean isNotification, boolean isEntryIdAPI, String id){
        final SingleEntryFragment profileFragment = new SingleEntryFragment();
        final Bundle args = new Bundle();
        if (userData != null)
            args.putSerializable(NewProfileActivity.USER, userData);
        args.putBoolean(NewProfileActivity.IS_NOTIFICATION, isNotification);
        args.putBoolean(IS_ENTRY_ID_API, isEntryIdAPI);
        if (id != null)
            args.putString(ID, id);
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
        if (args.containsKey(IS_ENTRY_ID_API))
            isEntryIdAPI = args.getBoolean(IS_ENTRY_ID_API);
        if (args.containsKey(ID))
            id = args.getString(ID);

    }

    @Override
    public boolean getEnablePulToRefreshAction() {
        return true;
    }

    @Override
    public boolean getEnableSwipeCardAction() {
        return false;
    }

    @Override
    protected void getEntryRequest(int pageNo) {
        final HashMap<String, String> params = new HashMap<>();

        textNoData.setVisibility(View.GONE);
        if (isNotification || isEntryIdAPI) {
            if (isEntryIdAPI)
                getEntry(Constant.GET_ENTRY + id, null, 1);
            else
                getEntry(Constant.GET_ENTRY + user.getEntryId() + Constant.INFO, null, 1);
        }
        else if (user != null && user.getUserId() != null) {
            params.put("user", user.getUserId());
            params.put("page", Integer.toString(pageNo));
            getEntry(Constant.MIX_ENTRY, params, pageNo);
        }
    }

}
