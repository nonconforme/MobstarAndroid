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
    public static final String IS_SOCIAL_LOGIN   = "isSocialLogin";
    public static final String USER_CONTINENT     = "user_continent";

    public static final String WELCOME_IS_CHECKED = "welcome_is_checked";

    private static final String IS_FIRST_OPEN_PREFERENCE = "is first open";

    public static final void saveUserProfileToPreference(final Context context, final Profile profile, final boolean isLogin, final int userContinent){
        getPreference(context).edit()
                .putString(USER_NAME, profile.getDisplayName())
                .putString(FULL_NAME, profile.getFullName())
                .putString(USER_ID, profile.getId())
                .putString(PROFILE_IMAGE, profile.getProfileImage())
                .putString(COVER_IMAGE, profile.getCoverImage())
                .putString(TAGLINE, profile.getTagline())
                .putString(BIO, profile.getBio())
                .putBoolean(IS_LOGIN, isLogin)
                .putInt(USER_CONTINENT, userContinent)
                .apply();
    }

    public static void isSocialLoginToPreference(final Context context, final boolean isSocial){
        getPreference(context).edit().putBoolean(IS_SOCIAL_LOGIN, isSocial).apply();
    }

    public static int getUserContinent(final Context context){
        return getPreference(context).getInt(USER_CONTINENT, 0);
    }

    public static boolean welcomeIsChecked(final Context context){
        return getPreference(context).getBoolean(WELCOME_IS_CHECKED, true);
    }

    public static void setWelcomeChecked(final Context context, final boolean isChecked){
        getPreference(context).edit().putBoolean(WELCOME_IS_CHECKED, isChecked).apply();
    }


    public static boolean isFirstOpenApp(final Context context){
        final SharedPreferences preferences = getPreference(context);
        final boolean isFirstOpen = preferences.getBoolean(IS_FIRST_OPEN_PREFERENCE, true);
        if (isFirstOpen)
            preferences.edit().putBoolean(IS_FIRST_OPEN_PREFERENCE, false).apply();
        return isFirstOpen;
    }

    public static boolean isLogin(final Context context){
        return getPreference(context).getBoolean(IS_LOGIN, false);
    }

    private static  SharedPreferences getPreference(final Context context){
        return context.getSharedPreferences(MOBSTAR_PREFERENCE, Context.MODE_PRIVATE);
    }
}
