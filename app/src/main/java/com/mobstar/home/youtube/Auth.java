package com.mobstar.home.youtube;

/**
 * Created by lipcha on 03.11.15.
 */
import com.google.android.gms.common.Scopes;
import com.google.api.services.youtube.YouTubeScopes;

public class Auth {
    // Register an API key here: https://console.developers.google.com
    public static final String KEY = "AIzaSyDHcFZOwG7Nx57NnPWNgDpr53u1hPr6Tos";

    public static final String[] SCOPES = {Scopes.PROFILE, YouTubeScopes.YOUTUBE};
}

