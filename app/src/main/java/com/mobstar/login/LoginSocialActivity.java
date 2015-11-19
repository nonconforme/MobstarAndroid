package com.mobstar.login;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;

import com.google.android.gms.plus.model.people.Person;
import com.mobstar.AdWordsManager;
import com.mobstar.R;
import com.mobstar.api.ConnectCallback;
import com.mobstar.api.new_api_call.AuthCall;
import com.mobstar.api.new_api_model.Login;
import com.mobstar.api.new_api_model.Profile;
import com.mobstar.api.new_api_model.Settings;
import com.mobstar.api.new_api_model.SocialType;
import com.mobstar.api.new_api_model.response.LoginResponse;
//import com.mobstar.custom.AbstractGetNameTask;
import com.mobstar.custom.CustomTextview;
import com.mobstar.custom.CustomTextviewBold;
//import com.mobstar.custom.GetNameInForeground;
import com.mobstar.geo_filtering.SelectCurrentRegionActivity;
import com.mobstar.help.WelcomeVideoActivity;
import com.mobstar.home.HomeActivity;
import com.mobstar.login.facebook.FacebookLoginDialog;
import com.mobstar.login.facebook.FacebookManager;
import com.mobstar.login.facebook.FacebookResponse;
import com.mobstar.login.google_plus.GooglePlusManager;
import com.mobstar.twitter.ImageTwitter;
import com.mobstar.twitter.ImageTwitter.OnCompleteListener;
import com.mobstar.utils.UserPreference;
import com.mobstar.utils.Utility;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.util.Collection;

public class LoginSocialActivity extends Activity implements OnClickListener, FacebookManager.OnFacebookSignInCompletedListener, GooglePlusManager.OnGooglePlusSignInCompletedListener {

	private LinearLayout btnGetStarted, btnSignIn;
	private CustomTextview btnLoginFB,  btnLoginTwitter, btnLoginGoogle;
	private CustomTextviewBold btnCountinueWOSignin;

//	private String sUserID = "", sToken = "", sUserFullName = "", sUserName = "", sUserDisplayName = "";
//	private String sErrorMessage = "";
//	private String ProfileImage = "", ProfileCover = "", UserTagLine = "",UserBio = "";
	private ImageTwitter mTweet;
	private GooglePlusManager googlePlusManager;

