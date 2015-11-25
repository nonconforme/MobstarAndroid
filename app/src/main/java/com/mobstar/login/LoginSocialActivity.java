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
import com.mobstar.custom.CustomTextview;
import com.mobstar.custom.CustomTextviewBold;
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

public class LoginSocialActivity extends Activity implements OnClickListener, FacebookManager.OnFacebookSignInCompletedListener, GooglePlusManager.OnGooglePlusSignInCompletedListener {

	private LinearLayout btnGetStarted, btnSignIn;
	private CustomTextview btnLoginFB,  btnLoginTwitter, btnLoginGoogle;
	private CustomTextviewBold btnCountinueWOSignin;
	private ImageTwitter mTweet;
	private GooglePlusManager googlePlusManager;
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

				} else {
					Utility.HideDialog(LoginSocialActivity.this);
				}

			}
		});
		mTweet.send();
	}

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

	private void socialLoginRequest(String displayName, String fullName, String socialId, SocialType socialType){
		Utility.ShowProgressDialog(this, getString(R.string.loading));
		AuthCall.signSocial(this, displayName, fullName, socialId, socialType, new ConnectCallback<LoginResponse>() {
			@Override
			public void onSuccess(LoginResponse object) {
				UserPreference.isSocialLoginToPreference(LoginSocialActivity.this, true);
				onLoginSuccess(object);
			}

			@Override
			public void onFailure(String error) {
				Utility.HideDialog(LoginSocialActivity.this);
			}

			@Override
			public void onServerError(com.mobstar.api.responce.Error error) {
				Utility.HideDialog(LoginSocialActivity.this);
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
			UserPreference.saveUserProfileToPreference(this, profile, true, login.getSettings().getContinent());
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

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		facebookManager.onActivityResult(requestCode, resultCode, data);
		googlePlusManager.onActivityResult(requestCode, resultCode, data);
	}

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
