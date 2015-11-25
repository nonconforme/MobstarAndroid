package com.mobstar.login;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mobstar.AdWordsManager;
import com.mobstar.R;
import com.mobstar.api.ConnectCallback;
import com.mobstar.api.new_api_call.AuthCall;
import com.mobstar.api.new_api_model.Login;
import com.mobstar.api.new_api_model.Profile;
import com.mobstar.api.new_api_model.Settings;
import com.mobstar.api.new_api_model.response.LoginResponse;
import com.mobstar.api.new_api_model.response.SuccessResponse;
import com.mobstar.geo_filtering.SelectCurrentRegionActivity;
import com.mobstar.help.WelcomeVideoActivity;
import com.mobstar.login.who_to_follow.WhoToFollowActivity;
import com.mobstar.utils.UserPreference;
import com.mobstar.utils.Utility;

public class LoginActivity extends Activity implements OnClickListener {

	private Button btnBack, btnLogin;
	private Typeface typefaceBtn;
	private EditText editEmail, editPassword;
	private TextView textEmailHint, textPasswordHint;
	private LinearLayout btnNewUser, btnResetPassword;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		initControls();
		Utility.SendDataToGA("Lgoin Screen", LoginActivity.this);
	}

	private void initControls() {
		findViews();
		setListeners();
		setTypeface();
		setTextChangeListeners();
		textEmailHint.setVisibility(View.INVISIBLE);
		textPasswordHint.setVisibility(View.INVISIBLE);
	}

	private void findViews(){
		btnNewUser        = (LinearLayout) findViewById(R.id.btnNewUser);
		btnResetPassword  = (LinearLayout) findViewById(R.id.btnResetPassword);
		btnBack           = (Button) findViewById(R.id.btnBack);
		btnLogin          = (Button) findViewById(R.id.btnLogin);
		editEmail         = (EditText) findViewById(R.id.editEmail);
		editPassword      = (EditText) findViewById(R.id.editPassword);
		textEmailHint     = (TextView) findViewById(R.id.textEmailHint);
		textPasswordHint  = (TextView) findViewById(R.id.textPasswordHint);
	}

	private void setListeners(){
		btnNewUser.setOnClickListener(this);
		btnResetPassword.setOnClickListener(this);
		btnBack.setOnClickListener(this);
		btnLogin.setOnClickListener(this);

	}

	private void setTypeface(){
		typefaceBtn = Typeface.createFromAsset(getAssets(), "GOTHAM-BOLD.TTF");
		btnBack.setTypeface(typefaceBtn);
		btnLogin.setTypeface(typefaceBtn);
		editEmail.setTypeface(typefaceBtn);
		editPassword.setTypeface(typefaceBtn);

	}

	private void setTextChangeListeners(){
		editEmail.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				editEmail.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
				textEmailHint.setVisibility(View.INVISIBLE);
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});

		editPassword.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				editPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
				textPasswordHint.setVisibility(View.INVISIBLE);
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()){
			case R.id.btnBack:
				startLoginSocialActivity();
				break;
			case R.id.btnLogin:
				onClickLogin();
				break;
			case R.id.btnNewUser:
				startSignUpActivity();
				break;
			case R.id.btnResetPassword:
				startResetPasswordActivity();
				break;
		}
	}

	private void onClickLogin(){
		boolean isValid = true;

		if (editEmail.getText().toString().trim().length() == 0) {
			editEmail.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.signup_cross, 0);
			textEmailHint.setText(getString(R.string.enter_email_address));
			textEmailHint.setVisibility(View.VISIBLE);
			isValid = false;
		} else if (!Utility.IsValidEmail(editEmail)) {
			editEmail.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.signup_cross, 0);
			textEmailHint.setText(getString(R.string.enter_valid_email_address));
			textEmailHint.setVisibility(View.VISIBLE);
			isValid = false;
		} else {
			editEmail.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.signup_tick, 0);
			textEmailHint.setVisibility(View.INVISIBLE);
		}

		if (editPassword.getText().toString().trim().length() == 0) {
			editPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.signup_cross, 0);
			textPasswordHint.setText(getString(R.string.enter_password));
			textPasswordHint.setVisibility(View.VISIBLE);
			isValid = false;
		} else {
			editPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.signup_tick, 0);
			textPasswordHint.setVisibility(View.INVISIBLE);
		}

		if (isValid) {
			Utility.ShowProgressDialog(this, getString(R.string.loading));
			loginRequest(editEmail.getText().toString().trim(), editPassword.getText().toString().trim());
		}
	}

	private void loginRequest(final String email, final String password){
		AuthCall.signInMail(this, email, password, new ConnectCallback<LoginResponse>() {
			@Override
			public void onSuccess(LoginResponse object) {
				onLoginSuccess(object);
			}

			@Override
			public void onFailure(String error) {
				Utility.HideDialog(LoginActivity.this);
			}

			@Override
			public void onServerError(com.mobstar.api.responce.Error error) {

			}
		});
	}

	private void onLoginSuccess(final LoginResponse loginResponse){
		Utility.HideDialog(this);
		AdWordsManager.getInstance().sendSingupEvent();
		sendAnalytic();
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
		else startWhoToFollowActivity();

	}

	private void sendAnalytic(){
		AuthCall.sendUserAnalytic(this, new ConnectCallback<SuccessResponse>() {
			@Override
			public void onSuccess(SuccessResponse object) {

			}

			@Override
			public void onFailure(String error) {

			}

			@Override
			public void onServerError(com.mobstar.api.responce.Error error) {

			}
		});
	}

	private void startSelectCurrentRegionActivity(){
		final Intent intent = new Intent(this, SelectCurrentRegionActivity.class);
		startActivity(intent);
		finish();
	}

	private void startWhoToFollowActivity(){
		final Intent intent = new Intent(this, WhoToFollowActivity.class);
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

	private void startLoginSocialActivity(){
		final Intent intent = new Intent(this, LoginSocialActivity.class);
		startActivity(intent);
		finish();
	}

	private void startSignUpActivity(){
		final Intent intent = new Intent(this, SignUpActivity.class);
		startActivity(intent);
		finish();
	}

	private void startResetPasswordActivity(){
		final Intent intent = new Intent(this, ResetPasswordActivity.class);
		startActivity(intent);
		finish();
	}


	
}
