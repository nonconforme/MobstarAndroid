package com.mobstar.home;

import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.mobstar.R;
import com.mobstar.api.ConnectCallback;
import com.mobstar.api.RestClient;
import com.mobstar.api.responce.*;
import com.mobstar.geo_filtering.SelectCurrentRegionActivity;
import com.mobstar.utils.AppRater;
import com.mobstar.utils.Constant;
import com.mobstar.utils.JSONParser;
import com.mobstar.utils.Utility;
import com.squareup.picasso.Picasso;

public class HomeInformationActivity extends Activity implements View.OnClickListener {
	
	private Context mContext;
	private TextView textTitle,textDes;
	private ImageView imgInfo;
	private ImageButton btnClose;
	private String sErrorMessage;
	private String title="",des="",img="";
	private SharedPreferences preferences;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_home_info);
		mContext=HomeInformationActivity.this;
		preferences = getSharedPreferences("mobstar_pref", Activity.MODE_PRIVATE);

		Bundle bundle=getIntent().getExtras();
		if(bundle!=null){
			if(bundle.containsKey("title")){
				title=bundle.getString("title");
			}
			if(bundle.containsKey("img")){
				img=bundle.getString("img");
			}
			if(bundle.containsKey("des")){
				des=bundle.getString("des");
			}
		}
		
		AppRater.app_launched(mContext);
		initControlls();
		setListeners();
//		new HomeInfoCall().start();
	}

	private void initControlls() {
		textTitle=(TextView)findViewById(R.id.textTitle);
		textDes=(TextView)findViewById(R.id.textDes);
		imgInfo=(ImageView)findViewById(R.id.imgInfo);
		btnClose = (ImageButton) findViewById(R.id.btnClose);
		textTitle.setText(title);
		textDes.setText(des);
		Picasso.with(mContext).load(img).into(imgInfo);
	}

	private void setListeners(){
		btnClose.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()){
			case R.id.btnClose:
				getUserAccountRequest();
				break;
		}
	}

	class HomeInfoCall extends Thread {

		@Override
		public void run() {

			String Query= Constant.SERVER_URL + Constant.HOME_INFO;
			String response = JSONParser.getRequest(Query,preferences.getString("token", null));

//			Log.v(Constant.TAG, "home info response " + response);

			if (response != null) {

				try {
					JSONObject jsonObject = new JSONObject(response);

					if (jsonObject.has("error")) {
						sErrorMessage = jsonObject.getString("error");
					}

					if (sErrorMessage != null && !sErrorMessage.equals("")) {
						handlerInfo.sendEmptyMessage(0);
					} else {
						handlerInfo.sendEmptyMessage(1);
					}

				} catch (Exception e) {
					e.printStackTrace();
					handlerInfo.sendEmptyMessage(0);
				}

			} else {

				handlerInfo.sendEmptyMessage(0);
			}

		}
	}

	void OkayAlertDialog(final String msg) {

		if (!isFinishing()) {
			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);

					// set title
					alertDialogBuilder.setTitle(getResources().getString(R.string.app_name));

					// set dialog message
					alertDialogBuilder.setMessage(msg).setCancelable(false).setNeutralButton("OK", null);

					// create alert dialog
					AlertDialog alertDialog = alertDialogBuilder.create();

					// show it
					alertDialog.show();
				}
			});
		}

	}

	Handler handlerInfo = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			Utility.HideDialog(mContext);

			if (msg.what == 1) {

			} else {
				OkayAlertDialog(sErrorMessage);
			}
		}
	};
	
	public void onBackPressed() {
		getUserAccountRequest();
	};


	private void getUserAccountRequest(){
		Utility.ShowProgressDialog(this, getString(R.string.loading));
		RestClient.getInstance(this).getRequest(Constant.USER_ACCOUNT, null, new ConnectCallback<UserAccountResponse>() {
			@Override
			public void onSuccess(UserAccountResponse object) {
				Utility.HideDialog(HomeInformationActivity.this);
				if (object.getUser().getUserContinentId() == 0){
					startSelectCurrentRegionActivity();
				}
				else startHomeActivity();
			}

			@Override
			public void onFailure(String error) {
				Utility.HideDialog(HomeInformationActivity.this);
				startHomeActivity();
			}

			@Override
			public void onServerError(com.mobstar.api.responce.Error error) {

			}
		});
	}

	private void startSelectCurrentRegionActivity(){
		final Intent intent = new Intent(this, SelectCurrentRegionActivity.class);
		startActivity(intent);
		finish();
	}

	private void startHomeActivity(){
		Intent intent = new Intent(mContext, HomeActivity.class);
		startActivity(intent);
		finish();
	}
	

}
