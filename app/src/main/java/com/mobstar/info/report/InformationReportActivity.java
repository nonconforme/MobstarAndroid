package com.mobstar.info.report;

import org.apache.commons.lang3.StringEscapeUtils;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mobstar.R;
import com.mobstar.custom.RoundedTransformation;
import com.mobstar.pojo.EntryPojo;
import com.mobstar.stararea.DeleteEntryActivity;
import com.mobstar.utils.Constant;
import com.mobstar.utils.JSONParser;
import com.mobstar.utils.Utility;
import com.squareup.picasso.Picasso;

public class InformationReportActivity extends Activity {

	private Context mContext;

	private ImageView btnReportThis, btnEntryInformation,btnDeleteEntry;

	private EntryPojo entryPojo;
	private ImageView imgUserPic;
	private TextView textUserName, textTime, textDescription;
	private String sErrorMessage,UserID;
	private SharedPreferences preferences;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_information_report);

		mContext = InformationReportActivity.this;
		
		preferences = getSharedPreferences("mobstar_pref", Activity.MODE_PRIVATE);

		UserID = preferences.getString("userid", "");

		entryPojo = (EntryPojo) getIntent().getSerializableExtra("entry");

		InitControls();
		
		Utility.SendDataToGA("InformationReport Screen", InformationReportActivity.this);
	}

	void InitControls() {

		textUserName = (TextView) findViewById(R.id.textUserName);
		textTime = (TextView) findViewById(R.id.textTime);
		textDescription = (TextView) findViewById(R.id.textDescription);

		textUserName.setText(entryPojo.getName());
		textDescription.setText(Utility.unescape_perl_string(entryPojo.getDescription()));
		textTime.setText(entryPojo.getCreated());

		btnReportThis = (ImageView) findViewById(R.id.btnReportThis);
		btnReportThis.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(entryPojo!=null){
					Intent intent = new Intent(mContext, ReportActivity.class);
					intent.putExtra("entry", entryPojo);
					startActivity(intent);
					overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
					onBackPressed();
				}
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
		
		btnDeleteEntry=(ImageView)findViewById(R.id.btnDeleteEntry);
		if(entryPojo.getUserID().equalsIgnoreCase(UserID)){
			btnDeleteEntry.setVisibility(View.VISIBLE);
			btnDeleteEntry.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(mContext, DeleteEntryActivity.class);
					intent.putExtra("entry", entryPojo);
					startActivity(intent);
					overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
					onBackPressed();
//					Utility.ShowProgressDialog(mContext, "Loading");
//
//					if (Utility.isNetworkAvailable(mContext)) {
//						Log.d("mobstar","DELETE ENTRY ID"+entryPojo.getID());
//						new DeleteEntryCall(entryPojo.getID()).start();
//
//					} else {
//
//						Toast.makeText(mContext, getString(R.string.no_internet_access), Toast.LENGTH_SHORT).show();
//						Utility.HideDialog(mContext);
//					}
					
				}
			});
		}
		else {
			btnDeleteEntry.setVisibility(View.GONE);
		}
		

		imgUserPic = (ImageView) findViewById(R.id.imgUserPic);

		if (entryPojo.getProfileImage().equals("")) {
			imgUserPic.setImageResource(R.drawable.ic_pic_small);
		} else {
			imgUserPic.setImageResource(R.drawable.ic_pic_small);
			
			Picasso.with(mContext).load(entryPojo.getProfileImage()).resize(Utility.dpToPx(mContext, 45), Utility.dpToPx(mContext, 45)).centerCrop().placeholder(R.drawable.ic_pic_small)
			.error(R.drawable.ic_pic_small).transform(new RoundedTransformation(Utility.dpToPx(mContext, 45), 0)).into(imgUserPic);
			
//			Ion.with(mContext).load(entryPojo.getProfileImage()).withBitmap().placeholder(R.drawable.ic_pic_small).error(R.drawable.ic_pic_small).resize(Utility.dpToPx(mContext, 45), Utility.dpToPx(mContext, 45)).centerCrop().asBitmap().setCallback(new FutureCallback<Bitmap>() {
//
//				@Override
//				public void onCompleted(Exception exception, Bitmap bitmap) {
//					// TODO Auto-generated method stub
//					if (exception == null) {
//						if (bitmap.getWidth() > 0 && bitmap.getHeight() > 0) {
//							Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);
//							Canvas canvas = new Canvas(output);
//
//							final int color = 0xff424242;
//							final Paint paint = new Paint();
//							final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
//
//							paint.setAntiAlias(true);
//							canvas.drawARGB(0, 0, 0, 0);
//							paint.setColor(color);
//							canvas.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2, bitmap.getWidth() / 2, paint);
//							paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
//							canvas.drawBitmap(bitmap, rect, rect, paint);
//
//							imgUserPic.setImageBitmap(output);
//							imgUserPic.invalidate();
//						}
//					}
//				}
//			});
		}
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

//			Log.v(Constant.TAG, "DeleteStarCall response " + response + " EntryId " + EntryId);

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

//				Intent intent = new Intent("star_removed");
//				intent.putExtra("UserID", arrSelectionStarID.get(0));
//				LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
//
//				int tempIndex = -1;
//
//				for (int i = 0; i < arrStarPojos.size(); i++) {
//					if (arrSelectionStarID.get(0).equals(arrStarPojos.get(i).getStarID())) {
//						tempIndex = i;
//						break;
//					}
//				}
//				arrStarPojos.remove(tempIndex);
//				myStarAdapter.notifyDataSetChanged();
//				arrSelectionStarID.remove(0);
//
//				if (arrSelectionStarID.size() == 0) {
//					Utility.HideDialog(mContext);
//				} else {
//					new DeleteStarCall(arrSelectionStarID.get(0)).start();
//				}
				Utility.HideDialog(mContext);
				LocalBroadcastManager.getInstance(InformationReportActivity.this).sendBroadcast(
			            new Intent("entry_deleted"));
				onBackPressed();

			} else {
				Utility.HideDialog(mContext);
				setResult(Activity.RESULT_CANCELED);
				onBackPressed();
			}
		}
	};

}