	// Google plus sign in
//	String mEmail;
	private static final String SCOPE = "oauth2:https://www.googleapis.com/auth/userinfo.profile";
//	private static final String SCOPE="https://www.googleapis.com/auth/youtube.readonly";
//	static final int REQUEST_CODE_PICK_ACCOUNT = 1000;
//	static final int REQUEST_CODE_RECOVER_FROM_AUTH_ERROR = 1001;
//	static final int REQUEST_CODE_RECOVER_FROM_PLAY_SERVICES_ERROR = 1002;
//	String googleToken="";
	private FacebookManager facebookManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		facebookManager = new FacebookManager(getApplicationContext(), this, this);
		setContentView(R.layout.activity_login_social);
		googlePlusManager = new GooglePlusManager(this, this);
		findViews();
		setListeners();
		Utility.SendDataToGA("LgoinSocial Screen", LoginSocialActivity.this);

	}

	private void findViews() {
		btnGetStarted = (LinearLayout) findViewById(R.id.btnGetStarted);
		btnSignIn = (LinearLayout) findViewById(R.id.btnSignIn);
		btnLoginFB = (CustomTextview) findViewById(R.id.btnLoginFB);
		btnLoginTwitter = (CustomTextview) findViewById(R.id.btnLoginTwitter);
		btnLoginGoogle = (CustomTextview) findViewById(R.id.btnLoginGoogle);
		btnCountinueWOSignin= (CustomTextviewBold) findViewById(R.id.btnCountinueWOSignin);
	}

	private void setListeners(){
		btnGetStarted.setOnClickListener(this);
		btnSignIn.setOnClickListener(this);
		btnLoginFB.setOnClickListener(this);
		btnLoginTwitter.setOnClickListener(this);
		btnLoginGoogle.setOnClickListener(this);
		btnCountinueWOSignin.setOnClickListener(this);
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()){
			case R.id.btnGetStarted:
				startSignUpActivity();
				break;
			case R.id.btnSignIn:
				startLoginActivity();
				break;
			case R.id.btnLoginFB:
				startLoginWithFacebookDialog();
				break;
			case R.id.btnLoginTwitter:
				loginWithTwitter();
				break;
			case R.id.btnLoginGoogle:
				googlePlusManager.signInWithGplus();
				break;
			case R.id.btnCountinueWOSignin:

				break;
		}
	}

	private void startLoginWithFacebookDialog(){
		final FacebookLoginDialog dialog = new FacebookLoginDialog(this);
		dialog.setOnAcceptListener(new FacebookLoginDialog.OnFacebookAcceptListener() {
			@Override
			public void onFacebookAccept() {
				dialog.dismiss();
				facebookManager.signInWithFacebook();
			}
		});
		dialog.show();
	}

	private void loginWithTwitter(){
		Utility.ShowProgressDialog(this, getString(R.string.loading));
		mTweet = new ImageTwitter(LoginSocialActivity.this, true, null,null);
		mTweet.setOnCompleteListener(new OnCompleteListener() {

			@Override
			public void onComplete(final String action) {
				// TODO Auto-generated method stub

				if (action.equals("Success")) {
					socialLoginRequest(mTweet.mUser.getName(), mTweet.mUser.getScreenName(), Long.toString(mTweet.mUser.getId()), SocialType.twitter);
//					if (Utility.isNetworkAvailable(mContext)) {
//						// Log.v(Constant.TAG, "User " +
//						// mTweet.mUser.toString());
//
//						sErrorMessage = "";
//						SharedPreferences pref = getSharedPreferences("mobstar_pref", MODE_PRIVATE);
//						pref.edit().putBoolean("isSocialLogin", true).commit();
//						new TwitterLoginCall(mTweet.mUser.getId() + "", mTweet.mUser.getScreenName(), mTweet.mUser.getName(), mTweet.mUser.getScreenName()).start();
//					} else {
//						Toast.makeText(mContext, getString(R.string.no_internet_access), Toast.LENGTH_SHORT).show();
//						Utility.HideDialog(mContext);
//					}
				} else {
					Utility.HideDialog(LoginSocialActivity.this);
				}

			}
		});
		mTweet.send();
	}

//	/** Called by button in the layout */
//	public void greetTheUser() {
//		int statusCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
//		if (statusCode == ConnectionResult.SUCCESS) {
//			getUsername();
//		} else if (GooglePlayServicesUtil.isUserRecoverableError(statusCode)) {
//			Dialog dialog = GooglePlayServicesUtil.getErrorDialog(statusCode, this, 0 /*
//																					 * request
//																					 * code
//																					 * not
//																					 * used
//																					 */);
//			dialog.show();
//		} else {
//			Toast.makeText(this, R.string.unrecoverable_error, Toast.LENGTH_SHORT).show();
//		}
//	}

	/**
	 * Attempt to get the user name. If the email address isn't known yet, then
	 * call pickUserAccount() method so the user can pick an account.
	 */
//	private void getUsername() {
//		if (mEmail == null) {
//			pickUserAccount();
//		} else {
//			if (Utility.isNetworkAvailable(mContext)) {
//				getTask(LoginSocialActivity.this, mEmail, SCOPE).execute();
//			} else {
//				Toast.makeText(mContext, getString(R.string.no_internet_access), Toast.LENGTH_SHORT).show();
//				Utility.HideDialog(mContext);
//			}
//		}
//	}

