package com.mobstar.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.mobstar.api.new_api_model.Profile;

/**
 * Created by lipcha on 17.11.15.
 */
public class UserPreference {

    public static final String MOBSTAR_PREFERENCE = "mobstar_pref";

    public static final String USER_NAME         = "username";
    public static final String FULL_NAME         = "fullName";
    public static final String DISPLAY_NAME      = "displayName";
    public static final String TOKEN             = "token";
    public static final String USER_ID           = "userId";
    public static final String PROFILE_IMAGE     = "profile_image";
    public static final String COVER_IMAGE       = "cover_image";
    public static final String TAGLINE           = "tagline";
    public static final String BIO               = "bio";
    public static final String IS_LOGIN          = "isLogin";

    public static final void saveUserProfileToPreference(final Context context, final Profile profile, final boolean isLogin){
        final SharedPreferences pref = context.getSharedPreferences(MOBSTAR_PREFERENCE, Context.MODE_PRIVATE);
        pref.edit()
                .putString(USER_NAME, profile.getDisplayName())
                .putString(FULL_NAME, profile.getFullName())
                .putString(USER_ID, profile.getId())
                .putString(PROFILE_IMAGE, profile.getProfileImage())
                .putString(COVER_IMAGE, profile.getCoverImage())
                .putString(TAGLINE, profile.getTagline())
                .putString(BIO, profile.getBio())
                .putBoolean(IS_LOGIN, isLogin)
                .apply();
    }


}
