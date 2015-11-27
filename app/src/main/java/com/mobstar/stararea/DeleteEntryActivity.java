package com.mobstar.stararea;

import org.json.JSONObject;

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
import android.widget.Toast;
import com.mobstar.R;
import com.mobstar.api.new_api_model.EntryP;
import com.mobstar.utils.Utility;

public class DeleteEntryActivity extends Activity implements OnClickListener {

	private EntryP entryPojo;
	private TextView textUserName, textTime, textDescription;
	private ImageView btnDeleteNo,btnDeleteYes;
	private ImageButton btnClose;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_delete_entry);
		entryPojo = (EntryP) getIntent().getSerializableExtra("entry");
		findViews();
		setListeners();
		setupControls();
		
		Utility.SendDataToGA("DeletEntry Screen", DeleteEntryActivity.this);
		
	}

	private void findViews(){
		textUserName     = (TextView) findViewById(R.id.textUserName);
		textTime         = (TextView) findViewById(R.id.textTime);
		textDescription  = (TextView) findViewById(R.id.textDescription);
		btnDeleteNo      = (ImageView)findViewById(R.id.btnDeleteNo);
		btnClose         = (ImageButton) findViewById(R.id.btnClose);
		btnDeleteYes     = (ImageView)findViewById(R.id.btnDeleteYes);
	}

	private void setListeners(){
		btnClose.setOnClickListener(this);
		btnDeleteNo.setOnClickListener(this);
		btnDeleteYes.setOnClickListener(this);
	}

	private void setupControls() {
		textUserName.setText(entryPojo.getUser().getFullName());
		textDescription.setText(Utility.unescape_perl_string(entryPojo.getEntry().getName()));
		textTime.setText(entryPojo.getEntry().getCreatedAgo());
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()){
			case R.id.btnClose:
			case R.id.btnDeleteNo:
				onBackPressed();
				break;
			case R.id.btnDeleteYes:
				deleteEntry();
				break;
		}
	}

	private void deleteEntry(){
		Utility.ShowProgressDialog(this, getString(R.string.loading));

		if (Utility.isNetworkAvailable(this)) {
			new DeleteEntryCall(entryPojo.getEntry().getId()).start();

		} else {

			Toast.makeText(this, getString(R.string.no_internet_access), Toast.LENGTH_SHORT).show();
			Utility.HideDialog(this);
		}
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
	}

		class DeleteEntryCall extends Thread {

			String EntryId;

			public DeleteEntryCall(String EntryId) {
				this.EntryId = EntryId;
			}

			@Override
			public void run() {
				// TODO Auto-generated method stub

//				String[] name = { "entry" };
//				String[] value = { EntryId };
//
//				String response = JSONParser.deleteRequest(Constant.SERVER_URL + Constant.DELETE_ENTRY+EntryId , null, null, preferences.getString("token", null));
//
////				Log.v(Constant.TAG, "DeleteStarCall response " + response + " EntryId " + EntryId);
//
//				if (response != null) {
//
//					try {
//
//						JSONObject jsonObject = new JSONObject(response);
//
//						if (jsonObject.has("error")) {
//							sErrorMessage = jsonObject.getString("error");
//						}
//
//						if (sErrorMessage != null && !sErrorMessage.equals("")) {
//							handlerDeleteEntry.sendEmptyMessage(0);
//						} else {
//							handlerDeleteEntry.sendEmptyMessage(1);
//						}
//
//					} catch (Exception e) {
//						// TODO: handle exception
//						e.printStackTrace();
//						handlerDeleteEntry.sendEmptyMessage(0);
//					}
//
//				} else {
//
//					handlerDeleteEntry.sendEmptyMessage(0);
//				}
//
			}
		}

		Handler handlerDeleteEntry = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub

				if (msg.what == 1) {
					Utility.HideDialog(DeleteEntryActivity.this);
					LocalBroadcastManager.getInstance(DeleteEntryActivity.this).sendBroadcast(
				            new Intent("entry_deleted").putExtra("deletedEntryId",entryPojo.getEntry().getId()));
					
					onBackPressed();

				} else {
					Utility.HideDialog(DeleteEntryActivity.this);
					setResult(Activity.RESULT_CANCELED);
					onBackPressed();
				}
			}
		};
}
