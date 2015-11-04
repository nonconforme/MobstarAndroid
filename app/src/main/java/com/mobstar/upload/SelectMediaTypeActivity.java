package com.mobstar.upload;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.mobstar.R;
import com.mobstar.custom.CustomTextviewBold;
//import com.mobstar.home.youtube.YouTubeListActivity;
import com.mobstar.home.youtube.YouTubeListActivity;
import com.mobstar.utils.Utility;

public class SelectMediaTypeActivity extends Activity implements OnClickListener {

	private Context mContext;
	private CustomTextviewBold btnBack, btnImage, btnAudioClip, btnMovieClip, btnYouTube;
	private Typeface typefaceBtn;
	private String categoryId, subCat;
	private boolean isModelType=false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_select_media_type);

		mContext = SelectMediaTypeActivity.this;

		Bundle b=getIntent().getExtras();
		if(b!=null) {
			if(b.containsKey("categoryId")) {
				categoryId=b.getString("categoryId");
				subCat=b.getString("subCat");
			}
		}

		initControls();

		Utility.SendDataToGA("SelectMediaType Screen", SelectMediaTypeActivity.this);
	}

	private void initControls() {

		typefaceBtn = Typeface.createFromAsset(getAssets(), "GOTHAM-BOLD.TTF");

		btnBack = (CustomTextviewBold) findViewById(R.id.btnBack);

		btnBack.setOnClickListener(this);

		btnImage = (CustomTextviewBold) findViewById(R.id.btnImage);
		btnImage.setOnClickListener(this);

		btnAudioClip = (CustomTextviewBold) findViewById(R.id.btnAudioClip);
		btnAudioClip.setOnClickListener(this);

		btnMovieClip = (CustomTextviewBold) findViewById(R.id.btnMovieClip);
		btnMovieClip.setOnClickListener(this);


		btnYouTube = (CustomTextviewBold) findViewById(R.id.btnYouTube);
		btnYouTube.setOnClickListener(this);

		if(subCat!=null && subCat.length()>0){
			isModelType=true;
			btnImage.setVisibility(View.GONE);
			btnAudioClip.setVisibility(View.GONE);
		}
		else {
			btnImage.setVisibility(View.VISIBLE);
			btnAudioClip.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		switch (view.getId()){
			case R.id.btnBack:
				onBackPressed();
				break;
			case R.id.btnImage:
				startTakePictureActivity();
				break;
			case R.id.btnAudioClip:
				startRecordAudioActivity();
				break;
			case R.id.btnMovieClip:
				startRecordVideoActivity();
				break;
			case R.id.btnYouTube:
				startYouTubeListActivity();
				break;

		}
	}

	private void startTakePictureActivity(){
		final Intent intent = new Intent(mContext, TakePictureActivity.class);
		intent.putExtra("categoryId",categoryId);
		startActivity(intent);
		finish();
		overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
	}

	private void startRecordAudioActivity(){
		final Intent intent = new Intent(mContext, RecordAudioActivity.class);
		intent.putExtra("categoryId",categoryId);
		startActivity(intent);
		finish();
		overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
	}

	private void startRecordVideoActivity(){
		final Intent intent = new Intent(mContext, RecordVideoActivity.class);
		intent.putExtra("categoryId",categoryId);
		if(subCat!=null){
			intent.putExtra("subCat",subCat);
		}
		startActivity(intent);
		finish();
		overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
	}


	private void startYouTubeListActivity(){
		final Intent intent = new Intent(mContext, YouTubeListActivity.class);
		intent.putExtra("categoryId",categoryId);
		if(subCat!=null){
			intent.putExtra("subCat",subCat);
		}
		startActivity(intent);
		finish();
		overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		if(isModelType){
			Intent intent = new Intent(mContext, SelectSubCategoryActivity.class);
			intent.putExtra("categoryId",categoryId);
			startActivity(intent);
			finish();
			overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
		}
		else {
			Intent intent = new Intent(mContext, SelectCategoryActivity.class);
			startActivity(intent);
			finish();
			overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
		}
	}

}
