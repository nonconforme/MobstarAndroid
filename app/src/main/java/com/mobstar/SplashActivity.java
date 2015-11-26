package com.mobstar;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.Settings.Secure;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.mobstar.api.ConnectCallback;
import com.mobstar.api.new_api_call.AuthCall;
import com.mobstar.api.new_api_call.NotificationCall;
import com.mobstar.api.new_api_model.DefaultNotification;
import com.mobstar.api.new_api_model.response.DefaultNotificationResponse;
import com.mobstar.api.new_api_model.response.SuccessResponse;
import com.mobstar.api.responce.Error;
import com.mobstar.geo_filtering.SelectCurrentRegionActivity;
import com.mobstar.home.HomeActivity;
import com.mobstar.home.HomeInformationActivity;
import com.mobstar.login.LoginSocialActivity;
import com.mobstar.utils.Constant;
import com.mobstar.utils.NetworkChangeReceiver;
import com.mobstar.utils.OnNetworkChangeListener;
import com.mobstar.utils.UserPreference;
import com.mobstar.utils.Utility;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class SplashActivity extends Activity implements OnNetworkChangeListener {

	private GoogleCloudMessaging gcm;
	private String regid;
	private String deepLinkedId;
	private NetworkChangeReceiver mNetworkChangeReceiver;
	private Toast mToast;
	private boolean networkConnect = true;

	private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);

		if (Build.VERSION.SDK_INT > Build.VERSION_CODES.GINGERBREAD) {
			StrictMode.ThreadPolicy tp = new StrictMode.ThreadPolicy.Builder().permitAll().build();
			StrictMode.setThreadPolicy(tp);
		}

        if (UserPreference.isFirstOpenApp(this))
            AdWordsManager.getInstance().sendFirstOpenEvent();
		parseIntent();
		initLastState();
		Utility.SendDataToGA("Splash Screen", SplashActivity.this);
		registerGoogleCloudMessaging();

	}

	private void initLastState(){
		if (UserPreference.isLogin(this)) {

			if( deepLinkedId != null) {
				verifyUserContinent();
			}
			else {
				//clear badge
				Utility.clearBadge(this);
//				new BadgeRead().run();
				sendAnalytic();
				getSystemDefaultNotification();

			}

		} else {
			startLoginSocialActivityPostDelay();
		}

	}

	private void startLoginSocialActivityPostDelay(){
		final Timer timer = new Timer();
		final TimerTask task = new TimerTask() {

			@Override
			public void run() {
				startLoginSocialActivity();
			}
		};
		timer.schedule(task, 3000);
	}

	private void parseIntent(){
		final Intent intent = getIntent();
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
			regid = Utility.getRegistrationId(this);
			//Log.v(Constant.TAG, "GCM Registration ID "+regid);
			if (regid.isEmpty()) {
				registerInBackground();
			}
		}
	}

	private void startHomeActivity(){
		final Intent intent = new Intent(this, HomeActivity.class);
		intent.putExtra("deepLinkedId", deepLinkedId);
		startActivity(intent);
		finish();
	}

	private void verifyUserContinent(){
		if (UserPreference.existUserContinent(this))
			startHomeActivity();
		else startSelectCurrentRegionActivity();
	}

	private void startSelectCurrentRegionActivity(){
		final Intent intent = new Intent(this, SelectCurrentRegionActivity.class);
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
		initLastState();
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

	private void registerInBackground() {
		new GCMRegistrationCall().start();
	}

	class GCMRegistrationCall extends Thread {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			try {
				if (gcm == null) {
					gcm = GoogleCloudMessaging.getInstance(SplashActivity.this);
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

	private void getSystemDefaultNotification(){
		NotificationCall.getDefaultNotification(this, new ConnectCallback<DefaultNotificationResponse>() {
			@Override
			public void onSuccess(DefaultNotificationResponse object) {
				if (object.getDefaultNotification().isShowSystem())
					startHomeInformationActivity(object.getDefaultNotification());
				else {
					if (UserPreference.isLogin(SplashActivity.this))
						verifyUserContinent();
					else startLoginSocialActivity();
				}
			}

			@Override
			public void onFailure(String error) {
				OkayAlertDialog(error);
			}

			@Override
			public void onServerError(Error error) {
				OkayAlertDialog(error.getMessage());
			}
		});
	}

	private void startHomeInformationActivity(final DefaultNotification defaultNotification){
		final Intent intent = new Intent(this, HomeInformationActivity.class);
		intent.putExtra(HomeInformationActivity.DEFAULT_NOTIFICATION, defaultNotification);
		startActivity(intent);
		finish();

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

	void OkayAlertDialog(final String msg) {

		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(SplashActivity.this);

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

	private void startLoginSocialActivity(){
		final Intent i = new Intent(this, LoginSocialActivity.class);
		startActivity(i);
		finish();
	}

}
