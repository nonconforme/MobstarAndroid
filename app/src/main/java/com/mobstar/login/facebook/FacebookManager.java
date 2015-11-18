package com.mobstar.login.facebook;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.facebook.share.ShareApi;
import com.facebook.share.Sharer;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mobstar.pojo.EntryPojo;
import com.mobstar.utils.Utility;
import org.json.JSONObject;
import java.util.Arrays;
import java.util.List;

/**
 * Created by lipcha on 18.11.15.
 */
public class FacebookManager {

    public static final List FACEBOOK_PERMISSIONS           = Arrays.asList("email, user_birthday, public_profile, publish_actions");
    public static final String FACEBOOK_USER_PARAMETERS     = "id,name,email,gender, birthday";

    private LoginButton mFacebookLoginButton;
    private CallbackManager mCallbackManager;
    private OnFacebookSignInCompletedListener onFacebookSignInCompletedListener;

    public FacebookManager(final Context context, final Activity activity, final OnFacebookSignInCompletedListener onCompletedListener) {
        FacebookSdk.sdkInitialize(context);
        mCallbackManager = CallbackManager.Factory.create();
        initFacebook(activity);
        onFacebookSignInCompletedListener = onCompletedListener;
    }

    public void signInWithFacebook(){
        mFacebookLoginButton.performClick();
    }

    public void onActivityResult(int requestCode, int responseCode, Intent intent){
        mCallbackManager.onActivityResult(requestCode, responseCode, intent);
    }

    public static void facebookLogOut(final Context context){
        FacebookSdk.sdkInitialize(context);
        LoginManager.getInstance().logOut();
    }

    public static boolean isLoggedIn() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        return accessToken != null;
    }

    public void post(final EntryPojo entryPojo, final boolean isTalent, final String userImg, final String shareText, final FacebookCallback<Sharer.Result> callback){
        Bitmap bitmap = null;
        if (isTalent) {
            bitmap = Utility.getBitmapFromURL(userImg);
        } else {
            bitmap = getShareBitmap(entryPojo);
        }
        ShareApi.share(getSharePhotoContent(getSharePhoto(bitmap, shareText)), callback);
    }

    private Bitmap getShareBitmap(final EntryPojo entryPojo){
        Bitmap bitmap = null;
        if (entryPojo.getType().equalsIgnoreCase("image")) {
            bitmap = Utility.getBitmapFromURL(entryPojo.getImageLink());
        } else if (entryPojo.getType().equalsIgnoreCase("audio")) {
            bitmap = Utility.getBitmapFromURL(entryPojo.getImageLink());
        } else if (entryPojo.getType().equalsIgnoreCase("video")) {
            bitmap = Utility.getBitmapFromURL(entryPojo.getVideoThumb());
        }
        return bitmap;
    }

    private SharePhotoContent getSharePhotoContent(final SharePhoto sharePhoto){
        final SharePhotoContent content = new SharePhotoContent.Builder()
                .addPhoto(sharePhoto)
                .build();
        return content;
    }

    private SharePhoto getSharePhoto(final Bitmap bitmap, final String caption){
        final SharePhoto sharePhoto = new SharePhoto.Builder()
                .setBitmap(bitmap)
                .setCaption(caption)
                .build();
        return sharePhoto;
    }




    private void initFacebook(final Context context) {
        mFacebookLoginButton = new LoginButton(context);
        mFacebookLoginButton.setReadPermissions(FACEBOOK_PERMISSIONS);
        mFacebookLoginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                requestFacebookUserData(loginResult);
            }

            @Override
            public void onCancel() {
                if (onFacebookSignInCompletedListener != null)
                    onFacebookSignInCompletedListener.onFacebookLoginFailure();
            }

            @Override
            public void onError(FacebookException e) {
                if (onFacebookSignInCompletedListener != null)
                    onFacebookSignInCompletedListener.onFacebookLoginFailure();
            }
        });
    }

    private void requestFacebookUserData(LoginResult loginResult) {
        final GraphRequest request = GraphRequest.newMeRequest(
                loginResult.getAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        final Gson gson = new GsonBuilder().create();
                        FacebookResponse facebookResponse = gson.fromJson(object.toString(), FacebookResponse.class);
                        if (onFacebookSignInCompletedListener != null)
                            onFacebookSignInCompletedListener.onFacebookLoginSuccess(facebookResponse);
                    }
                });
        final Bundle parameters = new Bundle();
        parameters.putString("fields", FACEBOOK_USER_PARAMETERS);
        request.setParameters(parameters);
        request.executeAsync();
    }

    public interface OnFacebookSignInCompletedListener {
        void onFacebookLoginSuccess(final FacebookResponse response);
        void onFacebookLoginFailure();
    }

}
