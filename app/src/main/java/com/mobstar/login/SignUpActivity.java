package com.mobstar.login;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.mobstar.AdWordsManager;
import com.mobstar.R;
import com.mobstar.api.ConnectCallback;
import com.mobstar.api.new_api_call.AuthCall;
import com.mobstar.api.new_api_model.Login;
import com.mobstar.api.new_api_model.Profile;
import com.mobstar.api.new_api_model.Settings;
import com.mobstar.api.new_api_model.response.LoginResponse;
import com.mobstar.geo_filtering.SelectCurrentRegionActivity;
import com.mobstar.help.WelcomeVideoActivity;
import com.mobstar.home.HomeActivity;
import com.mobstar.utils.UserPreference;
import com.mobstar.utils.Utility;

public class SignUpActivity extends Activity implements OnClickListener {

	private Button btnBack, btnFinish;
	private EditText editFullName, editEmail, editDisplayName, editPassword, editConfirmPassword;
	private TextView textFullNameHint, textEmailHint, textDisplayNameHint, textPasswordHint, textConfirmPasswordHint;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sign_up);
		initControls();
		Utility.SendDataToGA("SignUp Screen", SignUpActivity.this);

	}

	private void initControls() {
		findViews();
		setTypeface();
		settListeners();
		setTextChangeListeners();
		configureViews();
	}

	private void findViews(){
		btnBack                  = (Button) findViewById(R.id.btnBack);
		editFullName             = (EditText) findViewById(R.id.editFullName);
		btnFinish                = (Button) findViewById(R.id.btnFinish);
		editEmail                = (EditText) findViewById(R.id.editEmail);
		editDisplayName          = (EditText) findViewById(R.id.editDisplayName);
		editPassword             = (EditText) findViewById(R.id.editPassword);
		editConfirmPassword      = (EditText) findViewById(R.id.editConfirmPassword);
		textFullNameHint         = (TextView) findViewById(R.id.textFullNameHint);
		textEmailHint            = (TextView) findViewById(R.id.textEmailHint);
		textDisplayNameHint      = (TextView) findViewById(R.id.textDisplayNameHint);
		textPasswordHint         = (TextView) findViewById(R.id.textPasswordHint);
		textConfirmPasswordHint  = (TextView) findViewById(R.id.textConfirmPasswordHint);
	}

	private void setTypeface(){
		final Typeface typefaceBtn = Typeface.createFromAsset(getAssets(), "GOTHAM-BOLD.TTF");
		final TextView[] views = {btnBack, btnFinish, editFullName, editEmail, editDisplayName, editPassword, editConfirmPassword};
		for (TextView view : views) view.setTypeface(typefaceBtn);

	}

	private void settListeners(){
		final View[] views = {btnBack, btnFinish, editFullName, editEmail, editDisplayName, editPassword, editConfirmPassword};
		for (View view : views) view.setOnClickListener(this);
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()){
			case R.id.btnBack:
				startLoginSocialActivity();
				break;
			case R.id.btnFinish:
				onClickBtnFinish();
				break;
			case R.id.editFullName:

				break;
			case R.id.editEmail:

				break;
			case R.id.editDisplayName:

				break;
			case R.id.editPassword:

				break;
			case R.id.editConfirmPassword:

				break;
		}
	}

	private void configureViews(){
		textFullNameHint.setVisibility(View.INVISIBLE);
		textEmailHint.setVisibility(View.INVISIBLE);
		textDisplayNameHint.setVisibility(View.INVISIBLE);
		textPasswordHint.setVisibility(View.INVISIBLE);
		textConfirmPasswordHint.setVisibility(View.INVISIBLE);
	}


	private void setTextChangeListeners(){
		editFullName.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				editFullName.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
				textFullNameHint.setVisibility(View.INVISIBLE);
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});


		editEmail.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				editEmail.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
				textEmailHint.setVisibility(View.INVISIBLE);
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub

			}
		});


		editDisplayName.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				editDisplayName.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
				textDisplayNameHint.setVisibility(View.INVISIBLE);
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub

			}
		});


		editPassword.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				editPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
				textPasswordHint.setVisibility(View.INVISIBLE);
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub

			}
		});


		editConfirmPassword.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				editConfirmPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
				textConfirmPasswordHint.setVisibility(View.INVISIBLE);
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub

			}
		});
	}

	private void onClickBtnFinish(){
		boolean isValid = true;

		if (editFullName.getText().toString().trim().length() == 0) {

			editFullName.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.signup_cross, 0);
			textFullNameHint.setText(getString(R.string.enter_full_name));
			textFullNameHint.setVisibility(View.VISIBLE);

			isValid = false;
		} else {
			editFullName.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.signup_tick, 0);
			textFullNameHint.setVisibility(View.INVISIBLE);
		}
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
		if (editDisplayName.getText().toString().trim().length() == 0) {
			editDisplayName.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.signup_cross, 0);
			textDisplayNameHint.setText(getString(R.string.enter_display_name));
			textDisplayNameHint.setVisibility(View.VISIBLE);
			isValid = false;
		} else {
			editDisplayName.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.signup_tick, 0);
			textDisplayNameHint.setVisibility(View.INVISIBLE);
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

		if (editConfirmPassword.getText().toString().trim().length() == 0) {
			editConfirmPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.signup_cross, 0);
			textConfirmPasswordHint.setText(getString(R.string.enter_confirm_password));
			textConfirmPasswordHint.setVisibility(View.VISIBLE);
			isValid = false;
		} else if (!editConfirmPassword.getText().toString().trim().equals(editPassword.getText().toString().trim())) {
			editConfirmPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.signup_cross, 0);
			textConfirmPasswordHint.setText(getString(R.string.password_not_match));
			textConfirmPasswordHint.setVisibility(View.VISIBLE);
			isValid = false;
		} else {
			editConfirmPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.signup_tick, 0);
			textConfirmPasswordHint.setVisibility(View.INVISIBLE);
		}

		if (isValid) {
			Utility.ShowProgressDialog(this, getString(R.string.loading));
			signUpRequest(editEmail.getText().toString().trim(), editFullName.getText().toString().trim(), editDisplayName.getText().toString().trim(), editPassword.getText().toString().trim());

		}
	}

	private void signUpRequest(final String email, final String fullName, final String displayName, final String password){
		AuthCall.signUpMail(this, email, fullName, displayName, password, new ConnectCallback<LoginResponse>() {
			@Override
			public void onSuccess(LoginResponse object) {
				Utility.HideDialog(SignUpActivity.this);
				onLoginSuccess(object);
			}

			@Override
			public void onFailure(String error) {
				Utility.HideDialog(SignUpActivity.this);
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

	private void startHomeActivity(){
		final Intent intent = new Intent(this, HomeActivity.class);
		startActivity(intent);
		finish();
	}

	private void startLoginSocialActivity(){
		final Intent intent = new Intent(this, LoginSocialActivity.class);
		startActivity(intent);
		finish();
	}

	@Override
	public void onBackPressed() {
		startLoginSocialActivity();
	}

}