//	private void pickUserAccount() {
//		String[] accountTypes = new String[] { "com.google" };
//		Intent intent = AccountPicker.newChooseAccountIntent(null, null, accountTypes, false, null, null, null, null);
//		startActivityForResult(intent, REQUEST_CODE_PICK_ACCOUNT);
//	}

	@Override
	public void onFacebookLoginSuccess(FacebookResponse response) {
		socialLoginRequest(response.getName(), response.getName(), response.getId(), SocialType.facebook);
	}

	@Override
	public void onFacebookLoginFailure() {

	}

	@Override
	public void onGooglePlusSuccess(Person person) {
		socialLoginRequest(person.getDisplayName(), person.getNickname(), person.getId(), SocialType.google);
	}

//	private void loginWithFacebook(final FacebookResponse facebook){
//		try {
//			SharedPreferences pref = getSharedPreferences("mobstar_pref", MODE_PRIVATE);
//			pref.edit().putBoolean("isSocialLogin",true).commit();
//
//			new FBLoginCall(facebook.getId(), facebook.getName(), facebook.getEmail(), facebook.getName(),
//					facebook.getBirthday(), facebook.getGender(), facebook.getName()).start();
//		} catch (Exception e) {
//			// TODO: handle exception
//			e.printStackTrace();
//			Toast.makeText(mContext, getString(R.string.error_while_login_with_facebook), Toast.LENGTH_SHORT).show();
//			Utility.HideDialog(mContext);
//		}
//	}

	private boolean isSubsetOf(Collection<String> subset, Collection<String> superset) {
		for (String string : subset) {
			if (!superset.contains(string)) {
				return false;
			}
		}
		return true;
	}

	private void socialLoginRequest(String displayName, String fullName, String socialId, SocialType socialType){
		Utility.ShowProgressDialog(this, getString(R.string.loading));
		AuthCall.signSocial(this, displayName, fullName, socialId, socialType, new ConnectCallback<LoginResponse>() {
			@Override
			public void onSuccess(LoginResponse object) {
//				UserPreference.isSocialLoginToPreference(LoginSocialActivity.this, true);
				onLoginSuccess(object);
			}

			@Override
			public void onFailure(String error) {
				Utility.HideDialog(LoginSocialActivity.this);
			}

			@Override
			public void onServerError(com.mobstar.api.responce.Error error) {

			}
		});
	}

	private void onLoginSuccess(final LoginResponse loginResponse){
		Utility.HideDialog(this);
		if(loginResponse.getLogin() == null)
			return;
		final Login login = loginResponse.getLogin();
		final Profile profile = login.getProfile();
		if (profile != null){
//			UserPreference.saveUserProfileToPreference(this, profile, true);
			AdWordsManager.getInstance().sendSingupEvent();
			if (UserPreference.welcomeIsChecked(this)) {
				startWelcomeActivity();
			}else {
				if (login.getSettings() != null)
					verifyUserContinent(login.getSettings());
			}
		}
	}

	private void verifyUserContinent(final Settings settings){
		final String userContinents = settings.getContinent();
		if (userContinents == null || userContinents.equalsIgnoreCase("") || userContinents.equalsIgnoreCase("0"))
			startSelectCurrentRegionActivity();
		else startHomeActivity();

	}

