package com.mobstar.login;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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
import com.mobstar.geo_filtering.SelectCurrentRegionActivity;
import com.mobstar.help.WelcomeVideoActivity;
import com.mobstar.login.who_to_follow.WhoToFollowActivity;
import com.mobstar.utils.Constant;
import com.mobstar.utils.JSONParser;
import com.mobstar.utils.UserPreference;
import com.mobstar.utils.Utility;

import org.json.JSONObject;

import java.util.List;

public class LoginActivity extends Activity implements OnClickListener {

	private Button btnBack, btnLogin;
	private Typeface typefaceBtn;
	private EditText editEmail, editPassword;
	private TextView textEmailHint, textPasswordHint;
	private LinearLayout btnNewUser, btnResetPassword;
	private String sErrorMessage = "";

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
		sendAnalytics();
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
		else startWhoToFollowActivity();

	}


	private void sendAnalytics(){
		String myVersion = android.os.Build.VERSION.RELEASE; // e.g. myVersion := "1.6"
		
		String packageToCheck = "com.mobstar";  
		
		String versionName="";
		int versionCode = 0;

		List<PackageInfo> packages = getPackageManager().getInstalledPackages(0);
		for (int i=0; i<packages.size(); i++) {
		    PackageInfo p = packages.get(i);
		    if (p.packageName.contains(packageToCheck)) {
		        String name = p.applicationInfo.loadLabel(getPackageManager()).toString();
		        String pname = p.applicationInfo.loadLabel(getPackageManager()).toString();
		        String packageName = p.packageName;
		        versionName = p.versionName;
		        versionCode = p.versionCode;
		        Log.i("mobstar", name + ": " + pname + ": " + packageName + ": " + versionName + ": " + versionCode);
		    }
		}
		
		String deviceName=getDeviceName();
		
		if (Utility.isNetworkAvailable(this)) {
			Log.d("log_tag","versionName"+versionName);
			Log.d("log_tag","versionCode"+versionCode);
			Log.d("log_tag","Analytics data==>osVersion "+myVersion +" appVersion "+versionCode+" deviceName "+deviceName);
			new AddAnalytics(myVersion,versionCode,deviceName).start();

		} else {
			
		}
		
	}
	
	public String getDeviceName() {
		   String manufacturer = Build.MANUFACTURER;
		   String model = Build.MODEL;
		   if (model.startsWith(manufacturer)) {
		      return capitalize(model);
		   } else {
		      return capitalize(manufacturer) + " " + model;
		   }
		}


		private String capitalize(String s) {
		    if (s == null || s.length() == 0) {
		        return "";
		    }
		    char first = s.charAt(0);
		    if (Character.isUpperCase(first)) {
		        return s;
		    } else {
		        return Character.toUpperCase(first) + s.substring(1);
		    }
		}
	
	class AddAnalytics extends Thread {
		
		String osVersion,appVersion,deviceName;
		SharedPreferences pref;
		
		public AddAnalytics(String os,int appV,String deviceName){
			this.osVersion=os;
			this.appVersion=String.valueOf(appV);
			this.deviceName=deviceName;
			pref = getSharedPreferences("mobstar_pref", MODE_PRIVATE);
		}

		@Override
		public void run() {
			
			String[] name = {"platform","osversion","appversion","devicename"};
			String[] value = {"Android",osVersion,appVersion,deviceName};
			String response = JSONParser.postRequest(Constant.SERVER_URL + Constant.ANALYTICS_READ, name, value,pref.getString("token", null));

			Log.v(Constant.TAG, "home info response " + response);

			if (response != null) {

				try {
					Thread.sleep(10000);
					JSONObject jsonObject = new JSONObject(response);

					if (jsonObject.has("error")) {
						sErrorMessage = jsonObject.getString("error");
					}

					if (sErrorMessage != null && !sErrorMessage.equals("")) {
						handlerAnalytics.sendEmptyMessage(0);
					} else {
						handlerAnalytics.sendEmptyMessage(1);
					}

				} catch (Exception e) {
					e.printStackTrace();
					handlerAnalytics.sendEmptyMessage(0);
				}

			} else {

				handlerAnalytics.sendEmptyMessage(0);
			}

		}
	}



	Handler handlerAnalytics = new Handler() {

		@Override
		public void handleMessage(Message msg) {
//			Utility.HideDialog(mContext);

			if (msg.what == 1) {
			
			} else {
			}
		}
	};

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
