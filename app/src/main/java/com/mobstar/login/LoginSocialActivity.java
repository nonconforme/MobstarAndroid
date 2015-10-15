package com.mobstar.login;

import android.accounts.AccountManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.facebook.LoggingBehavior;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.Settings;
import com.facebook.model.GraphUser;
import com.google.android.gms.auth.GooglePlayServicesAvailabilityException;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.common.AccountPicker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.mobstar.AdWordsManager;
import com.mobstar.R;
import com.mobstar.api.ConnectCallback;
import com.mobstar.api.RestClient;
import com.mobstar.api.responce.UserAccountResponse;
import com.mobstar.custom.AbstractGetNameTask;
import com.mobstar.custom.CustomTextview;
import com.mobstar.custom.CustomTextviewBold;
import com.mobstar.custom.GetNameInForeground;
import com.mobstar.geo_filtering.SelectCurrentRegionActivity;
import com.mobstar.help.WelcomeVideoActivity;
import com.mobstar.home.HomeActivity;
import com.mobstar.twitter.ImageTwitter;
import com.mobstar.twitter.ImageTwitter.OnCompleteListener;
import com.mobstar.utils.Constant;
import com.mobstar.utils.JSONParser;
import com.mobstar.utils.Utility;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class LoginSocialActivity extends Activity implements OnClickListener {

	private Context mContext;

	private LinearLayout btnGetStarted, btnSignIn;
	private CustomTextview btnLoginFB,  btnLoginTwitter, btnLoginGoogle;
	private CustomTextviewBold btnCountinueWOSignin;
	private Session.StatusCallback statusCallback = new SessionStatusCallback();
	private static final List<String> READ_PERMISSIONS = Arrays.asList("email","public_profile");

	private String sUserID = "", sToken = "", sUserFullName = "", sUserName = "", sUserDisplayName = "";
	private String sErrorMessage = "";
	private String ProfileImage = "", ProfileCover = "", UserTagLine = "",UserBio = "";
	private ImageTwitter mTweet;

	// Google plus sign in
	String mEmail;
	private static final String SCOPE = "oauth2:https://www.googleapis.com/auth/userinfo.profile";
//	private static final String SCOPE="https://www.googleapis.com/auth/youtube.readonly";
	static final int REQUEST_CODE_PICK_ACCOUNT = 1000;
	static final int REQUEST_CODE_RECOVER_FROM_AUTH_ERROR = 1001;
	static final int REQUEST_CODE_RECOVER_FROM_PLAY_SERVICES_ERROR = 1002;
	String googleToken="";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login_social);

		mContext = LoginSocialActivity.this;

		initControls();

		Settings.addLoggingBehavior(LoggingBehavior.INCLUDE_ACCESS_TOKENS);

		Session session = Session.getActiveSession();

		if (session == null) {
			if (savedInstanceState != null) {
				session = Session.restoreSession(mContext, null, statusCallback, savedInstanceState);
			}
			if (session == null) {
				session = new Session(mContext);
			}
			Session.setActiveSession(session);
		}
        session = Session.getActiveSession();
        session.closeAndClearTokenInformation();
        session.removeCallback(statusCallback);
        Session.setActiveSession(session);

		Utility.SendDataToGA("LgoinSocial Screen", LoginSocialActivity.this);

	}

	void initControls() {

		btnGetStarted = (LinearLayout) findViewById(R.id.btnGetStarted);
		btnGetStarted.setOnClickListener(this);

		btnSignIn = (LinearLayout) findViewById(R.id.btnSignIn);
		btnSignIn.setOnClickListener(this);

		btnLoginFB = (CustomTextview) findViewById(R.id.btnLoginFB);
		btnLoginFB.setOnClickListener(this);

		btnLoginTwitter = (CustomTextview) findViewById(R.id.btnLoginTwitter);
		btnLoginTwitter.setOnClickListener(this);

		btnLoginGoogle = (CustomTextview) findViewById(R.id.btnLoginGoogle);
		btnLoginGoogle.setOnClickListener(this);
		
		btnCountinueWOSignin= (CustomTextviewBold) findViewById(R.id.btnCountinueWOSignin);
		btnCountinueWOSignin.setOnClickListener(this);

	}

	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub

		if (view.equals(btnGetStarted)) {
			Intent intent = new Intent(mContext, SignUpActivity.class);
			startActivity(intent);
			finish();
		} else if (view.equals(btnSignIn)) {
			Intent intent = new Intent(mContext, LoginActivity.class);
			startActivity(intent);
			finish();
		} else if (btnLoginFB.equals(view)) {

			final Button btnDeny, btnAllow;
			final ImageButton btnClose;

			final Dialog dialog = new Dialog(LoginSocialActivity.this, R.style.DialogTheme);
			dialog.setContentView(R.layout.dialog_fb);
			btnClose = (ImageButton) dialog.findViewById(R.id.btnClose);
			btnClose.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
			btnAllow = (Button) dialog.findViewById(R.id.btnAllow);
			btnAllow.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Utility.ShowProgressDialog(mContext, getString(R.string.loading));
					onFBLogin();
					dialog.dismiss();
				}
			});
			btnDeny = (Button) dialog.findViewById(R.id.btnDeny);
			btnDeny.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					dialog.dismiss();
				}
			});
			dialog.show();

		} else if (btnLoginTwitter.equals(view)) {

			Utility.ShowProgressDialog(mContext, getString(R.string.loading));

			boolean authOnly = true;
			mTweet = new ImageTwitter(LoginSocialActivity.this, authOnly, null,null);
			mTweet.setOnCompleteListener(new OnCompleteListener() {

				@Override
				public void onComplete(final String action) {
					// TODO Auto-generated method stub

					if (action.equals("Success")) {

						if (Utility.isNetworkAvailable(mContext)) {
							// Log.v(Constant.TAG, "User " +
							// mTweet.mUser.toString());

							sErrorMessage = "";
							SharedPreferences pref = getSharedPreferences("mobstar_pref", MODE_PRIVATE);
							pref.edit().putBoolean("isSocialLogin",true).commit();
							new TwitterLoginCall(mTweet.mUser.getId() + "", mTweet.mUser.getScreenName(), mTweet.mUser.getName(), mTweet.mUser.getScreenName()).start();
						} else {
							Toast.makeText(mContext, getString(R.string.no_internet_access), Toast.LENGTH_SHORT).show();
							Utility.HideDialog(mContext);
						}
					} else {
						Utility.HideDialog(mContext);
					}

				}
			});
			mTweet.send();
		} else if (btnLoginGoogle.equals(view)) {
			Utility.ShowProgressDialog(mContext, getString(R.string.loading));
			getUsername();
		}else if(btnCountinueWOSignin.equals(view)) {           //khyati
			
		}
		
	}

	/** Called by button in the layout */
	public void greetTheUser() {
		int statusCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
		if (statusCode == ConnectionResult.SUCCESS) {
			getUsername();
		} else if (GooglePlayServicesUtil.isUserRecoverableError(statusCode)) {
			Dialog dialog = GooglePlayServicesUtil.getErrorDialog(statusCode, this, 0 /*
																					 * request
																					 * code
																					 * not
																					 * used
																					 */);
			dialog.show();
		} else {
			Toast.makeText(this, R.string.unrecoverable_error, Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * Attempt to get the user name. If the email address isn't known yet, then
	 * call pickUserAccount() method so the user can pick an account.
	 */
	private void getUsername() {
		if (mEmail == null) {
			pickUserAccount();
		} else {
			if (Utility.isNetworkAvailable(mContext)) {
				getTask(LoginSocialActivity.this, mEmail, SCOPE).execute();
			} else {
				Toast.makeText(mContext, getString(R.string.no_internet_access), Toast.LENGTH_SHORT).show();
				Utility.HideDialog(mContext);
			}
		}
	}

	private void pickUserAccount() {
		String[] accountTypes = new String[] { "com.google" };
		Intent intent = AccountPicker.newChooseAccountIntent(null, null, accountTypes, false, null, null, null, null);
		startActivityForResult(intent, REQUEST_CODE_PICK_ACCOUNT);
	}

	private void onFBLogin() {
        Session session = Session.getActiveSession();
        session.closeAndClearTokenInformation();
        session.removeCallback(statusCallback);


        Session.setActiveSession(new Session(mContext));
		session = Session.getActiveSession();

		if (!session.isOpened() && !session.isClosed()) {
			session.openForRead(new Session.OpenRequest(this).setCallback(statusCallback));

		} else {
			Session.openActiveSession(LoginSocialActivity.this, true, statusCallback);
		}
	}

	private class SessionStatusCallback implements Session.StatusCallback {
		@Override
		public void call(Session session, SessionState state, Exception exception) {

			if (exception != null) {

				new AlertDialog.Builder(mContext).setTitle(R.string.app_name).setMessage(exception.getMessage()).setPositiveButton("OK", null).show();

				Utility.HideDialog(mContext);

			}

			if (session.isOpened()) {

				List<String> permissions = session.getPermissions();
				if (!isSubsetOf(READ_PERMISSIONS, permissions)) {

					Session.NewPermissionsRequest newPermissionsRequest = new Session.NewPermissionsRequest(LoginSocialActivity.this, READ_PERMISSIONS);
					session.requestNewReadPermissions(newPermissionsRequest);

					return;
				}

				if (session.getPermissions().contains("email")) {

					if (Utility.isNetworkAvailable(mContext)) {
						Request.newMeRequest(session, new Request.GraphUserCallback() {

							@Override
							public void onCompleted(GraphUser user, Response response) {
								if (user != null) {
									sErrorMessage = "";
									try {
										SharedPreferences pref = getSharedPreferences("mobstar_pref", MODE_PRIVATE);
										pref.edit().putBoolean("isSocialLogin",true).commit();
                                        FbAccount fbAccount = new FbAccount(user);
//										new FBLoginCall(user.getId(), user.getName(), user.getProperty("email").toString(), user.getName(), user.getBirthday(), user.getProperty(
//												"gender").toString(), user.getFirstName()).start();
                                        new FBLoginCall(fbAccount.id, fbAccount.name, fbAccount.email, fbAccount.name,
                                                fbAccount.birthday, fbAccount.gender, fbAccount.firstName).start();
                                    } catch (Exception e) {
										// TODO: handle exception
										e.printStackTrace();
										Toast.makeText(mContext, getString(R.string.error_while_login_with_facebook), Toast.LENGTH_SHORT).show();
										Utility.HideDialog(mContext);
									}

								}
							}
						}).executeAsync();
					} else {

						Toast.makeText(mContext, getString(R.string.no_internet_access), Toast.LENGTH_SHORT).show();
						Utility.HideDialog(mContext);
					}

				}

			}

		}
	}

	private boolean isSubsetOf(Collection<String> subset, Collection<String> superset) {
		for (String string : subset) {
			if (!superset.contains(string)) {
				return false;
			}
		}
		return true;
	}

	class GoogleLoginCall extends Thread {

		String displayName, userId, userName, gender, fullName;

		public GoogleLoginCall(String userId, String userName, String displayName, String gender, String fullName) {
			this.userId = userId;
			this.displayName = displayName;
			this.userName = userName;
			this.gender = gender;
			this.fullName = fullName;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub

			String[] name = { "userName", "userId", "displayName", "gender", "fullName","deviceToken","device"};
			String[] value = { userName, userId, displayName, gender, fullName,Utility.getRegistrationId(mContext),"google" };

			String response = JSONParser.postRequest(Constant.SERVER_URL + Constant.GOOGLE_LOGIN, name, value, null);

//			Log.v(Constant.TAG, "GoogleLoginCall response " + response);

			if (response != null) {

				try {

					JSONObject jsonObject = new JSONObject(response);

					if (jsonObject.has("error")) {
						sErrorMessage = jsonObject.getString("error");
					}

					if (jsonObject.has("token")) {
						sToken = jsonObject.getString("token");
					}

					if (jsonObject.has("userId")) {
						sUserID = jsonObject.getString("userId");
					}

					if (jsonObject.has("userName")) {
						sUserName = jsonObject.getString("userName");
					}

					if (jsonObject.has("fullName")) {
						sUserFullName = jsonObject.getString("fullName");
					}

					if (jsonObject.has("profileImage")) {
						ProfileImage = jsonObject.getString("profileImage");
					}

					if (jsonObject.has("profileCover")) {
						ProfileCover = jsonObject.getString("profileCover");
					}

					if (jsonObject.has("userTagline")) {
						UserTagLine = jsonObject.getString("userTagline");
					}
					
					if (jsonObject.has("userBio")) {
						UserBio = jsonObject.getString("userBio");
					}

					if (jsonObject.has("userDisplayName")) {
						sUserDisplayName = jsonObject.getString("userDisplayName");
					}

					if (sErrorMessage != null && !sErrorMessage.equals("")) {
						handlerLogin.sendEmptyMessage(0);
					} else {
						handlerLogin.sendEmptyMessage(1);
					}

				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
					handlerLogin.sendEmptyMessage(0);
				}

			} else {

				handlerLogin.sendEmptyMessage(0);
			}

		}
	}

	class FBLoginCall extends Thread {
		
		
		String displayName, username, dob, userId, email, gender, fullName;

		public FBLoginCall(String userId, String username, String email, String displayName, String dob, String gender, String fullName) {
			this.userId = userId;
			this.username = username;
			this.displayName = displayName;
			this.email = email;
			this.dob = dob;
			this.gender = gender;
			this.fullName = fullName;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub

			String[] name = { "userName", "email", "userId", "displayName", "dob", "gender", "fullName","deviceToken","device" };
			String[] value = { username, email, userId, displayName, dob, gender, fullName ,Utility.getRegistrationId(mContext),"google"};

			String response = JSONParser.postRequest(Constant.SERVER_URL + Constant.FACEBOOK_LOGIN, name, value, null);

//			Log.v(Constant.TAG, "FBLoginCall response " + response);

			if (response != null) {

				try {

					JSONObject jsonObject = new JSONObject(response);

					if (jsonObject.has("error")) {
						sErrorMessage = jsonObject.getString("error");
					}

					if (jsonObject.has("token")) {
						sToken = jsonObject.getString("token");
					}

					if (jsonObject.has("userId")) {
						sUserID = jsonObject.getString("userId");
					}

					if (jsonObject.has("userName")) {
						sUserName = jsonObject.getString("userName");
					}

					if (jsonObject.has("fullName")) {
						sUserFullName = jsonObject.getString("fullName");
					}

					if (jsonObject.has("userDisplayName")) {
						sUserDisplayName = jsonObject.getString("userDisplayName");
					}

					if (jsonObject.has("profileImage")) {
						ProfileImage = jsonObject.getString("profileImage");
					}

					if (jsonObject.has("profileCover")) {
						ProfileCover = jsonObject.getString("profileCover");
					}

					if (jsonObject.has("userTagline")) {
						UserTagLine = jsonObject.getString("userTagline");
					}

					if (sErrorMessage != null && !sErrorMessage.equals("")) {
						handlerLogin.sendEmptyMessage(0);
					} else {
						handlerLogin.sendEmptyMessage(1);
					}

				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
					handlerLogin.sendEmptyMessage(0);
				}

			} else {

				handlerLogin.sendEmptyMessage(0);
			}

		}
	}

	class TwitterLoginCall extends Thread {

		String displayName, userId, FullName, UserName;

		public TwitterLoginCall(String userId, String displayName, String FullName, String UserName) {
			this.userId = userId;
			this.displayName = displayName;
			this.FullName = FullName;
			this.UserName = UserName;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub

			String[] name = { "userId", "displayName", "fullName", "userName" };
			String[] value = { userId, displayName, FullName, UserName };

			String response = JSONParser.postRequest(Constant.SERVER_URL + Constant.TWITTER_LOGIN, name, value, null);

//			Log.v(Constant.TAG, "TwitterLoginCall response " + response);

			if (response != null) {

				try {

					JSONObject jsonObject = new JSONObject(response);

					if (jsonObject.has("error")) {
						sErrorMessage = jsonObject.getString("error");
					}

					if (jsonObject.has("token")) {
						sToken = jsonObject.getString("token");
					}

					if (jsonObject.has("userId")) {
						sUserID = jsonObject.getString("userId");
					}

					if (jsonObject.has("userName")) {
						sUserName = jsonObject.getString("userName");
					}

					if (jsonObject.has("fullName")) {
						sUserFullName = jsonObject.getString("fullName");
					}

					if (jsonObject.has("userDisplayName")) {
						sUserDisplayName = jsonObject.getString("userDisplayName");
					}

					if (jsonObject.has("profileImage")) {
						ProfileImage = jsonObject.getString("profileImage");
					}

					if (jsonObject.has("profileCover")) {
						ProfileCover = jsonObject.getString("profileCover");
					}

					if (jsonObject.has("userTagline")) {
						UserTagLine = jsonObject.getString("userTagline");
					}

					if (sErrorMessage != null && !sErrorMessage.equals("")) {
						handlerLogin.sendEmptyMessage(0);
					} else {
						handlerLogin.sendEmptyMessage(1);
					}

				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
					handlerLogin.sendEmptyMessage(0);
				}

			} else {

				handlerLogin.sendEmptyMessage(0);
			}

		}
	}

	Handler handlerLogin = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			Utility.HideDialog(mContext);

			if (msg.what == 1) {

				SharedPreferences pref = getSharedPreferences("mobstar_pref", MODE_PRIVATE);
				pref.edit().putString("username", sUserName).commit();
				pref.edit().putString("fullName", sUserFullName).commit();
				pref.edit().putString("displayName", sUserDisplayName).commit();
				pref.edit().putString("token", sToken).commit();
				pref.edit().putString("userid", sUserID).commit();
				pref.edit().putString("profile_image", ProfileImage).commit();
				pref.edit().putString("cover_image", ProfileCover).commit();
				pref.edit().putString("tagline", UserTagLine).commit();
				pref.edit().putString("bio", UserBio).commit();
				pref.edit().putBoolean("isLogin", true).commit();

                AdWordsManager.getInstance().sendSingupEvent();
				if (pref.getBoolean(WelcomeVideoActivity.WELCOME_IS_CHECKED, true)) {
					startWelcomeActivity();
				}else {
					getUserAccountRequest();
				}
				
			} else {
				OkayAlertDialog(sErrorMessage);
			}
		}
	};

	private void startWelcomeActivity(){
		Intent intent = new Intent(mContext, WelcomeVideoActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
		finish();
	}

	private void getUserAccountRequest(){
		Utility.ShowProgressDialog(mContext, getString(R.string.loading));
		RestClient.getInstance(this).getRequest(Constant.USER_ACCOUNT, null, new ConnectCallback<UserAccountResponse>() {
			@Override
			public void onSuccess(UserAccountResponse object) {
				Utility.HideDialog(mContext);
				if (object.getUser().getUserContinentId() == 0) {
					startSelectCurrentRegionActivity();
				} else startHomeActivity();
			}

			@Override
			public void onFailure(String error) {
				Utility.HideDialog(mContext);
				startHomeActivity();
			}
		});
	}

	private void startSelectCurrentRegionActivity(){
		final Intent intent = new Intent(this, SelectCurrentRegionActivity.class);
		startActivity(intent);
		finish();
	}


	private void startHomeActivity(){
		Intent intent = new Intent(mContext, HomeActivity.class);
		startActivity(intent);
		finish();
	}

	void OkayAlertDialog(final String msg) {

		if (!isFinishing()) {
			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);

					// set title
					alertDialogBuilder.setTitle(getResources().getString(R.string.app_name));

					// set dialog message
					alertDialogBuilder.setMessage(msg).setCancelable(false).setNeutralButton("OK", null);

					// create alert dialog
					AlertDialog alertDialog = alertDialogBuilder.create();

					// show it
					alertDialog.show();
				}
			});
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		Session session = Session.getActiveSession();
		Session.saveSession(session, outState);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
		if (requestCode == REQUEST_CODE_PICK_ACCOUNT) {
			if (resultCode == RESULT_OK) {
				mEmail = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
				getUsername();
			} else if (resultCode == RESULT_CANCELED) {
				Toast.makeText(this, getString(R.string.you_must_pick_an_account), Toast.LENGTH_SHORT).show();
			}
		} else if ((requestCode == REQUEST_CODE_RECOVER_FROM_AUTH_ERROR || requestCode == REQUEST_CODE_RECOVER_FROM_PLAY_SERVICES_ERROR) && resultCode == RESULT_OK) {
			handleAuthorizeResult(resultCode, data);
			return;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	private void handleAuthorizeResult(int resultCode, Intent data) {
		if (data == null) {
			showToast(getString(R.string.unknown_error_click_button));
			return;
		}
		if (resultCode == RESULT_OK) {
			showToast(getString(R.string.retrying));
			getTask(this, mEmail, SCOPE).execute();
			return;
		}
		if (resultCode == RESULT_CANCELED) {
			showToast(getString(R.string.user_rejected_authorization));
			return;
		}
		showToast(getString(R.string.unknown_error_click_button));
	}

	private AbstractGetNameTask getTask(LoginSocialActivity activity, String email, String scope) {

		return new GetNameInForeground(activity, email, scope);

	}

	/**
	 * This method is a hook for background threads and async tasks that need to
	 * provide the user a response UI when an exception occurs.
	 */
	public void handleException(final Exception e) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if (e instanceof GooglePlayServicesAvailabilityException) {
					// The Google Play services APK is old, disabled, or not
					// present.
					// Show a dialog created by Google Play services that allows
					// the user to update the APK
					int statusCode = ((GooglePlayServicesAvailabilityException) e).getConnectionStatusCode();
					Dialog dialog = GooglePlayServicesUtil.getErrorDialog(statusCode, LoginSocialActivity.this, REQUEST_CODE_RECOVER_FROM_PLAY_SERVICES_ERROR);
					dialog.show();
				} else if (e instanceof UserRecoverableAuthException) {
					// Unable to authenticate, such as when the user has not yet
					// granted
					// the app access to the account, but the user can fix this.
					// Forward the user to an activity in Google Play services.
					Intent intent = ((UserRecoverableAuthException) e).getIntent();
					startActivityForResult(intent, REQUEST_CODE_RECOVER_FROM_PLAY_SERVICES_ERROR);
				}
			}
		});
	}

	public void GetGoogleLoginInfo(final String resposne) {
//		Log.v(Constant.TAG, "resposne " + resposne);
		try {
			JSONObject jsonObject = new JSONObject(resposne);
			sErrorMessage = "";
			SharedPreferences pref = getSharedPreferences("mobstar_pref", MODE_PRIVATE);
			pref.edit().putBoolean("isSocialLogin",true).commit();
			new GoogleLoginCall(jsonObject.optString("id", "")
                    , jsonObject.optString("given_name", "")
                    , jsonObject.optString("name", "")
                    , jsonObject.optString("gender", "")
                    , jsonObject.optString("name", "")).start();
//			Intent i=new Intent(LoginSocialActivity.this,YouTubeData.class);
//			startActivity(i);

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	public void getGoogleToken(String token){
		googleToken=token;
//		SharedPreferences pref = getSharedPreferences("mobstar_pref", MODE_PRIVATE);
//		pref.edit().putString("googleToken", token).commit();
//		Log.d(Constant.TAG,"Google api token"+token);
	}

	public void showToast(final String message) {
		if (!isFinishing()) {
			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
				}
			});
		}
	}

	public void showError(final String message) {

		if (!isFinishing()) {
			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
					Utility.HideDialog(mContext);
				}
			});
		}

	}
	
	

}