//	class GoogleLoginCall extends Thread {
//
//		String displayName, userId, userName, gender, fullName;
//
//		public GoogleLoginCall(String userId, String userName, String displayName, String gender, String fullName) {
//			this.userId = userId;
//			this.displayName = displayName;
//			this.userName = userName;
//			this.gender = gender;
//			this.fullName = fullName;
//		}
//
//		@Override
//		public void run() {
//			// TODO Auto-generated method stub
//
//			String[] name = { "userName", "userId", "displayName", "gender", "fullName","deviceToken","device"};
//			String[] value = { userName, userId, displayName, gender, fullName,Utility.getRegistrationId(mContext),"google" };
//
//			String response = JSONParser.postRequest(Constant.SERVER_URL + Constant.GOOGLE_LOGIN, name, value, null);
//
////			Log.v(Constant.TAG, "GoogleLoginCall response " + response);
//
//			if (response != null) {
//
//				try {
//
//					JSONObject jsonObject = new JSONObject(response);
//
//					if (jsonObject.has("error")) {
//						sErrorMessage = jsonObject.getString("error");
//					}
//
//					if (jsonObject.has("token")) {
//						sToken = jsonObject.getString("token");
//					}
//
//					if (jsonObject.has("userId")) {
//						sUserID = jsonObject.getString("userId");
//					}
//
//					if (jsonObject.has("userName")) {
//						sUserName = jsonObject.getString("userName");
//					}
//
//					if (jsonObject.has("fullName")) {
//						sUserFullName = jsonObject.getString("fullName");
//					}
//
//					if (jsonObject.has("profileImage")) {
//						ProfileImage = jsonObject.getString("profileImage");
//					}
//
//					if (jsonObject.has("profileCover")) {
//						ProfileCover = jsonObject.getString("profileCover");
//					}
//
//					if (jsonObject.has("userTagline")) {
//						UserTagLine = jsonObject.getString("userTagline");
//					}
//
//					if (jsonObject.has("userBio")) {
//						UserBio = jsonObject.getString("userBio");
//					}
//
//					if (jsonObject.has("userDisplayName")) {
//						sUserDisplayName = jsonObject.getString("userDisplayName");
//					}
//
//					if (sErrorMessage != null && !sErrorMessage.equals("")) {
//						handlerLogin.sendEmptyMessage(0);
//					} else {
//						handlerLogin.sendEmptyMessage(1);
//					}
//
//				} catch (Exception e) {
//					// TODO: handle exception
//					e.printStackTrace();
//					handlerLogin.sendEmptyMessage(0);
//				}
//
//			} else {
//
//				handlerLogin.sendEmptyMessage(0);
//			}
//
//		}
//	}

//	class FBLoginCall extends Thread {
//
//
//		String displayName, username, dob, userId, email, gender, fullName;
//
//		public FBLoginCall(String userId, String username, String email, String displayName, String dob, String gender, String fullName) {
//			this.userId = userId;
//			this.username = username;
//			this.displayName = displayName;
//			this.email = email;
//			this.dob = dob;
//			this.gender = gender;
//			this.fullName = fullName;
//		}
//
//		@Override
//		public void run() {
//			// TODO Auto-generated method stub
//
//			String[] name = { "userName", "email", "userId", "displayName", "dob", "gender", "fullName","deviceToken","device" };
//			String[] value = { username, email, userId, displayName, dob, gender, fullName ,Utility.getRegistrationId(mContext),"google"};
//
//			String response = JSONParser.postRequest(Constant.SERVER_URL + Constant.FACEBOOK_LOGIN, name, value, null);
//
////			Log.v(Constant.TAG, "FBLoginCall response " + response);
//
//			if (response != null) {
//
//				try {
//
//					JSONObject jsonObject = new JSONObject(response);
//
//					if (jsonObject.has("error")) {
//						sErrorMessage = jsonObject.getString("error");
//					}
//
//					if (jsonObject.has("token")) {
//						sToken = jsonObject.getString("token");
//					}
//
//					if (jsonObject.has("userId")) {
//						sUserID = jsonObject.getString("userId");
//					}
//
//					if (jsonObject.has("userName")) {
//						sUserName = jsonObject.getString("userName");
//					}
//
//					if (jsonObject.has("fullName")) {
//						sUserFullName = jsonObject.getString("fullName");
//					}
//
//					if (jsonObject.has("userDisplayName")) {
//						sUserDisplayName = jsonObject.getString("userDisplayName");
//					}
//
//					if (jsonObject.has("profileImage")) {
//						ProfileImage = jsonObject.getString("profileImage");
//					}
//
//					if (jsonObject.has("profileCover")) {
//						ProfileCover = jsonObject.getString("profileCover");
//					}
//
//					if (jsonObject.has("userTagline")) {
//						UserTagLine = jsonObject.getString("userTagline");
//					}
//
//					if (sErrorMessage != null && !sErrorMessage.equals("")) {
//						handlerLogin.sendEmptyMessage(0);
//					} else {
//						handlerLogin.sendEmptyMessage(1);
//					}
//
//				} catch (Exception e) {
//					// TODO: handle exception
//					e.printStackTrace();
//					handlerLogin.sendEmptyMessage(0);
//				}
//
//			} else {
//
//				handlerLogin.sendEmptyMessage(0);
//			}
//
//		}
//	}

