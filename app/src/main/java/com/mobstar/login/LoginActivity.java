package com.mobstar.login;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mobstar.R;
import com.mobstar.help.WelcomeVideoActivity;
import com.mobstar.utils.Constant;
import com.mobstar.utils.JSONParser;
import com.mobstar.utils.Utility;

public class LoginActivity extends Activity implements OnClickListener {

	Context mContext;

	Button btnBack, btnLogin;

	Typeface typefaceBtn;

	EditText editEmail, editPassword;
	TextView textEmailHint, textPasswordHint;

	ImageView btnNewUser, btnResetPassword;
	String sUserID = "", sToken = "", sUserFullName = "", sUserName = "", sUserDisplayName = "";
	String ProfileImage = "", ProfileCover = "", UserTagLine = "",UserBio;
	String sErrorMessage = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		mContext = LoginActivity.this;

		InitControls();

		Utility.SendDataToGA("Lgoin Screen", LoginActivity.this);
	}

	void InitControls() {

		typefaceBtn = Typeface.createFromAsset(getAssets(), "GOTHAM-BOLD.TTF");

		btnNewUser = (ImageView) findViewById(R.id.btnNewUser);
		btnNewUser.setOnClickListener(this);

		btnResetPassword = (ImageView) findViewById(R.id.btnResetPassword);
		btnResetPassword.setOnClickListener(this);

		btnBack = (Button) findViewById(R.id.btnBack);
		btnBack.setTypeface(typefaceBtn);
		btnBack.setOnClickListener(this);

		btnLogin = (Button) findViewById(R.id.btnLogin);
		btnLogin.setTypeface(typefaceBtn);
		btnLogin.setOnClickListener(this);

		editEmail = (EditText) findViewById(R.id.editEmail);
		editEmail.setTypeface(typefaceBtn);
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

		editPassword = (EditText) findViewById(R.id.editPassword);
		editPassword.setTypeface(typefaceBtn);
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

		textEmailHint = (TextView) findViewById(R.id.textEmailHint);
		textEmailHint.setVisibility(View.INVISIBLE);

		textPasswordHint = (TextView) findViewById(R.id.textPasswordHint);
		textPasswordHint.setVisibility(View.INVISIBLE);

		 //editEmail.setText("dhavalk.spaceo@gmail.com");
		 //editPassword.setText("123123");
	}

	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		if (btnBack.equals(view)) {
			Intent intent = new Intent(mContext, LoginSocialActivity.class);
			startActivity(intent);
			finish();
		} else if (btnLogin.equals(view)) {

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
				Utility.ShowProgressDialog(mContext, getString(R.string.loading));
				sErrorMessage = "";
				if (Utility.isNetworkAvailable(mContext)) {
//					String s=Utility.getRegistrationId(mContext);
//					Log.d("mobstar","Login=>"+s);
					new LoginCall(editEmail.getText().toString().trim(), editPassword.getText().toString().trim()).start();

				} else {

					Toast.makeText(mContext, getString(R.string.no_internet_access), Toast.LENGTH_SHORT).show();
					Utility.HideDialog(mContext);
				}
			}

		} else if (view.equals(btnNewUser)) {
			Intent intent = new Intent(mContext, SignUpActivity.class);
			startActivity(intent);
			finish();
		} else if (view.equals(btnResetPassword)) {
			Intent intent = new Intent(mContext, ResetPasswordActivity.class);
			startActivity(intent);
			finish();
		}
	}

	class LoginCall extends Thread {

		String password, email;

		public LoginCall(String email, String password) {

			this.email = email;
			this.password = password;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
//			Log.d("mobstar","GCM RegId is=>"+Utility.getRegistrationId(mContext));

			String[] name = { "email", "password","deviceToken","device"};
			String[] value = { email, password,Utility.getRegistrationId(mContext),"google" };

			String response = JSONParser.postRequest(Constant.SERVER_URL + Constant.LOGIN, name, value,null);

			Log.v(Constant.TAG, "LoginCall response " + response);

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

					if (jsonObject.has("userFullName")) {
						sUserFullName = jsonObject.getString("userFullName");
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
					
					if (jsonObject.has("userBio")) {
						UserBio = jsonObject.getString("userBio");
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

	Handler handlerLogin = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			Utility.HideDialog(mContext);

			if (msg.what == 1) {

				SharedPreferences pref = getSharedPreferences("mobstar_pref", MODE_PRIVATE);
				
				pref.edit().putString("token", sToken).commit();
				pref.edit().putString("userid", sUserID).commit();
				pref.edit().putString("email_address", editEmail.getText().toString()).commit();
				
				
//				pref.edit().putString("username", sUserName).commit();
//				pref.edit().putString("fullName", sUserFullName).commit();
//				pref.edit().putString("displayName", sUserDisplayName).commit();
//				pref.edit().putString("profile_image", ProfileImage).commit();
//				pref.edit().putString("cover_image", ProfileCover).commit();
//				pref.edit().putString("tagline", UserTagLine).commit();
//				pref.edit().putString("bio", UserBio).commit();
//				pref.edit().putBoolean("isLogin", true).commit();
//				pref.edit().putBoolean("isVerifyMobileCode",true).commit();

//				Intent intent = new Intent(mContext, HomeActivity.class);
//				startActivity(intent);
//				finish();
				
				Utility.ShowProgressDialog(mContext, getString(R.string.loading));
				sErrorMessage = "";
				if (Utility.isNetworkAvailable(mContext)) {
//					String s=Utility.getRegistrationId(mContext);
//					Log.d("mobstar","Login=>"+s);
					new GetProfileCall(sUserID).start();

				} else {

					Toast.makeText(mContext, getString(R.string.no_internet_access), Toast.LENGTH_SHORT).show();
					Utility.HideDialog(mContext);
				}

			} else {

				editEmail.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.signup_cross, 0);
				textEmailHint.setText("");
				textEmailHint.setVisibility(View.VISIBLE);

				editPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.signup_cross, 0);
				textPasswordHint.setText("");
				textPasswordHint.setVisibility(View.VISIBLE);

				OkayAlertDialog(sErrorMessage);
			}
		}
	};

	class GetProfileCall extends Thread {

		String UserID;

		public GetProfileCall(String UserID) {
			this.UserID = UserID;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub

			String response = JSONParser.getRequest(Constant.SERVER_URL + Constant.GET_PROFILE + UserID,sToken);

//			Log.v(Constant.TAG, "GetProfile response " + response);

			if (response != null) {

				String ErrorMessage = "";

				try {

					JSONObject jsonObject = new JSONObject(response);

					if (jsonObject.has("error")) {
						ErrorMessage = jsonObject.getString("error");
					}

					if (jsonObject.has("users")) {

						JSONArray jsonArrayUser = jsonObject.getJSONArray("users");

						if (jsonArrayUser.length() > 0) {

							JSONObject jsonObj = jsonArrayUser.getJSONObject(0);

							if (jsonObj.has("user")) {
								JSONObject jsonObjUser = jsonObj.getJSONObject("user");

								// entryPojo.setUserID(jsonObjUser.getString("id"));
								// entryPojo.setUserName(jsonObjUser.getString("userName"));
								// entryPojo.setUserDisplayName(jsonObjUser.getString("displayName"));
								// entryPojo.setProfileImage(jsonObjUser.getString("profileImage"));
								// entryPojo.setProfileCover(jsonObjUser.getString("profileCover"));
								// entryPojo.setTagline(jsonObjUser.getString("tagLine"));
								//								UserName = jsonObjUser.getString("userName");
								//								UserDisplayName = jsonObjUser.getString("displayName");
								sUserName=jsonObjUser.getString("userName");
								sUserFullName=jsonObjUser.getString("fullName");
								sUserDisplayName=jsonObjUser.getString("displayName");
								ProfileImage=jsonObjUser.getString("profileImage");
								ProfileCover=jsonObjUser.getString("profileCover");
								UserTagLine=jsonObjUser.getString("tagLine");
								UserBio=jsonObjUser.getString("bio");
							}
						}
					}

					if (ErrorMessage != null && !ErrorMessage.equals("")) {
						handlerProfile.sendEmptyMessage(0);
					} else {
						handlerProfile.sendEmptyMessage(1);
					}

				} catch (Exception exception) {
					// TODO: handle exception
					exception.printStackTrace();
					handlerProfile.sendEmptyMessage(0);
				}

			} else {
				handlerProfile.sendEmptyMessage(0);
			}

		}
	}

	Handler handlerProfile = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			Utility.HideDialog(mContext);

			if (msg.what == 1) {
				//				if (UserCoverImage.equals("")) {
				//					imgCoverPage.setBackgroundResource(R.drawable.cover_bg);
				//				} else {
				//					imgCoverPage.setBackgroundResource(R.drawable.cover_bg);
				//
				//					Picasso.with(mContext).load(UserCoverImage).fit().centerCrop().placeholder(R.drawable.cover_bg).error(R.drawable.cover_bg).into(imgCoverPage);
				//				}
				//
				//				if (UserPic.equals("")) {
				//					imgUserPic.setImageResource(R.drawable.profile_pic_new);
				//				} else {
				//					imgUserPic.setImageResource(R.drawable.profile_pic_new);
				//
				//					Picasso.with(mContext).load(UserPic).resize(Utility.dpToPx(mContext, 126), Utility.dpToPx(mContext, 126)).centerCrop().placeholder(R.drawable.profile_pic_new)
				//					.error(R.drawable.profile_pic_new).transform(new RoundedTransformation(Utility.dpToPx(mContext, 126), 0)).into(imgUserPic);
				//				}
				//
				//				textUserName.setText(UserName);
				//				textUserDisplayName.setText(UserDisplayName);
//				textTagline.setText(StringEscapeUtils.unescapeJava(UserTagline));

				//				UpdatesFragment updatesFragment = new UpdatesFragment();
				//				Bundle extras = new Bundle();
				//				extras.putString("UserID",UserID);
				//				updatesFragment.setArguments(extras);
				//				replaceFragment(updatesFragment, "UpdatesFragment");

				//add dynamically layout header height
				//				ViewGroup.LayoutParams params = topTransparent.getLayoutParams();
				//				params.height = llHeader.getHeight()+150;
				//				topTransparent.setLayoutParams(params);
				//				topTransparent.requestLayout();

				SharedPreferences pref = getSharedPreferences("mobstar_pref", MODE_PRIVATE);
				pref.edit().putString("username", sUserName).commit();
				pref.edit().putString("fullName", sUserFullName).commit();
				pref.edit().putString("displayName", sUserDisplayName).commit();
				pref.edit().putString("profile_image", ProfileImage).commit();
				pref.edit().putString("cover_image", ProfileCover).commit();
				pref.edit().putString("tagline", UserTagLine).commit();
				pref.edit().putString("bio", UserBio).commit();
				pref.edit().putBoolean("isLogin", true).commit();
				pref.edit().putBoolean("isVerifyMobileCode",true).commit();

//				Intent intent = new Intent(mContext, HomeActivity.class);
//				startActivity(intent);
//				finish();
				
				sendAnalytics();
				
				if (pref.getBoolean(WelcomeVideoActivity.WELCOME_IS_CHECKED, true)) {
					Intent intent = new Intent(mContext, WelcomeVideoActivity.class);
					startActivity(intent);
					finish();
				}
				else {
					Intent intent = new Intent(mContext, WhoToFollowActivity.class);
					startActivity(intent);
					finish();
				}
				

			} else {

			}
		}
	};
	
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
		
		if (Utility.isNetworkAvailable(mContext)) {
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
	
}
