package com.mobstar.info.report;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.mobstar.R;
import com.mobstar.custom.CustomTextviewBold;
import com.mobstar.custom.RoundedTransformation;
import com.mobstar.pojo.EntryPojo;
import com.mobstar.utils.Constant;
import com.mobstar.utils.JSONParser;
import com.mobstar.utils.Utility;
import com.squareup.picasso.Picasso;

public class ReportActivity extends Activity implements OnClickListener {

	Context mContext;

	EntryPojo entryPojo;

	TextView textUserName, textTime, textDescription;
	ImageView imgUserPic;

	CustomTextviewBold btnInAppropriateImage, btnInAppropriateLanguage, btnSpam, btnOthers;
	SharedPreferences preferences;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_report);

		mContext = ReportActivity.this;

		entryPojo = (EntryPojo) getIntent().getSerializableExtra("entry");

		preferences = getSharedPreferences("mobstar_pref", Activity.MODE_PRIVATE);

		InitControls();

		Utility.SendDataToGA("Report Screen", ReportActivity.this);
	}

	void InitControls() {

		textUserName = (TextView) findViewById(R.id.textUserName);
		textTime = (TextView) findViewById(R.id.textTime);
		textDescription = (TextView) findViewById(R.id.textDescription);

		if(entryPojo.getName()!=null && entryPojo.getName().length()>0){
			textUserName.setText(entryPojo.getName());
		}
		else if(entryPojo.getUserName()!=null && entryPojo.getUserName().length()>0){
			textUserName.setText(entryPojo.getUserName());
		}
		
		textDescription.setText(Utility.unescape_perl_string(entryPojo.getDescription()));
		textTime.setText(entryPojo.getCreated());

		btnInAppropriateImage = (CustomTextviewBold) findViewById(R.id.btnInAppropriateImage);
		btnInAppropriateImage.setOnClickListener(this);

		btnInAppropriateLanguage = (CustomTextviewBold) findViewById(R.id.btnInAppropriateLanguage);
		btnInAppropriateLanguage.setOnClickListener(this);

		btnSpam = (CustomTextviewBold) findViewById(R.id.btnSpam);
		btnSpam.setOnClickListener(this);

		btnOthers = (CustomTextviewBold) findViewById(R.id.btnOthers);
		btnOthers.setOnClickListener(this);

		imgUserPic = (ImageView) findViewById(R.id.imgUserPic);

		if (entryPojo.getProfileImage().equals("")) {
			imgUserPic.setImageResource(R.drawable.ic_pic_small);
		} else {
			imgUserPic.setImageResource(R.drawable.ic_pic_small);

			Picasso.with(mContext).load(entryPojo.getProfileImage()).resize(Utility.dpToPx(mContext, 45), Utility.dpToPx(mContext, 45)).centerCrop()
					.placeholder(R.drawable.ic_pic_small).error(R.drawable.ic_pic_small).transform(new RoundedTransformation(Utility.dpToPx(mContext, 45), 0)).into(imgUserPic);

		}

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v.equals(btnInAppropriateImage)) {
			OpenReportedDialog();
			new ReportCall(entryPojo.getID(), "inappropriate imagery").start();
		} else if (v.equals(btnInAppropriateLanguage)) {
			OpenReportedDialog();
			new ReportCall(entryPojo.getID(), "inappropriate language").start();
		} else if (v.equals(btnSpam)) {
			OpenReportedDialog();
			new ReportCall(entryPojo.getID(), "spam").start();
		} else if (v.equals(btnOthers)) {
			OpenReportedDialog();
			new ReportCall(entryPojo.getID(), "other").start();
		}
	}

	void OpenReportedDialog() {

		final Dialog dialog = new Dialog(ReportActivity.this, R.style.DialogAnimationTheme);
		dialog.setContentView(R.layout.dialog_reported);
		dialog.show();

		final Timer timer = new Timer();
		TimerTask task = new TimerTask() {

			@Override
			public void run() {
				// TODO Auto-generated method stub

				dialog.dismiss();

				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						onBackPressed();
					}
				});

			}
		};
		timer.schedule(task, 1000);
	}

	class ReportCall extends Thread {

		String Reason, EntryID;

		public ReportCall(String EntryID, String Reason) {
			this.Reason = Reason;
			this.EntryID = EntryID;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub

			String[] name = { "entryIds", "reason" };
			String[] value = { EntryID, Reason };

			String response = JSONParser.postRequest(Constant.SERVER_URL + Constant.REPORT_ENTRY + EntryID, name, value, preferences.getString("token", null));

			// Log.v(Constant.TAG, "ReportCall response " + response);

		}
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
	}
}