//	class TwitterLoginCall extends Thread {
//
//		String displayName, userId, FullName, UserName;
//
//		public TwitterLoginCall(String userId, String displayName, String FullName, String UserName) {
//			this.userId = userId;
//			this.displayName = displayName;
//			this.FullName = FullName;
//			this.UserName = UserName;
//		}
//
//		@Override
//		public void run() {
//			// TODO Auto-generated method stub
//
//			String[] name = { "userId", "displayName", "fullName", "userName" };
//			String[] value = { userId, displayName, FullName, UserName };
//
//			String response = JSONParser.postRequest(Constant.SERVER_URL + Constant.TWITTER_LOGIN, name, value, null);
//
////			Log.v(Constant.TAG, "TwitterLoginCall response " + response);
//
//			if (response != null) {
//
//				try {
//
//					JSONObject jsonObject = new JSONObject(response);
//
//					if (jsonObject.has("error")) {
//						sErrorMessage = jsonObject.getString("error");
//					}
//
//					if (jsonObject.has("token")) {
//						sToken = jsonObject.getString("token");
//					}
//
//					if (jsonObject.has("userId")) {
//						sUserID = jsonObject.getString("userId");
//					}
//
//					if (jsonObject.has("userName")) {
//						sUserName = jsonObject.getString("userName");
//					}
//
//					if (jsonObject.has("fullName")) {
//						sUserFullName = jsonObject.getString("fullName");
//					}
//
//					if (jsonObject.has("userDisplayName")) {
//						sUserDisplayName = jsonObject.getString("userDisplayName");
//					}
//
//					if (jsonObject.has("profileImage")) {
//						ProfileImage = jsonObject.getString("profileImage");
//					}
//
//					if (jsonObject.has("profileCover")) {
//						ProfileCover = jsonObject.getString("profileCover");
//					}
//
//					if (jsonObject.has("userTagline")) {
//						UserTagLine = jsonObject.getString("userTagline");
//					}
//
//					if (sErrorMessage != null && !sErrorMessage.equals("")) {
//						handlerLogin.sendEmptyMessage(0);
//					} else {
//						handlerLogin.sendEmptyMessage(1);
//					}
//
//				} catch (Exception e) {
//					// TODO: handle exception
//					e.printStackTrace();
//					handlerLogin.sendEmptyMessage(0);
//				}
//
//			} else {
//
//				handlerLogin.sendEmptyMessage(0);
//			}
//
//		}
//	}
//
//	Handler handlerLogin = new Handler() {
//
//		@Override
//		public void handleMessage(Message msg) {
//			// TODO Auto-generated method stub
//			Utility.HideDialog(mContext);
//
//			if (msg.what == 1) {
//
//				SharedPreferences pref = getSharedPreferences("mobstar_pref", MODE_PRIVATE);
//				pref.edit().putString("username", sUserName).commit();
//				pref.edit().putString("fullName", sUserFullName).commit();
//				pref.edit().putString("displayName", sUserDisplayName).commit();
//				pref.edit().putString("token", sToken).commit();
//				pref.edit().putString("userid", sUserID).commit();
//				pref.edit().putString("profile_image", ProfileImage).commit();
//				pref.edit().putString("cover_image", ProfileCover).commit();
//				pref.edit().putString("tagline", UserTagLine).commit();
//				pref.edit().putString("bio", UserBio).commit();
//				pref.edit().putBoolean("isLogin", true).commit();
//
//                AdWordsManager.getInstance().sendSingupEvent();
//				if (pref.getBoolean(WelcomeVideoActivity.WELCOME_IS_CHECKED, true)) {
//					startWelcomeActivity();
//				}else {
//					getUserAccountRequest();
//				}
//
//			} else {
//				OkayAlertDialog(sErrorMessage);
//			}
//		}
//	};

