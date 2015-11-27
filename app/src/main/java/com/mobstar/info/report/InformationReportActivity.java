package com.mobstar.info.report;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import com.mobstar.R;
import com.mobstar.api.new_api_model.EntryP;
import com.mobstar.custom.CustomTextviewBold;
import com.mobstar.custom.RoundedTransformation;
import com.mobstar.stararea.DeleteEntryActivity;
import com.mobstar.utils.UserPreference;
import com.mobstar.utils.Utility;
import com.squareup.picasso.Picasso;

public class InformationReportActivity extends Activity implements OnClickListener {

	private CustomTextviewBold btnReportThis, btnEntryInformation,btnDeleteEntry;
	private EntryP entryPojo;
	private ImageView imgUserPic;
	private TextView textUserName, textTime, textDescription;
	private ImageButton btnClose;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_information_report);
		entryPojo = (EntryP) getIntent().getSerializableExtra("entry");
		findViews();
		setListeners();
		setupControls();
		
		Utility.SendDataToGA("InformationReport Screen", InformationReportActivity.this);
	}

	private void findViews(){
		btnClose            = (ImageButton) findViewById(R.id.btnClose);
		textUserName        = (TextView) findViewById(R.id.textUserName);
		textTime            = (TextView) findViewById(R.id.textTime);
		textDescription     = (TextView) findViewById(R.id.textDescription);
		btnReportThis       = (CustomTextviewBold) findViewById(R.id.btnReportThis);
		btnEntryInformation = (CustomTextviewBold) findViewById(R.id.btnEntryInformation);
		btnDeleteEntry      = (CustomTextviewBold)findViewById(R.id.btnDeleteEntry);
		imgUserPic          = (ImageView) findViewById(R.id.imgUserPic);
	}

	private void setListeners(){
		btnClose.setOnClickListener(this);
		btnReportThis.setOnClickListener(this);
		btnEntryInformation.setOnClickListener(this);
		btnDeleteEntry.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		if (entryPojo == null)
			return;
		switch (v.getId()){
			case R.id.btnClose:
				onBackPressed();
				break;
			case R.id.btnReportThis:
				startReportActivity();
				break;
			case R.id.btnEntryInformation:
				startInformationDetailActivity();
				break;
			case R.id.btnDeleteEntry:
				startDeleteEntryActivity();
				break;
		}
	}

	private void setupControls() {
		textUserName.setText(entryPojo.getUser().getDisplayName());
		textDescription.setText(Utility.unescape_perl_string(entryPojo.getEntry().getName()));
		textTime.setText(entryPojo.getEntry().getCreatedAgo());

		if(entryPojo.getUser().getId().equalsIgnoreCase(UserPreference.getUserId(this))){
			btnDeleteEntry.setVisibility(View.VISIBLE);
		}
		else {
			btnDeleteEntry.setVisibility(View.GONE);
		}

		if (entryPojo.getUser().getProfileImage().equals("")) {
			imgUserPic.setImageResource(R.drawable.ic_pic_small);
		} else {
			imgUserPic.setImageResource(R.drawable.ic_pic_small);
			
			Picasso.with(this)
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
	public void onBackPressed() {
		super.onBackPressed();
		overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
	}

	private void startReportActivity(){
		final Intent intent = new Intent(this, ReportActivity.class);
		intent.putExtra("entry", entryPojo);
		startActivity(intent);
		overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
		onBackPressed();
	}

	private void startInformationDetailActivity(){
		final Intent intent = new Intent(this, InformationDetailActivity.class);
		intent.putExtra("entry", entryPojo);
		startActivity(intent);
		overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
		onBackPressed();
	}

	private void startDeleteEntryActivity(){
		final Intent intent = new Intent(this, DeleteEntryActivity.class);
		intent.putExtra("entry", entryPojo);
		startActivity(intent);
		overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
		onBackPressed();
	}
}
