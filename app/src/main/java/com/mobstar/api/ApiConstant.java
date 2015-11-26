package com.mobstar.api;

/**
 * Created by lipcha on 17.11.15.
 */
public class ApiConstant {

    public static final String BASE_SERVER_URL  = "http://192.168.88.250:8841/";

    //   LOGIN

    public static final String SIGN_UP               = "signUp";
    public static final String SIGN_SOCIAL           = "signSocial";
    public static final String SIGN_IN               = "signIn";
    public static final String WELCOME               = "welcome";
    public static final String FORGOT_PASSWORD       = "forgotPassword";
    public static final String USER_ANALYTIC         = "user/analytic";
    public static final String SIGN_OUT              = "signOut";

    //   ENTRY
    public static final String GET_ENTRY             = "entry";
    public static final String VOTE_UP               = "entry/voteUp/";
    public static final String VOTE_DOWN             = "entry/voteDown/";
    public static final String UPDATE_VIEW_COUNTS    = "entry/view/";

    //   FILTERS

    public static final String CATEGORIES             = "categories";

    //   PROFILE
    public static final String SETTING_USER_CONTINENT = "settings/userContinent/";
    public static final String USER                   = "user/";
    public static final String PROFILE                = "/profile";
    public static final String USER_SETTINGS          = "user/settings";
    public static final String USER_STARED_BY         = "user/staredBy";
    public static final String WHO_TO_FOLLOW          = "user/team";
    public static final String USER_FOLLOW            = "user/followTeam";
    public static final String FOLLOW                 = "user/follow/";
    public static final String UNFOLLOW               = "user/unfollow/";


    //  NOTIFICATION
    public static final String DEFAULT_NOTIFICATION  = "notification/default";
    public static final String NOTIFICATION_COUNT    = "notification/count";


    //    OTHER
    public static final String GOOGLE_DEVICE_TYPE    = "google";


}