//	private void getUserAccountRequest(){
//		Utility.ShowProgressDialog(mContext, getString(R.string.loading));
//		RestClient.getInstance(this).getRequest(Constant.USER_ACCOUNT, null, new ConnectCallback<UserAccountResponse>() {
//			@Override
//			public void onSuccess(UserAccountResponse object) {
//				Utility.HideDialog(mContext);
//				if (object.getUser().getUserContinentId() == 0) {
//					startSelectCurrentRegionActivity();
//				} else startHomeActivity();
//			}
//
//			@Override
//			public void onFailure(String error) {
//				Utility.HideDialog(mContext);
//				startHomeActivity();
//			}
//
//			@Override
//			public void onServerError(Error error) {
//
//			}
//		});
//	}

//	void OkayAlertDialog(final String msg) {
//
//		if (!isFinishing()) {
//			runOnUiThread(new Runnable() {
//
//				@Override
//				public void run() {
//					// TODO Auto-generated method stub
//					AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);
//
//					// set title
//					alertDialogBuilder.setTitle(getResources().getString(R.string.app_name));
//
//					// set dialog message
//					alertDialogBuilder.setMessage(msg).setCancelable(false).setNeutralButton("OK", null);
//
//					// create alert dialog
//					AlertDialog alertDialog = alertDialogBuilder.create();
//
//					// show it
//					alertDialog.show();
//				}
//			});
//		}
//	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		facebookManager.onActivityResult(requestCode, resultCode, data);
		googlePlusManager.onActivityResult(requestCode, resultCode, data);
//		if (requestCode == REQUEST_CODE_PICK_ACCOUNT) {
//			if (resultCode == RESULT_OK) {
//				mEmail = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
//				getUsername();
//			} else if (resultCode == RESULT_CANCELED) {
//				Toast.makeText(this, getString(R.string.you_must_pick_an_account), Toast.LENGTH_SHORT).show();
//			}
//		} else if ((requestCode == REQUEST_CODE_RECOVER_FROM_AUTH_ERROR || requestCode == REQUEST_CODE_RECOVER_FROM_PLAY_SERVICES_ERROR) && resultCode == RESULT_OK) {
//			handleAuthorizeResult(resultCode, data);
//			return;
//		}
//		super.onActivityResult(requestCode, resultCode, data);
	}

//	private void handleAuthorizeResult(int resultCode, Intent data) {
//		if (data == null) {
//			showToast(getString(R.string.unknown_error_click_button));
//			return;
//		}
//		if (resultCode == RESULT_OK) {
//			showToast(getString(R.string.retrying));
//			getTask(this, mEmail, SCOPE).execute();
//			return;
//		}
//		if (resultCode == RESULT_CANCELED) {
//			showToast(getString(R.string.user_rejected_authorization));
//			return;
//		}
//		showToast(getString(R.string.unknown_error_click_button));
//	}
//
//	private AbstractGetNameTask getTask(LoginSocialActivity activity, String email, String scope) {
//
//		return new GetNameInForeground(activity, email, scope);
//
//	}

	/**
	 * This method is a hook for background threads and async tasks that need to
	 * provide the user a response UI when an exception occurs.
	 */
