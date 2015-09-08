package com.mobstar.upload;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

import com.mobstar.AddContentTypeActivity;
import com.mobstar.R;
import com.mobstar.custom.CustomTextviewBold;
import com.mobstar.utils.Constant;

public class SelectProfileContentType extends Activity implements OnClickListener{
	
	private Context mContext;
	private CustomTextviewBold btnCaptureNow,btnUploadPhone,btnBack;
	private int IMG_PICKER_SELECT=29;
	private int VIDEO_PICKER_SELECT=31;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_profile_content_type);
		mContext=SelectProfileContentType.this;
		initControlls();
		
	}

	private void initControlls() {
		btnCaptureNow=(CustomTextviewBold)findViewById(R.id.btnCaptureNow);
		btnCaptureNow.setOnClickListener(this);
		btnUploadPhone=(CustomTextviewBold)findViewById(R.id.btnUploadPhone);
		btnUploadPhone.setOnClickListener(this);
		btnBack=(CustomTextviewBold)findViewById(R.id.btnBack);
		btnBack.setOnClickListener(this);
		
	}

	@Override
	public void onClick(View v) {
		if(v == btnCaptureNow){
			Intent intent = new Intent(mContext, AddContentTypeActivity.class);
			intent.putExtra("categoryId", Constant.PROFILE_CATEGORYID);
			startActivity(intent);
			finish();
			overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
		}
		else if(v == btnUploadPhone){
			Intent intent = new Intent(mContext, AddContentTypeActivity.class);
			intent.putExtra("categoryId", Constant.PROFILE_CATEGORYID);
			intent.putExtra("isGallery",true);
			startActivity(intent);
			finish();
			overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
		}
		else if(v == btnBack){
			onBackPressed();
		}
		
	}
	
//	public Dialog selectFromGalleryDialog() {
//
//		AlertDialog.Builder builder = new AlertDialog.Builder(this);
//
//		CharSequence[] array = {"Image","Video"};
//		builder.setTitle(getString(R.string.select_file_type))
//		.setSingleChoiceItems(array,-1, new DialogInterface.OnClickListener() {
//
//			@Override
//			public void onClick(DialogInterface dialog, int pos) {
////				Log.d("Mobstar","Clicked id is=>"+pos);
//				if(pos==0){
//					Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//					intent.setType("image/*");
//					startActivityForResult(intent, IMG_PICKER_SELECT);
//				}
//				else {
//					Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
//					intent.setType("video/*");
//					startActivityForResult(intent,VIDEO_PICKER_SELECT);
//				}
//				dialog.dismiss();
//			}
//		})
//
//
//		.setNegativeButton(getString(R.string.cancel_), new DialogInterface.OnClickListener() {
//			@Override
//			public void onClick(DialogInterface dialog, int id) {
//				dialog.dismiss();
//			}
//		});
//
//		return builder.create();
//	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode == RESULT_OK) {
			
			 if(requestCode==IMG_PICKER_SELECT){
				Uri selectedMediaUri = data.getData();
				String selectedPath = getPath(selectedMediaUri);
				System.out.println("SELECT_IMG Path : " + selectedPath);
				Log.d("mobstar","img path=>"+selectedPath);
//				File sVideoPath=new File(selectedPath);
				
				Intent intent = new Intent(mContext, UploadFileActivity.class);
				intent.putExtra("categoryId", Constant.PROFILE_CATEGORYID);
				intent.putExtra("file1", selectedPath);
				intent.putExtra("type", "image");
				startActivity(intent);
//				finish();
				overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

			}else if(requestCode==VIDEO_PICKER_SELECT){
				Uri selectedMediaUri = data.getData();
				String selectedPath = getPath(selectedMediaUri);
				System.out.println("SELECT_VIDEO Path : " + selectedPath);
				Log.d("mobstar","video path=>"+selectedPath);
//				File sImagePath=new File(selectedPath);
				
				
				Intent intent = new Intent(mContext, UploadFileActivity.class);
				intent.putExtra("file1", selectedPath);
				intent.putExtra("type", "video");
				intent.putExtra("categoryId", Constant.PROFILE_CATEGORYID);
				startActivity(intent);
//				finish();
				overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
			}
			
		}
	};

	public String getPath(Uri uri) {
		String[] projection = { MediaStore.Images.Media.DATA };
		Cursor cursor = managedQuery(uri, projection, null, null, null);
		int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		cursor.moveToFirst();
		return cursor.getString(column_index);
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		Intent intent = new Intent(mContext, SelectUploadTypeActivity.class);
		startActivity(intent);
		finish();
		overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
	
	}

}
