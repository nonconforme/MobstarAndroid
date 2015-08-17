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
import android.widget.ImageView;
import android.widget.TextView;

import com.mobstar.R;
import com.mobstar.utils.AppRater;
import com.mobstar.utils.Constant;
import com.mobstar.utils.JSONParser;
import com.mobstar.utils.Utility;
import com.squareup.picasso.Picasso;

public class HomeInformationActivity extends Activity{
	
	private Context mContext;
	private TextView textTitle,textDes;
	private ImageView imgInfo;
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
//		new HomeInfoCall().start();
	}

	private void initControlls() {
		textTitle=(TextView)findViewById(R.id.textTitle);
		textDes=(TextView)findViewById(R.id.textDes);
		imgInfo=(ImageView)findViewById(R.id.imgInfo);
		
		textTitle.setText(title);
		textDes.setText(des);
		Picasso.with(mContext).load(img).into(imgInfo);
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
		Intent intent = new Intent(mContext,HomeActivity.class);
		startActivity(intent);
		finish();
	};
	

}
