package com.mobstar.service;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import com.mobstar.utils.Constant;
import com.mobstar.utils.JSONParser;
import com.mobstar.utils.Utility;

import org.json.JSONObject;

public class NotificationService extends Service{
	int NotificationCount = 0;
	SharedPreferences preferences;
	private static final String TAG = "NotificationService";
	public static final String BROADCAST_ACTION = "notification_count_update_from_service";
	private final Handler handler = new Handler();
	Intent intent;

	@Override
	public void onCreate() {
		super.onCreate();
		preferences = getSharedPreferences("mobstar_pref", MODE_PRIVATE);
		intent = new Intent(BROADCAST_ACTION);	
		handler.removeCallbacks(sendUpdatesToUI);
		handler.postDelayed(sendUpdatesToUI, 30000); // 30second
	}


	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	private Runnable sendUpdatesToUI = new Runnable() {
		public void run() {
			Log.d("mobstar","****1 Call sendUpdatesToUI****");
			if (Utility.isNetworkAvailable(getApplicationContext())) {
				Log.d("mobstar","****2 Call from notification service****");
				new NotificationCountCall().start();
			}		
			handler.postDelayed(this, 30000); // 30 seconds
		}
	};    


	@Override
	public void onDestroy() {		
		handler.removeCallbacks(sendUpdatesToUI);		
		super.onDestroy();
	}

	class NotificationCountCall extends Thread {

		@Override
		public void run() {
			// TODO Auto-generated method stub

			String response = JSONParser.getRequest(Constant.SERVER_URL + Constant.GET_NOTIFICATION_COUNT, preferences.getString("token", null));

			//			Log.v(Constant.TAG, "GET_NOTIFICATION_COUNT response " + response);

			if (response != null) {

				try {

					JSONObject jsonObject = new JSONObject(response);

					NotificationCount = jsonObject.getInt("notifications");

					handlerNotification.sendEmptyMessage(1);

				} catch (Exception e) {
					e.printStackTrace();
					handlerNotification.sendEmptyMessage(0);
				}
			} else {

				handlerNotification.sendEmptyMessage(0);
			}
		}
	}

	Handler handlerNotification = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			if (NotificationCount > 0) {
				Log.d(TAG, "entered DisplayLoggingInfo"+NotificationCount);
                Utility.setBadge(getApplicationContext(), NotificationCount);
				intent.putExtra("notification_count",NotificationCount);
				sendBroadcast(intent);
			} 
		}
	};

}
