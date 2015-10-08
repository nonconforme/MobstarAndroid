package com.mobstar.stararea;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;

import com.mobstar.R;
import com.mobstar.custom.CustomTextviewBold;
import com.mobstar.info.report.InformationDetailActivity;
import com.mobstar.pojo.EntryPojo;
import com.mobstar.utils.Utility;

public class MyEntriesInformationActivity extends Activity implements OnClickListener {

	private Context mContext;
	private CustomTextviewBold btnEditInformation, btnEntryInformation, btnDeleteEntry;
	private EntryPojo entryPojo;
	private TextView textUserName, textTime, textDescription;
	private ImageButton btnClose;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_my_entries_information);

		mContext = MyEntriesInformationActivity.this;

		entryPojo = (EntryPojo) getIntent().getSerializableExtra("entry");

		InitControls();
		
		Utility.SendDataToGA("MyEntriesInformation Screen", MyEntriesInformationActivity.this);
	}

	void InitControls() {

		btnClose = (ImageButton) findViewById(R.id.btnClose);
		btnClose.setOnClickListener(this);

		textUserName = (TextView) findViewById(R.id.textUserName);
		textTime = (TextView) findViewById(R.id.textTime);
		textDescription = (TextView) findViewById(R.id.textDescription);

		textUserName.setText(entryPojo.getName());
		textDescription.setText(entryPojo.getDescription());
		textTime.setText(entryPojo.getCreated());

		btnEditInformation = (CustomTextviewBold) findViewById(R.id.btnEditInformation);
		btnEditInformation.setOnClickListener(this);

		btnEntryInformation = (CustomTextviewBold) findViewById(R.id.btnEntryInformation);
		btnEntryInformation.setOnClickListener(this);

		btnDeleteEntry = (CustomTextviewBold) findViewById(R.id.btnDeleteEntry);
		btnDeleteEntry.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()){
			case R.id.btnClose:
				onBackPressed();
				break;
			case R.id.btnEditInformation:
				startEditInformationActivity();
				break;
			case R.id.btnEntryInformation:
				startInformationDetailActivity();
				break;
			case R.id.btnDeleteEntry:
				startDeleteEntryActivity();
				break;
		}
	}

	private void startEditInformationActivity(){
		final Intent intent = new Intent(mContext, EditInformationActivity.class);
		intent.putExtra("entry", entryPojo);
		startActivity(intent);
		overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
		onBackPressed();
	}

	private void startInformationDetailActivity(){
		final Intent intent = new Intent(mContext, InformationDetailActivity.class);
		intent.putExtra("entry", entryPojo);
		startActivity(intent);
		overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
		onBackPressed();
	}

	private void startDeleteEntryActivity(){
		final Intent intent = new Intent(mContext, DeleteEntryActivity.class);
		intent.putExtra("entry", entryPojo);
		startActivity(intent);
		overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
		onBackPressed();
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
	}
}
