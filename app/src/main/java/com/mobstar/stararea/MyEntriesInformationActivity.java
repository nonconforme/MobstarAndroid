package com.mobstar.stararea;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.mobstar.R;
import com.mobstar.info.report.InformationDetailActivity;
import com.mobstar.pojo.EntryPojo;
import com.mobstar.utils.Utility;

public class MyEntriesInformationActivity extends Activity {

	Context mContext;

	ImageView btnEditInformation, btnEntryInformation, btnDeleteEntry;

	EntryPojo entryPojo;

	TextView textUserName, textTime, textDescription;

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

		textUserName = (TextView) findViewById(R.id.textUserName);
		textTime = (TextView) findViewById(R.id.textTime);
		textDescription = (TextView) findViewById(R.id.textDescription);

		textUserName.setText(entryPojo.getName());
		textDescription.setText(entryPojo.getDescription());
		textTime.setText(entryPojo.getCreated());

		btnEditInformation = (ImageView) findViewById(R.id.btnEditInformation);
		btnEditInformation.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mContext, EditInformationActivity.class);
				intent.putExtra("entry", entryPojo);
				startActivity(intent);
				overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
				onBackPressed();

			}
		});

		btnEntryInformation = (ImageView) findViewById(R.id.btnEntryInformation);
		btnEntryInformation.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(mContext, InformationDetailActivity.class);
				intent.putExtra("entry", entryPojo);
				startActivity(intent);
				overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
				onBackPressed();

			}
		});

		btnDeleteEntry = (ImageView) findViewById(R.id.btnDeleteEntry);
		btnDeleteEntry.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(mContext, DeleteEntryActivity.class);
				intent.putExtra("entry", entryPojo);
				startActivity(intent);
				overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
				onBackPressed();

			}
		});
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
	}

}
