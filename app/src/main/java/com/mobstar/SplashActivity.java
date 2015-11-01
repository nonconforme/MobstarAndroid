package com.mobstar;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.provider.Settings.Secure;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.mobstar.api.ConnectCallback;
import com.mobstar.api.RestClient;
import com.mobstar.api.responce.UserAccountResponse;
import com.mobstar.geo_filtering.SelectCurrentRegionActivity;
import com.mobstar.home.HomeActivity;
import com.mobstar.home.HomeInformationActivity;
import com.mobstar.login.LoginSocialActivity;
import com.mobstar.utils.Constant;
import com.mobstar.utils.JSONParser;
import com.mobstar.utils.NetworkChangeReceiver;
import com.mobstar.utils.OnNetworkChangeListener;
import com.mobstar.utils.Utility;

import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class SplashActivity extends Activity implements OnNetworkChangeListener {

    private static final String IS_FIRST_OPEN_PREFERENCE = "is first open";
    private Timer timer;
	private Context mContext;

	private GoogleCloudMessaging gcm;
	private String regid;
	private String deepLinkedId;
	private String show_system_notification="",defaultNotificationTitle="",defaultNotificationImage="",description="";
	private NetworkChangeReceiver mNetworkChangeReceiver;
	private Toast mToast;
	private boolean networkConnect = true;
	private HomeInfoCall homeInfoCall;

	private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
	private SharedPreferences preferences;
	private String sErrorMessage;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		String android_id = Secure.getString(this.getContentResolver(),
				Secure.ANDROID_ID);
		String device_id=android_id;
		Log.d("mobstar","device id"+device_id);

		mContext = SplashActivity.this;

		if (Build.VERSION.SDK_INT > Build.VERSION_CODES.GINGERBREAD) {
			StrictMode.ThreadPolicy tp = new StrictMode.ThreadPolicy.Builder().permitAll().build();
			StrictMode.setThreadPolicy(tp);
		}

		preferences = getSharedPreferences(Constant.MOBSTAR_PREF, Activity.MODE_PRIVATE);

        if (preferences.getBoolean(IS_FIRST_OPEN_PREFERENCE, true)) {
            preferences.edit().putBoolean(IS_FIRST_OPEN_PREFERENCE, false).apply();
            AdWordsManager.getInstance().sendFirstOpenEvent();
        }

		parseIntent();

		if (preferences.getBoolean("isLogin", false)) {

			if(deepLinkedId!=null) {
				getUserAccountRequest();
			}
			else {
				//clear badge
				Utility.clearBadge(mContext);
				new BadgeRead().run();
				sendAnalytics();
				if (Utility.isNetworkAvailable(mContext)) {
					networkConnect = true;
					if (homeInfoCall == null)
						homeInfoCall = new HomeInfoCall();
					homeInfoCall.start();

				} else {
					networkConnect = false;
					showToastNotification(getString(R.string.no_internet_access));
//					Utility.HideDialog(mContext);
				}

			}

		} else {
			sendAnalytics();
			timer = new Timer();
			TimerTask task = new TimerTask() {

				@Override
				public void run() {
					Intent i = new Intent(mContext,LoginSocialActivity.class);
					startActivity(i);
					finish();
				}
			};
			timer.schedule(task, 3000);
		}

		Utility.SendDataToGA("Splash Screen", SplashActivity.this);

		registerGoogleCloudMessaging();

	}

	private void parseIntent(){
		Intent intent = getIntent();
		if(intent != null) {
			String action = intent.getAction();
			Uri data = intent.getData();
			if(data != null) {
				Log.d("mobstar","uri==>" + data.toString());
				deepLinkedId = data.getQueryParameter("id");
				if(deepLinkedId != null){
					Log.d("mobstar","id==>"+deepLinkedId);
					//				    		SharedPreferences pref = getSharedPreferences("mobstar_pref", MODE_PRIVATE);
					//							pref.edit().putString("deepLinkedId",deepLinkedId).commit();
				}
			}

		}
	}

	private void registerGoogleCloudMessaging(){
		// GCM Push Notification
		if (checkPlayServices()) {
			gcm = GoogleCloudMessaging.getInstance(this);
			regid = Utility.getRegistrationId(mContext);
			//Log.v(Constant.TAG, "GCM Registration ID "+regid);
			if (regid.isEmpty()) {
				registerInBackground();
			}
		}
	}

	private void getUserAccountRequest(){
		Utility.ShowProgressDialog(SplashActivity.this, getString(R.string.loading));
		RestClient.getInstance(this).getRequest(Constant.USER_ACCOUNT, null, new ConnectCallback<UserAccountResponse>() {
			@Override
			public void onSuccess(UserAccountResponse object) {
				Utility.HideDialog(SplashActivity.this);
				if (object.getUser().getUserContinentId() == 0){
					startSelectCurrentRegionActivity();
				}
				else startHomeActivity();
			}

			@Override
			public void onFailure(String error) {
				Utility.HideDialog(SplashActivity.this);
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
		final Intent intent = new Intent(mContext, HomeActivity.class);
		intent.putExtra("deepLinkedId", deepLinkedId);
		startActivity(intent);
		finish();
	}

	@Override
	protected void onResume() {
		super.onResume();
		registerNetworkConnectReceiver();
	}

	private void registerNetworkConnectReceiver(){
		final IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(NetworkChangeReceiver.CONNECTIVITY_CHANGE);
		intentFilter.addAction(NetworkChangeReceiver.WIFI_STATE_CHANGED);
		mNetworkChangeReceiver = new NetworkChangeReceiver();
		mNetworkChangeReceiver.setNetworkChangeListener(this);
		registerReceiver(mNetworkChangeReceiver, intentFilter);
	}

	@Override
	public void onInternetConnect() {
		if (networkConnect)
			return;
		networkConnect = true;
		if (homeInfoCall == null)
			homeInfoCall = new HomeInfoCall();
		homeInfoCall.start();
	}

	@Override
	public void onInternetDisconnect() {
		if (!networkConnect)
			return;
		networkConnect = false;
		showToastNotification(getString(R.string.no_internet_access));
	}

	private void unregisteredNetworkConnectReceiver(){
		unregisterReceiver(mNetworkChangeReceiver);
	}

	@Override
	protected void onPause() {
		super.onPause();
		unregisteredNetworkConnectReceiver();
	}

//	@Override
//	protected void onDestroy() {
//		super.onDestroy();
//		unregisteredNetworkConnectReceiver();
//	}

	private void registerInBackground() {
		new GCMRegistrationCall().start();
	}

	class GCMRegistrationCall extends Thread {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			try {
				if (gcm == null) {
					gcm = GoogleCloudMessaging.getInstance(mContext);
				}
				regid = gcm.register(Constant.SENDER_ID);
				Log.v(Constant.TAG, "GCM Registration ID "+regid);
				// Persist the regID - no need to register again.
				Utility.storeRegistrationId(SplashActivity.this, regid);

			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}

	@Override
	public void onBackPressed() {
		if (!networkConnect)
			super.onBackPressed();
	}

	private boolean checkPlayServices() {
		int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
		if (resultCode != ConnectionResult.SUCCESS) {
			if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
				GooglePlayServicesUtil.getErrorDialog(resultCode, this, PLAY_SERVICES_RESOLUTION_REQUEST).show();
			} else {
				Log.i(Constant.TAG, "This device is not supported.");
				finish();
			}
			return false;
		}
		return true;
	}
	class HomeInfoCall extends Thread {

		@Override
		public void run() {

			String Query= Constant.SERVER_URL + Constant.HOME_INFO;
			String response = JSONParser.getRequest(Query,preferences.getString("token", null));

			Log.v(Constant.TAG, "home info response " + response);

			if (response != null) {

				try {
					Thread.sleep(5000);
					if(response!=null){
						JSONObject jsonObject = new JSONObject(response);

						if (jsonObject.has("error")) {
							sErrorMessage = jsonObject.getString("error");
						}

						if (jsonObject.has("show_system_notification")) {
							show_system_notification = jsonObject.getString("show_system_notification");
							defaultNotificationTitle=jsonObject.getString("defaultNotificationTitle");
							defaultNotificationImage=jsonObject.getString("defaultNotificationImage");
							description=jsonObject.getString("description");

						}

						if (sErrorMessage != null && !sErrorMessage.equals("")) {
							handlerInfo.sendEmptyMessage(0);
						} else {
							handlerInfo.sendEmptyMessage(1);
						}
					}


				} catch (Exception e) {
					e.printStackTrace();
					handlerInfo.sendEmptyMessage(0);
				}

			} else {

				handlerInfo.sendEmptyMessage(0);
			}

		}
	}



	Handler handlerInfo = new Handler() {

		@Override
		public void handleMessage(Message msg) {
//			Utility.HideDialog(mContext);

			if (msg.what == 1) {
				if(show_system_notification!=null && show_system_notification.equalsIgnoreCase("TRUE")){
				startHomeInformationActivity();
				}
			} else {
				OkayAlertDialog(sErrorMessage);
			}
		}
	};

	private void startHomeInformationActivity(){
		Intent intent = new Intent(mContext, HomeInformationActivity.class);
		intent.putExtra("title", defaultNotificationTitle);
		intent.putExtra("img", defaultNotificationImage);
		intent.putExtra("des", description);
		startActivity(intent);
		finish();

	}

	class BadgeRead extends Thread {

		@Override
		public void run() {

			String Query= Constant.SERVER_URL + Constant.BADGE_READ;
			String response = JSONParser.postRequest(Query,null,null,preferences.getString("token", null));

			Log.v(Constant.TAG, "home info response " + response);

			if (response != null) {

				try {
					if(response!=null){

							handlerBadge.sendEmptyMessage(1);
					}


				} catch (Exception e) {
					e.printStackTrace();
					handlerBadge.sendEmptyMessage(0);
				}

			} else {

				handlerBadge.sendEmptyMessage(0);
			}

		}
	}



	private Handler handlerBadge= new Handler() {

		@Override
		public void handleMessage(Message msg) {

			if (msg.what == 1) {

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

		public AddAnalytics(String os,int appV,String deviceName){
			this.osVersion=os;
			this.appVersion=String.valueOf(appV);
			this.deviceName=deviceName;
		}

		@Override
		public void run() {

			String[] name = {"platform","osversion","appversion","devicename"};
			String[] value = {"Android",osVersion,appVersion,deviceName};
			String response = JSONParser.postRequest(Constant.SERVER_URL + Constant.ANALYTICS_READ, name, value,preferences.getString("token", null));

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

	void OkayAlertDialog(final String msg) {

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

	private void showToastNotification(String _message){
		if (mToast == null){
			mToast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
		}
		mToast.setText(_message);
		mToast.show();
	}

}