//	public void handleException(final Exception e) {
//		runOnUiThread(new Runnable() {
//			@Override
//			public void run() {
//				if (e instanceof GooglePlayServicesAvailabilityException) {
//					// The Google Play services APK is old, disabled, or not
//					// present.
//					// Show a dialog created by Google Play services that allows
//					// the user to update the APK
//					int statusCode = ((GooglePlayServicesAvailabilityException) e).getConnectionStatusCode();
//					Dialog dialog = GooglePlayServicesUtil.getErrorDialog(statusCode, LoginSocialActivity.this, REQUEST_CODE_RECOVER_FROM_PLAY_SERVICES_ERROR);
//					dialog.show();
//				} else if (e instanceof UserRecoverableAuthException) {
//					// Unable to authenticate, such as when the user has not yet
//					// granted
//					// the app access to the account, but the user can fix this.
//					// Forward the user to an activity in Google Play services.
//					Intent intent = ((UserRecoverableAuthException) e).getIntent();
//					startActivityForResult(intent, REQUEST_CODE_RECOVER_FROM_PLAY_SERVICES_ERROR);
//				}
//			}
//		});
//	}

//	public void GetGoogleLoginInfo(final String resposne) {
////		Log.v(Constant.TAG, "resposne " + resposne);
//		try {
//			JSONObject jsonObject = new JSONObject(resposne);
//			sErrorMessage = "";
//			SharedPreferences pref = getSharedPreferences("mobstar_pref", MODE_PRIVATE);
//			pref.edit().putBoolean("isSocialLogin",true).commit();
////			googleLoginCall(jsonObject.optString("name", ""), jsonObject.optString("given_name", ""), jsonObject.optString("id", ""));
////			new GoogleLoginCall(jsonObject.optString("id", "")
////                    , jsonObject.optString("given_name", "")
////                    , jsonObject.optString("name", "")
////                    , jsonObject.optString("gender", "")
////                    , jsonObject.optString("name", "")).start();
////			Intent i=new Intent(LoginSocialActivity.this,YouTubeData.class);
////			startActivity(i);
//
//		} catch (JSONException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//
//	}
	
//	public void getGoogleToken(String token){
//		googleToken=token;
////		SharedPreferences pref = getSharedPreferences("mobstar_pref", MODE_PRIVATE);
////		pref.edit().putString("googleToken", token).commit();
////		Log.d(Constant.TAG,"Google api token"+token);
//	}
//
//	public void showToast(final String message) {
//		if (!isFinishing()) {
//			runOnUiThread(new Runnable() {
//
//				@Override
//				public void run() {
//					// TODO Auto-generated method stub
//					Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
//				}
//			});
//		}
//	}
//
//	public void showError(final String message) {
//
//		if (!isFinishing()) {
//			runOnUiThread(new Runnable() {
//
//				@Override
//				public void run() {
//					// TODO Auto-generated method stub
//					Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
//					Utility.HideDialog(mContext);
//				}
//			});
//		}

//	}


	private void startHomeActivity(){
		final Intent intent = new Intent(this, HomeActivity.class);
		startActivity(intent);
		finish();
	}

	private void startWelcomeActivity(){
		final Intent intent = new Intent(this, WelcomeVideoActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
		finish();
	}

	private void startSelectCurrentRegionActivity(){
		final Intent intent = new Intent(this, SelectCurrentRegionActivity.class);
		startActivity(intent);
		finish();
	}

	private void startSignUpActivity(){
		final Intent intent = new Intent(this, SignUpActivity.class);
		startActivity(intent);
		finish();
	}

	private void startLoginActivity(){
		final Intent intent = new Intent(this, LoginActivity.class);
		startActivity(intent);
		finish();
	}

	@Override
	protected void onStart() {
		super.onStart();
		googlePlusManager.onStart();
	}

	@Override
	protected void onStop() {
		super.onStop();
		googlePlusManager.onStop();
	}
	
	

}
