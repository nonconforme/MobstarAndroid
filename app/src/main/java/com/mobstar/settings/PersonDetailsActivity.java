package com.mobstar.settings;

import java.util.Timer;
import java.util.TimerTask;

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
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.mobstar.R;
import com.mobstar.info.report.InformationDetailActivity;
import com.mobstar.utils.Constant;
import com.mobstar.utils.JSONParser;
import com.mobstar.utils.Utility;

public class PersonDetailsActivity extends Activity implements OnClickListener {

	Context mContext;

	Button btnCancel, btnSave;

	Typeface typefaceBtn;
	EditText editFullName, editEmail, editDisplayName;
	TextView textFullNameHint, textEmailHint, textDisplayNameHint;

	SharedPreferences preferences;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_person_details);

		mContext = PersonDetailsActivity.this;

		preferences = getSharedPreferences("mobstar_pref", MODE_PRIVATE);
		InitControls();
		
		Utility.SendDataToGA("PersonDetails Screen", PersonDetailsActivity.this);

	}

	void InitControls() {

		typefaceBtn = Typeface.createFromAsset(getAssets(), "GOTHAM-BOLD.TTF");

		btnCancel = (Button) findViewById(R.id.btnCancel);
		btnCancel.setTypeface(typefaceBtn);
		btnCancel.setOnClickListener(this);

		btnSave = (Button) findViewById(R.id.btnSave);
		btnSave.setTypeface(typefaceBtn);
		btnSave.setOnClickListener(this);

		editFullName = (EditText) findViewById(R.id.editFullName);
		editFullName.setTypeface(typefaceBtn);
		editFullName.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				editFullName.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
				textFullNameHint.setVisibility(View.INVISIBLE);
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

		editDisplayName = (EditText) findViewById(R.id.editDisplayName);
		editDisplayName.setTypeface(typefaceBtn);
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

		textFullNameHint = (TextView) findViewById(R.id.textFullNameHint);
		textFullNameHint.setVisibility(View.INVISIBLE);

		textEmailHint = (TextView) findViewById(R.id.textEmailHint);
		textEmailHint.setVisibility(View.INVISIBLE);

		textDisplayNameHint = (TextView) findViewById(R.id.textDisplayNameHint);
		textDisplayNameHint.setVisibility(View.INVISIBLE);

		editFullName.setText(preferences.getString("fullName", ""));
		editEmail.setText(preferences.getString("email_address", ""));
		editDisplayName.setText(preferences.getString("displayName", ""));
	}

	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		if (btnCancel.equals(view)) {
			onBackPressed();
		} else if (btnSave.equals(view)) {

			boolean isValid = true;

			if (editFullName.getText().toString().trim().length() == 0) {

				editFullName.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.signup_cross, 0);
				textFullNameHint.setText("Enter FullName");
				textFullNameHint.setVisibility(View.VISIBLE);

				isValid = false;
			} else {
				editFullName.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.signup_tick, 0);
				textFullNameHint.setVisibility(View.INVISIBLE);
			}

			if (editEmail.getText().toString().trim().length() == 0) {
				editEmail.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.signup_cross, 0);
				textEmailHint.setText("Enter Email Address");
				textEmailHint.setVisibility(View.VISIBLE);
				isValid = false;
			} else if (!Utility.IsValidEmail(editEmail)) {
				editEmail.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.signup_cross, 0);
				textEmailHint.setText("Enter Valid Email Address");
				textEmailHint.setVisibility(View.VISIBLE);
				isValid = false;
			} else {
				editEmail.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.signup_tick, 0);
				textEmailHint.setVisibility(View.INVISIBLE);
			}

			if (editDisplayName.getText().toString().trim().length() == 0) {
				editDisplayName.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.signup_cross, 0);
				textDisplayNameHint.setText("Enter Display Name");
				textDisplayNameHint.setVisibility(View.VISIBLE);
				isValid = false;
			} else {
				editDisplayName.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.signup_tick, 0);
				textDisplayNameHint.setVisibility(View.INVISIBLE);
			}

			if (isValid) {
				Utility.ShowProgressDialog(mContext, "Loading");
				if (Utility.isNetworkAvailable(mContext)) {
					new UpdateCall(editFullName.getText().toString().trim(), editEmail.getText().toString().trim(), editDisplayName.getText().toString().trim()).start();
				} else {

					Toast.makeText(mContext, "No, Internet Access!", Toast.LENGTH_SHORT).show();
					Utility.HideDialog(mContext);
				}
			}
		}
	}

	class UpdateCall extends Thread {

		String displayName, fullName, email;

		public UpdateCall(String fullName, String email, String displayName) {
			this.fullName = fullName;
			this.displayName = displayName;
			this.email = email;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub

			String[] name = { "email", "fullName", "displayName", "userId" };
			String[] value = { email, fullName, displayName, preferences.getString("userid", "") };

			String response = JSONParser.putRequest(Constant.SERVER_URL + Constant.SIGNUP + preferences.getString("userid", ""), name, value, preferences.getString("token", null));

			// Log.v(Constant.TAG, "UpdateCall response " + response);

			if (response != null) {

				try {

					JSONObject jsonObject = new JSONObject(response);

					if (jsonObject.has("error")) {
						handlerUpdate.sendEmptyMessage(0);
					} else {
						handlerUpdate.sendEmptyMessage(1);
					}

				} catch (Exception e) {
					// TODO: handle exception
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
			// TODO Auto-generated method stub
			Utility.HideDialog(mContext);

			if (msg.what == 1) {

				Intent intent = new Intent("profile_image_changed");
				LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);

				SharedPreferences pref = getSharedPreferences("mobstar_pref", MODE_PRIVATE);
				pref.edit().putString("displayName", editDisplayName.getText().toString().trim()).commit();
				pref.edit().putString("fullName", editFullName.getText().toString().trim()).commit();

				final Dialog dialog = new Dialog(mContext, R.style.DialogAnimationTheme);
				dialog.setContentView(R.layout.dialog_update_profile);
				dialog.show();

				Timer timer = new Timer();
				TimerTask task = new TimerTask() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						dialog.dismiss();
					}
				};
				timer.schedule(task, 1000);

			} else {

			}
		}
	};
}
