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

public class ReportAbuseActivity extends Activity implements OnClickListener {

	EditText editReport;

	Button btnCancel, btnSendReport;

	Context mContext;

	Typeface typefaceBtn;
	SharedPreferences preferences;

	public String sErrorMessage;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_report_abuse);

		mContext = ReportAbuseActivity.this;
		preferences = getSharedPreferences("mobstar_pref", Activity.MODE_PRIVATE);

		InitControls();
		
		Utility.SendDataToGA("ReportAbuse Screen", ReportAbuseActivity.this);
	}

	void InitControls() {

		typefaceBtn = Typeface.createFromAsset(getAssets(), "GOTHAM-BOLD.TTF");

		btnCancel = (Button) findViewById(R.id.btnCancel);
		btnCancel.setTypeface(typefaceBtn);
		btnCancel.setOnClickListener(this);

		btnSendReport = (Button) findViewById(R.id.btnSendReport);
		btnSendReport.setTypeface(typefaceBtn);
		btnSendReport.setOnClickListener(this);

		editReport = (EditText) findViewById(R.id.editReport);
		editReport.setTypeface(typefaceBtn);
	}

	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub

		if (btnCancel.equals(view)) {
			onBackPressed();
		} else if (btnSendReport.equals(view)) {
			Utility.ShowProgressDialog(mContext, getString(R.string.loading) + "...");

			if (Utility.isNetworkAvailable(mContext)) {

				new SendReportCall(editReport.getText().toString().trim()).start();

			} else {

				Toast.makeText(mContext, getString(R.string.no_internet_access), Toast.LENGTH_SHORT).show();
				Utility.HideDialog(mContext);
			}
		}

	}

	class SendReportCall extends Thread {

		String ReportDetails;

		public SendReportCall(String ReportDetails) {

			this.ReportDetails = ReportDetails;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub

			String[] name = { "reportDetails" };
			String[] value = { ReportDetails };

			String response = JSONParser.postRequest(Constant.SERVER_URL + Constant.SEND_REPORT, name, value, preferences.getString("token", null));

//			Log.v(Constant.TAG, "SendReportCall response " + response);

			if (response != null) {

				try {

					JSONObject jsonObject = new JSONObject(response);

					if (jsonObject.has("error")) {
						sErrorMessage = jsonObject.getString("error");
					}

					if (sErrorMessage != null && !sErrorMessage.equals("")) {
						handlerSendReport.sendEmptyMessage(0);
					} else {
						handlerSendReport.sendEmptyMessage(1);
					}

				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
					handlerSendReport.sendEmptyMessage(0);
				}

			} else {

				handlerSendReport.sendEmptyMessage(0);
			}

		}
	}

	Handler handlerSendReport = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			Utility.HideDialog(mContext);

			if (msg.what == 1) {
				editReport.setText("");
				final Dialog dialog = new Dialog(mContext, R.style.DialogAnimationTheme);
				dialog.setContentView(R.layout.dialog_report_abuse);

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
