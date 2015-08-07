package com.mobstar.stararea;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mobstar.R;
import com.mobstar.pojo.EntryPojo;
import com.mobstar.utils.Constant;
import com.mobstar.utils.JSONParser;
import com.mobstar.utils.Utility;

public class DeleteEntryActivity extends Activity {

	Context mContext;

	EntryPojo entryPojo;

	TextView textUserName, textTime, textDescription;

	ImageView btnDeleteNo,btnDeleteYes;
	
	SharedPreferences preferences;
	
	String sErrorMessage;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_delete_entry);

		mContext = DeleteEntryActivity.this;
		
		preferences = getSharedPreferences("mobstar_pref", Activity.MODE_PRIVATE);

		entryPojo = (EntryPojo) getIntent().getSerializableExtra("entry");

		InitControls();
		
		Utility.SendDataToGA("DeletEntry Screen", DeleteEntryActivity.this);
		
	}

	void InitControls() {

		textUserName = (TextView) findViewById(R.id.textUserName);
		textTime = (TextView) findViewById(R.id.textTime);
		textDescription = (TextView) findViewById(R.id.textDescription);

		textUserName.setText(entryPojo.getName());
		textDescription.setText(Utility.unescape_perl_string(entryPojo.getDescription()));
		textTime.setText(entryPojo.getCreated());
		
		
		btnDeleteNo=(ImageView)findViewById(R.id.btnDeleteNo);
		btnDeleteNo.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				onBackPressed();
			}
		});
		
		btnDeleteYes=(ImageView)findViewById(R.id.btnDeleteYes);
		btnDeleteYes.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				Utility.ShowProgressDialog(mContext, getString(R.string.loading));

				if (Utility.isNetworkAvailable(mContext)) {
					new DeleteEntryCall(entryPojo.getID()).start();

				} else {

					Toast.makeText(mContext, getString(R.string.no_internet_access), Toast.LENGTH_SHORT).show();
					Utility.HideDialog(mContext);
				}
			}
		});

	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
	}
	
	// added by khyati
		class DeleteEntryCall extends Thread {

			String EntryId;

			public DeleteEntryCall(String EntryId) {
				this.EntryId = EntryId;
			}

			@Override
			public void run() {
				// TODO Auto-generated method stub

				String[] name = { "entry" };
				String[] value = { EntryId };

				String response = JSONParser.deleteRequest(Constant.SERVER_URL + Constant.DELETE_ENTRY+EntryId , null, null, preferences.getString("token", null));

//				Log.v(Constant.TAG, "DeleteStarCall response " + response + " EntryId " + EntryId);

				if (response != null) {

					try {

						JSONObject jsonObject = new JSONObject(response);

						if (jsonObject.has("error")) {
							sErrorMessage = jsonObject.getString("error");
						}

						if (sErrorMessage != null && !sErrorMessage.equals("")) {
							handlerDeleteEntry.sendEmptyMessage(0);
						} else {
							handlerDeleteEntry.sendEmptyMessage(1);
						}

					} catch (Exception e) {
						// TODO: handle exception
						e.printStackTrace();
						handlerDeleteEntry.sendEmptyMessage(0);
					}

				} else {

					handlerDeleteEntry.sendEmptyMessage(0);
				}

			}
		}

		Handler handlerDeleteEntry = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub

				if (msg.what == 1) {
					Utility.HideDialog(mContext);
					LocalBroadcastManager.getInstance(DeleteEntryActivity.this).sendBroadcast(
				            new Intent("entry_deleted").putExtra("deletedEntryId",entryPojo.getID()));
					
					onBackPressed();

				} else {
					Utility.HideDialog(mContext);
					setResult(Activity.RESULT_CANCELED);
					onBackPressed();
				}
			}
		};
}
