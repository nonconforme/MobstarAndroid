package com.mobstar.login;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.mobstar.AdWordsManager;
import com.mobstar.R;
import com.mobstar.api.ConnectCallback;
import com.mobstar.api.RestClient;
import com.mobstar.api.new_api_call.LoginCall;
import com.mobstar.api.new_api_model.Login;
import com.mobstar.api.new_api_model.Profile;
import com.mobstar.api.new_api_model.Settings;
import com.mobstar.api.new_api_model.response.LoginResponse;
import com.mobstar.api.responce.UserAccountResponse;
import com.mobstar.geo_filtering.SelectCurrentRegionActivity;
import com.mobstar.help.WelcomeVideoActivity;
import com.mobstar.home.HomeActivity;
import com.mobstar.utils.Constant;
import com.mobstar.utils.JSONParser;
import com.mobstar.utils.UserPreference;
import com.mobstar.utils.Utility;

import org.json.JSONObject;

public class SignUpActivity extends Activity implements OnClickListener {

	private Button btnBack, btnFinish;

	private Typeface typefaceBtn;
	private EditText editFullName, editEmail, editDisplayName, editPassword, editConfirmPassword;
	private TextView textFullNameHint, textEmailHint, textDisplayNameHint, textPasswordHint, textConfirmPasswordHint;

//	private boolean isAlreadyRegistered = false;
//	private boolean isAlreadyTaken = false;
//	private String sUserID = "";
//	private String sToken = "";
//	private String ProfileImage = "", ProfileCover = "";

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
		typefaceBtn = Typeface.createFromAsset(getAssets(), "GOTHAM-BOLD.TTF");
		btnBack.setTypeface(typefaceBtn);
		btnFinish.setTypeface(typefaceBtn);
		editFullName.setTypeface(typefaceBtn);
		editEmail.setTypeface(typefaceBtn);
		editDisplayName.setTypeface(typefaceBtn);
		editPassword.setTypeface(typefaceBtn);
		editConfirmPassword.setTypeface(typefaceBtn);
	}

	private void settListeners(){
		btnBack.setOnClickListener(this);
		btnFinish.setOnClickListener(this);
		editFullName.setOnClickListener(this);
		editEmail.setOnClickListener(this);
		editDisplayName.setOnClickListener(this);
		editPassword.setOnClickListener(this);
		editConfirmPassword.setOnClickListener(this);

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
//			if (Utility.isNetworkAvailable(mContext)) {
//
//				new SignUPCall(editFullName.getText().toString().trim(), editEmail.getText().toString().trim(), editDisplayName.getText().toString().trim(), editPassword.getText().toString().trim()).start();
//
//			} else {
//
//				Toast.makeText(mContext, getString(R.string.no_internet_access), Toast.LENGTH_SHORT).show();
//				Utility.HideDialog(mContext);
//			}
		}
	}

	private void signUpRequest(final String email, final String fullName, final String displayName, final String password){
		LoginCall.signUpMail(this, email, fullName, displayName, password, new ConnectCallback<LoginResponse>() {
			@Override
			public void onSuccess(LoginResponse object) {
				Utility.HideDialog(SignUpActivity.this);
				onLoginSuccess(object);
			}

			@Override
			public void onFailure(String error) {
				Utility.HideDialog(SignUpActivity.this);
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
			if (UserPreference.welcomIsChecked(this)) {
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

//	class SignUPCall extends Thread {
//
//		String displayName, password, fullName, email;
//
//		public SignUPCall(String fullName, String email, String displayName, String password) {
//			this.fullName = fullName;
//			this.displayName = displayName;
//			this.email = email;
//			this.password = password;
//		}
//
//		@Override
//		public void run() {
//			// TODO Auto-generated method stub
//
//			String[] name = { "userName", "email", "fullName", "displayName", "password","deviceToken","device" };
//			String[] value = { displayName, email, fullName, displayName, password ,Utility.getRegistrationId(mContext),"google"};
//
//			String response = JSONParser.postRequest(Constant.SERVER_URL + Constant.SIGNUP, name, value, null);
//
////			Log.v(Constant.TAG, "SignUPCall response " + response);
//
//			if (response != null) {
//
//				try {
//
//					JSONObject jsonObject = new JSONObject(response);
//
//					if (jsonObject.has("token")) {
//						sToken = jsonObject.getString("token");
//					}
//
//					if (jsonObject.has("userId")) {
//						sUserID = jsonObject.getString("userId");
//					}
//
//					if (jsonObject.has("email") && jsonObject.getString("email").contains("already registered")) {
//						isAlreadyRegistered = true;
//					}
//
//					if (jsonObject.has("userName") && jsonObject.getString("userName").contains("already taken")) {
//						isAlreadyTaken = true;
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
//					if (isAlreadyRegistered || isAlreadyTaken) {
//						handlerSignUP.sendEmptyMessage(0);
//					} else {
//						handlerSignUP.sendEmptyMessage(1);
//					}
//
//				} catch (Exception e) {
//					// TODO: handle exception
//					e.printStackTrace();
//					handlerSignUP.sendEmptyMessage(0);
//				}
//
//			} else {
//
//				handlerSignUP.sendEmptyMessage(0);
//			}
//
//		}
//	}
//
//	Handler handlerSignUP = new Handler() {
//
//		@Override
//		public void handleMessage(Message msg) {
//			// TODO Auto-generated method stub
//			Utility.HideDialog(mContext);
//
//			if (msg.what == 1) {
//
//				SharedPreferences pref = getSharedPreferences("mobstar_pref", MODE_PRIVATE);
//				pref.edit().putString("username", editDisplayName.getText().toString().trim()).commit();
//				pref.edit().putString("fullName", editFullName.getText().toString().trim()).commit();
//				pref.edit().putString("displayName", editDisplayName.getText().toString().trim()).commit();
//				pref.edit().putString("token", sToken).commit();
//				pref.edit().putString("userid", sUserID).commit();
//				pref.edit().putString("email_address", editEmail.getText().toString()).commit();
//				pref.edit().putBoolean("isLogin", true).commit();
//				pref.edit().putString("profile_image", ProfileImage).commit();
//				pref.edit().putString("cover_image", ProfileCover).commit();
//
//
//                AdWordsManager.getInstance().sendSingupEvent();
//				if (pref.getBoolean(WelcomeVideoActivity.WELCOME_IS_CHECKED, true)) {
//					startWelcomeActivity();
//				}else {
//					getUserAccountRequest();
//				}
//
//
////				Intent intent = new Intent(mContext, VerifyMobileNoActivity.class);
////				startActivity(intent);
////				finish();
//
//			} else {
//
//				if (isAlreadyRegistered) {
//					editEmail.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.signup_cross, 0);
//					textEmailHint.setText(getString(R.string.already_registered));
//					textEmailHint.setVisibility(View.VISIBLE);
//					isAlreadyRegistered = false;
//				}
//				if (isAlreadyTaken) {
//					editDisplayName.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.signup_cross, 0);
//					textDisplayNameHint.setText(getString(R.string.already_taken));
//					textDisplayNameHint.setVisibility(View.VISIBLE);
//					isAlreadyTaken = false;
//				}
//
//			}
//		}
//	};

	private void startWelcomeActivity(){
		final Intent intent = new Intent(this, WelcomeVideoActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
		finish();
	}


//	private void getUserAccountRequest(){
//		Utility.ShowProgressDialog(this, getString(R.string.loading));
//		RestClient.getInstance(this).getRequest(Constant.USER_ACCOUNT, null, new ConnectCallback<UserAccountResponse>() {
//			@Override
//			public void onSuccess(UserAccountResponse object) {
//				Utility.HideDialog(SignUpActivity.this);
//				if (object.getUser().getUserContinentId() == 0) {
//					startSelectCurrentRegionActivity();
//				} else startHomeActivity();
//			}
//
//			@Override
//			public void onFailure(String error) {
//				Utility.HideDialog(SignUpActivity.this);
//				startHomeActivity();
//			}
//		});
//	}

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
