package com.mobstar.settings;

import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
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
import android.widget.TextView;
import android.widget.Toast;

import com.mobstar.R;
import com.mobstar.info.report.InformationDetailActivity;
import com.mobstar.settings.PersonDetailsActivity.UpdateCall;
import com.mobstar.utils.Constant;
import com.mobstar.utils.JSONParser;
import com.mobstar.utils.Utility;

public class ChangePasswordActivity extends Activity implements OnClickListener {

	Context mContext;

	EditText editOldPassword, editNewPassword, editConfirmPassword;
	TextView textOldPasswordHint, textNewPasswordHint, textConfirmPasswordHint;

	Button btnCancel, btnSave;

	Typeface typefaceBtn;

	SharedPreferences preferences;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_change_password);

		mContext = ChangePasswordActivity.this;
		preferences = getSharedPreferences("mobstar_pref", MODE_PRIVATE);

		InitControls();
		
		Utility.SendDataToGA("ChangePassword Screen", ChangePasswordActivity.this);

	}

	void InitControls() {

		typefaceBtn = Typeface.createFromAsset(getAssets(), "GOTHAM-BOLD.TTF");

		btnCancel = (Button) findViewById(R.id.btnCancel);
		btnCancel.setTypeface(typefaceBtn);
		btnCancel.setOnClickListener(this);

		btnSave = (Button) findViewById(R.id.btnSave);
		btnSave.setTypeface(typefaceBtn);
		btnSave.setOnClickListener(this);

		editOldPassword = (EditText) findViewById(R.id.editOldPassword);
		editOldPassword.setTypeface(typefaceBtn);
		editOldPassword.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				editOldPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
				textOldPasswordHint.setVisibility(View.INVISIBLE);
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

		editNewPassword = (EditText) findViewById(R.id.editNewPassword);
		editNewPassword.setTypeface(typefaceBtn);
		editNewPassword.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				editNewPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
				textNewPasswordHint.setVisibility(View.INVISIBLE);
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

		editConfirmPassword = (EditText) findViewById(R.id.editConfirmPassword);
		editConfirmPassword.setTypeface(typefaceBtn);
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

		textOldPasswordHint = (TextView) findViewById(R.id.textOldPasswordHint);
		textOldPasswordHint.setVisibility(View.INVISIBLE);

		textNewPasswordHint = (TextView) findViewById(R.id.textNewPasswordHint);
		textNewPasswordHint.setVisibility(View.INVISIBLE);

		textConfirmPasswordHint = (TextView) findViewById(R.id.textConfirmPasswordHint);
		textConfirmPasswordHint.setVisibility(View.INVISIBLE);
	}

	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		if (btnCancel.equals(view)) {
			onBackPressed();
		} else if (btnSave.equals(view)) {

			boolean isValid = true;

			if (editOldPassword.getText().toString().trim().length() == 0) {
				editOldPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.signup_cross, 0);
				textOldPasswordHint.setText("Enter Old Password");
				textOldPasswordHint.setVisibility(View.VISIBLE);
				isValid = false;
			} else {
				editOldPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.signup_tick, 0);
				textOldPasswordHint.setVisibility(View.INVISIBLE);
			}

			if (editNewPassword.getText().toString().trim().length() == 0) {
				editNewPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.signup_cross, 0);
				textNewPasswordHint.setText("Enter New Password");
				textNewPasswordHint.setVisibility(View.VISIBLE);
				isValid = false;
			} else {
				editNewPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.signup_tick, 0);
				textNewPasswordHint.setVisibility(View.INVISIBLE);
			}

			if (editConfirmPassword.getText().toString().trim().length() == 0) {
				editConfirmPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.signup_cross, 0);
				textConfirmPasswordHint.setText("Enter Confirm Password");
				textConfirmPasswordHint.setVisibility(View.VISIBLE);
				isValid = false;
			} else if (!editConfirmPassword.getText().toString().trim().equals(editNewPassword.getText().toString().trim())) {
				editConfirmPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.signup_cross, 0);
				textConfirmPasswordHint.setText("Password not match");
				textConfirmPasswordHint.setVisibility(View.VISIBLE);
				isValid = false;
			} else {
				editConfirmPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.signup_tick, 0);
				textConfirmPasswordHint.setVisibility(View.INVISIBLE);
			}

			if (isValid) {
				Utility.ShowProgressDialog(mContext, "Loading");
				if (Utility.isNetworkAvailable(mContext)) {
					new UpdateCall(editNewPassword.getText().toString().trim()).start();
				} else {

					Toast.makeText(mContext, "No, Internet Access!", Toast.LENGTH_SHORT).show();
					Utility.HideDialog(mContext);
				}
			}
		}
	}

	class UpdateCall extends Thread {

		String NewPassword;

		public UpdateCall(String NewPassword) {
			this.NewPassword = NewPassword;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub

			String[] name = { "password", "userId" };
			String[] value = { NewPassword, preferences.getString("userid", "") };

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
