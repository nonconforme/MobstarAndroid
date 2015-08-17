package com.mobstar.help;

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
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.mobstar.R;
import com.mobstar.utils.Constant;
import com.mobstar.utils.JSONParser;
import com.mobstar.utils.Utility;

public class LeaveFeedbackActivity extends Activity implements OnClickListener {

	EditText editFeedback;

	Button btnCancel, btnLeaveFeedback;

	Context mContext;

	Typeface typefaceBtn;
	SharedPreferences preferences;
	public String sErrorMessage;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_leave_feedback);
		preferences = getSharedPreferences("mobstar_pref", Activity.MODE_PRIVATE);
		mContext = LeaveFeedbackActivity.this;

		InitControls();
		
		Utility.SendDataToGA("LeaveFeedback Screen", LeaveFeedbackActivity.this);

	}

	void InitControls() {

		typefaceBtn = Typeface.createFromAsset(getAssets(), "GOTHAM-BOLD.TTF");

		btnCancel = (Button) findViewById(R.id.btnCancel);
		btnCancel.setTypeface(typefaceBtn);
		btnCancel.setOnClickListener(this);

		btnLeaveFeedback = (Button) findViewById(R.id.btnLeaveFeedback);
		btnLeaveFeedback.setTypeface(typefaceBtn);
		btnLeaveFeedback.setOnClickListener(this);

		btnLeaveFeedback = (Button) findViewById(R.id.btnLeaveFeedback);
		btnLeaveFeedback.setTypeface(typefaceBtn);
		btnLeaveFeedback.setOnClickListener(this);

		editFeedback = (EditText) findViewById(R.id.editFeedback);
		editFeedback.setTypeface(typefaceBtn);
	}

	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub

		if (btnCancel.equals(view)) {
			onBackPressed();
		} else if (btnLeaveFeedback.equals(view)) {
			Utility.ShowProgressDialog(mContext, getString(R.string.loading) + "...");

			if (Utility.isNetworkAvailable(mContext)) {

				new FeedbackCall(editFeedback.getText().toString().trim()).start();

			} else {

				Toast.makeText(mContext, getString(R.string.no_internet_access), Toast.LENGTH_SHORT).show();
				Utility.HideDialog(mContext);
			}
		}
	}

	class FeedbackCall extends Thread {

		String FeedbackDetails;

		public FeedbackCall(String FeedbackDetails) {
			this.FeedbackDetails = FeedbackDetails;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub

			String[] name = { "feedbackDetails" };
			String[] value = { FeedbackDetails };

			String response = JSONParser.postRequest(Constant.SERVER_URL + Constant.LEAVE_FEEDBACK, name, value, preferences.getString("token", null));

//			Log.v(Constant.TAG, "FeedbackCall response " + response);

			if (response != null) {

				try {

					JSONObject jsonObject = new JSONObject(response);

					if (jsonObject.has("error")) {
						sErrorMessage = jsonObject.getString("error");
					}

					if (sErrorMessage != null && !sErrorMessage.equals("")) {
						handlerFeedback.sendEmptyMessage(0);
					} else {
						handlerFeedback.sendEmptyMessage(1);
					}

				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
					handlerFeedback.sendEmptyMessage(0);
				}

			} else {

				handlerFeedback.sendEmptyMessage(0);
			}

		}
	}

	Handler handlerFeedback = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			Utility.HideDialog(mContext);

			if (msg.what == 1) {
				editFeedback.setText("");
				final Dialog dialog = new Dialog(mContext, R.style.DialogAnimationTheme);
				dialog.setContentView(R.layout.dialog_leave_feedback);
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
