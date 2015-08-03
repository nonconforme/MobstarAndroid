package com.mobstar.settings;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.TextView;
import android.widget.Toast;

import com.mobstar.R;
import com.mobstar.utils.Constant;
import com.mobstar.utils.JSONParser;
import com.mobstar.utils.Utility;

public class PrivacySettingsActivity extends Activity {

	SharedPreferences preferences;
	Context mContext;
	String PrivacyText="";
	
	TextView textPrivacyPolicy;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_privacy_policy);
		
		preferences = getSharedPreferences("mobstar_pref", MODE_PRIVATE);
		mContext=PrivacySettingsActivity.this;
		
		InitControls();
		
		Utility.SendDataToGA("PrivacySettings Screen", PrivacySettingsActivity.this);
	}
	
	void InitControls(){
		textPrivacyPolicy=(TextView)findViewById(R.id.textPrivacyPolicy);
		textPrivacyPolicy.setText("");
		
		Utility.ShowProgressDialog(mContext, "Loading");

		if (Utility.isNetworkAvailable(mContext)) {

			new PrivacyCall().start();

		} else {

			Toast.makeText(mContext, "No, Internet Access!", Toast.LENGTH_SHORT).show();
			Utility.HideDialog(mContext);
		}
	}
	
	class PrivacyCall extends Thread {

		@Override
		public void run() {
			// TODO Auto-generated method stub

			String response = JSONParser.getRequest(Constant.SERVER_URL + Constant.GET_PRIVACY, preferences.getString("token", null));

			// Log.v(Constant.TAG, "GET_NOTIFICATION_COUNT response " +
			// response);

			if (response != null) {

				try {

					JSONObject jsonObject = new JSONObject(response);

					PrivacyText = jsonObject.getString("privacyPolicy");

					handlerPrivacy.sendEmptyMessage(1);

				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
					handlerPrivacy.sendEmptyMessage(0);
				}

			} else {

				handlerPrivacy.sendEmptyMessage(0);
			}

		}
	}

	Handler handlerPrivacy = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			textPrivacyPolicy.setText(PrivacyText);
			
			Utility.HideDialog(mContext);
		}
	};
}
