package com.mobstar.upload;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import com.mobstar.R;
import com.mobstar.custom.CustomTextviewBold;
import com.mobstar.utils.Utility;

public class SelectUploadTypeActivity extends Activity implements OnClickListener{
	
	private CustomTextviewBold btnEnterMobstar,btnUploadProfile,btnNewMessage;
	private Context mContext;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_select_upload_type);
		mContext=SelectUploadTypeActivity.this;
		initControlls();
		Utility.SendDataToGA("SelectUploadType Screen", SelectUploadTypeActivity.this);
	}

	private void initControlls() {
		btnEnterMobstar=(CustomTextviewBold)findViewById(R.id.btnEnterMobstar);
		btnEnterMobstar.setOnClickListener(this);
		btnUploadProfile=(CustomTextviewBold)findViewById(R.id.btnUploadProfile);
		btnUploadProfile.setOnClickListener(this);
		btnNewMessage=(CustomTextviewBold)findViewById(R.id.btnNewMessage);
		btnNewMessage.setOnClickListener(this);
		
	}

	@Override
	public void onClick(View v) {
		if(v == btnEnterMobstar){
			Intent intent = new Intent(mContext, SelectCategoryActivity.class);
			startActivity(intent);
			finish();
			overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
		}
		else if(v == btnUploadProfile){
			Intent intent = new Intent(mContext, SelectProfileContentType.class);
			startActivity(intent);
			finish();
			overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
		}
		else if(v == btnNewMessage){
			Intent intent = new Intent(mContext,MessageComposeActivity.class);
			startActivity(intent);
			finish();
			overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
			
//			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);
//
//			// set title
//			alertDialogBuilder.setTitle(getResources().getString(R.string.app_name));
//
//			// set dialog message
//			alertDialogBuilder.setMessage(getString(R.string.coming_soon)).setCancelable(false).setNeutralButton("OK", null);
//
//			// create alert dialog
//			AlertDialog alertDialog = alertDialogBuilder.create();
//
//			// show it
//			alertDialog.show();
		}
		
	}
	
	

}
