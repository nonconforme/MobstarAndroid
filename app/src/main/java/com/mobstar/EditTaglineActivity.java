package com.mobstar;

import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.lang3.StringEscapeUtils;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.mobstar.utils.Constant;
import com.mobstar.utils.JSONParser;
import com.mobstar.utils.Utility;

public class EditTaglineActivity extends Activity implements OnClickListener {

	EditText editTagline,editBio;
	Context mContext;

	Button btnCancel, btnSave;

	Typeface typefaceBtn;
	SharedPreferences preferences;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_tagline);
		mContext = EditTaglineActivity.this;
		preferences = getSharedPreferences("mobstar_pref", Activity.MODE_PRIVATE);

		InitControls();
		
		Utility.SendDataToGA("EditTagline Screen", EditTaglineActivity.this);
	}

	void InitControls() {

		typefaceBtn = Typeface.createFromAsset(getAssets(), "GOTHAM-BOLD.TTF");

		btnCancel = (Button) findViewById(R.id.btnCancel);
		btnCancel.setTypeface(typefaceBtn);
		btnCancel.setOnClickListener(this);

		btnSave = (Button) findViewById(R.id.btnSave);
		btnSave.setTypeface(typefaceBtn);
		btnSave.setOnClickListener(this);

		editTagline = (EditText) findViewById(R.id.editTagline);
		editTagline.setText(Utility.unescape_perl_string(preferences.getString("tagline", "")));
		
		editBio= (EditText) findViewById(R.id.editBio);
		editBio.setText(Utility.unescape_perl_string(preferences.getString("bio", "")));
	}

	@Override
	public void onClick(View view) {
		if (btnCancel.equals(view)) {
			onBackPressed();
		} else if (btnSave.equals(view)) {

			boolean isValid = true;

			if (editTagline.getText().toString().trim().length() == 0) {
				editTagline.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.signup_cross, 0);
				isValid = false;
			} else {
				editTagline.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.signup_tick, 0);
			}

			if (isValid) {
				Utility.ShowProgressDialog(mContext, "Loading");
				if (Utility.isNetworkAvailable(mContext)) {
					String myBio=editBio.getText().toString().trim();
					String myTagline=editTagline.getText().toString().trim();
//					Log.d("mobstar","escape String"+StringEscapeUtils.escapeJava(myBio));
					new UpdateCall(StringEscapeUtils.escapeJava(myTagline),StringEscapeUtils.escapeJava(myBio)).start();
					
					final Dialog dialog = new Dialog(mContext, R.style.DialogAnimationTheme);
					dialog.setContentView(R.layout.dialog_tagline_saved);
					dialog.show();

					Timer timer = new Timer();
					TimerTask task = new TimerTask() {

						@Override
						public void run() {
							
							runOnUiThread(new  Runnable() {
								public void run() {
									dialog.dismiss();
									
								}
							});
							
						}
					};
					timer.schedule(task, 1000);
				} else {

					Toast.makeText(mContext, "No, Internet Access!", Toast.LENGTH_SHORT).show();
					Utility.HideDialog(mContext);
				}
			}
		}
	}

	class UpdateCall extends Thread {

		String tagline;
		String bio;

		public UpdateCall(String tagline,String bio) {
			this.tagline = tagline;
			this.bio=bio;
		}

		@Override
		public void run() {

			String[] name = { "tagline","bio" };
			String[] value = { tagline,bio };

			String response = JSONParser.putRequest(Constant.SERVER_URL + Constant.SIGNUP + preferences.getString("userid", ""), name, value, preferences.getString("token", null));

//			Log.v(Constant.TAG, "UpdateCall response " + response);

			if (response != null) {

				try {

					JSONObject jsonObject = new JSONObject(response);

					if (jsonObject.has("error")) {
						handlerUpdate.sendEmptyMessage(0);
					} else {
						handlerUpdate.sendEmptyMessage(1);
					}

				} catch (Exception e) {

					e.printStackTrace();
					handlerUpdate.sendEmptyMessage(0);
				}

			} else {

				handlerUpdate.sendEmptyMessage(0);
			}

		}
	}

	Handler handlerUpdate = new Handler() {

		@Override
		public void handleMessage(Message msg) {

			Utility.HideDialog(mContext);

			if (msg.what == 1) {

				Intent intent = new Intent("profile_image_changed");
				LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
				
			

				SharedPreferences pref = getSharedPreferences("mobstar_pref", MODE_PRIVATE);
				pref.edit().putString("tagline", editTagline.getText().toString().trim()).commit();
				pref.edit().putString("bio", editBio.getText().toString().trim()).commit();
				
				Handler handler = new Handler(); 
			    handler.postDelayed(new Runnable() { 
			         public void run() { 
			             finish();
			         } 
			    }, 1000); 
				

			} else {

			}
		}
	};
}
