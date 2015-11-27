package com.mobstar.info.report;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.mobstar.R;
import com.mobstar.api.new_api_model.EntryP;
import com.mobstar.custom.CustomTextviewBold;
import com.mobstar.custom.RoundedTransformation;
import com.mobstar.utils.Constant;
import com.mobstar.utils.JSONParser;
import com.mobstar.utils.Utility;
import com.squareup.picasso.Picasso;

public class ReportActivity extends Activity implements OnClickListener {

	private EntryP entryPojo;
	private TextView textUserName, textTime, textDescription;
	private ImageView imgUserPic;
	private CustomTextviewBold btnInAppropriateImage, btnInAppropriateLanguage, btnSpam, btnOthers;
	private SharedPreferences preferences;
	private ImageButton btnClose;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_report);
		entryPojo = (EntryP) getIntent().getSerializableExtra("entry");

		preferences = getSharedPreferences("mobstar_pref", Activity.MODE_PRIVATE);
		findViews();
		setListeners();
		setupControls();

		Utility.SendDataToGA("Report Screen", ReportActivity.this);
	}

	private void findViews(){
		btnClose                 = (ImageButton) findViewById(R.id.btnClose);
		textUserName             = (TextView) findViewById(R.id.textUserName);
		textTime                 = (TextView) findViewById(R.id.textTime);
		textDescription          = (TextView) findViewById(R.id.textDescription);
		btnInAppropriateImage    = (CustomTextviewBold) findViewById(R.id.btnInAppropriateImage);
		btnInAppropriateLanguage = (CustomTextviewBold) findViewById(R.id.btnInAppropriateLanguage);
		btnSpam                  = (CustomTextviewBold) findViewById(R.id.btnSpam);
		btnOthers                = (CustomTextviewBold) findViewById(R.id.btnOthers);
		imgUserPic               = (ImageView) findViewById(R.id.imgUserPic);

	}

	private void setListeners(){
		btnClose.setOnClickListener(this);
		btnInAppropriateImage.setOnClickListener(this);
		btnInAppropriateLanguage.setOnClickListener(this);
		btnSpam.setOnClickListener(this);
		btnOthers.setOnClickListener(this);
	}

	private void setupControls() {

		if(entryPojo.getUser().getDisplayName() != null && entryPojo.getUser().getDisplayName().length() > 0){
			textUserName.setText(entryPojo.getUser().getDisplayName());
		}
		else if(entryPojo.getUser().getFullName()!=null && entryPojo.getUser().getFullName().length()>0){
			textUserName.setText(entryPojo.getUser().getFullName());
		}
		
		textDescription.setText(Utility.unescape_perl_string(entryPojo.getEntry().getName()));
		textTime.setText(entryPojo.getEntry().getCreatedAgo());

		if (entryPojo.getUser().getProfileImage().equals("")) {
			imgUserPic.setImageResource(R.drawable.ic_pic_small);
		} else {
			imgUserPic.setImageResource(R.drawable.ic_pic_small);

			Picasso
					.with(this)
					.load(entryPojo.getUser().getProfileImage())
					.resize(Utility.dpToPx(this, 45), Utility.dpToPx(this, 45))
					.centerCrop()
					.placeholder(R.drawable.ic_pic_small)
					.error(R.drawable.ic_pic_small)
					.transform(new RoundedTransformation(Utility.dpToPx(this, 45), 0))
					.into(imgUserPic);

		}

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()){
			case R.id.btnInAppropriateImage:
				OpenReportedDialog();
				new ReportCall(entryPojo.getEntry().getId(), "inappropriate imagery").start();
				break;
			case R.id.btnInAppropriateLanguage:
				OpenReportedDialog();
				new ReportCall(entryPojo.getEntry().getId(), "inappropriate language").start();
				break;
			case R.id.btnSpam:
				OpenReportedDialog();
				new ReportCall(entryPojo.getEntry().getId(), "spam").start();
				break;
			case R.id.btnOthers:
				OpenReportedDialog();
				new ReportCall(entryPojo.getEntry().getId(), "other").start();
				break;
			case R.id.btnClose:
				onBackPressed();
				break;

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

				dialog.dismiss();

				runOnUiThread(new Runnable() {

					@Override
					public void run() {
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
