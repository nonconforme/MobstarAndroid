package com.mobstar;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.MediaStore.MediaColumns;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.mobstar.custom.CustomTextviewBold;
import com.mobstar.upload.RecordVideoActivity;
import com.mobstar.upload.SelectProfileContentType;
import com.mobstar.upload.TakePictureActivity;
import com.mobstar.upload.UploadFileActivity;
import com.mobstar.utils.Utility;

public class AddContentTypeActivity extends Activity implements OnClickListener{

	private Context mContext;
	private CustomTextviewBold btnBack, btnImage, btnMovieClip;
	private Typeface typefaceBtn;
	private String categoryId;
	private int IMG_PICKER_SELECT=32;
	private int VIDEO_PICKER_SELECT=33;
	private boolean isGallery=false,FromProfile=false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_add_content_type);

		mContext = AddContentTypeActivity.this;

		Bundle b=getIntent().getExtras();
		if(b!=null) {
			if(b.containsKey("categoryId")) {
				categoryId=b.getString("categoryId");
				isGallery=b.getBoolean("isGallery");
			}
			if(b.containsKey("FromProfile")){
				FromProfile=b.getBoolean("FromProfile");
				
			}
		}

		InitControls();

		Utility.SendDataToGA("AddContentType Screen", AddContentTypeActivity.this);
	}

	void InitControls() {

		typefaceBtn = Typeface.createFromAsset(getAssets(), "GOTHAM-BOLD.TTF");

		btnBack = (CustomTextviewBold) findViewById(R.id.btnBack);

		btnBack.setOnClickListener(this);

		btnImage = (CustomTextviewBold) findViewById(R.id.btnImage);
		btnImage.setOnClickListener(this);


		btnMovieClip = (CustomTextviewBold) findViewById(R.id.btnMovieClip);
		btnMovieClip.setOnClickListener(this);

	}

	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		if (view.equals(btnBack)) {
			onBackPressed();
			
		} else if (view.equals(btnImage)) {
			if(isGallery){
				Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
				intent.setType("image/*");
				startActivityForResult(intent, IMG_PICKER_SELECT);
			}
			else {
				Intent intent = new Intent(mContext, TakePictureActivity.class);
				intent.putExtra("categoryId",categoryId);
				startActivity(intent);
				finish();
				overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
			}

		} else if (btnMovieClip.equals(view)) {
			if(isGallery){
				Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
				intent.setType("video/*");
				startActivityForResult(intent,VIDEO_PICKER_SELECT);
			}
			else {
				Intent intent = new Intent(mContext, RecordVideoActivity.class);
				intent.putExtra("categoryId",categoryId);
				startActivity(intent);
				finish();
				overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
			}

		}
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode == RESULT_OK) {

			if(requestCode==IMG_PICKER_SELECT ){
				if(data!=null && data.getData()!=null){
					Uri selectedMediaUri = data.getData();
					String selectedPath = getPath(selectedMediaUri);
					System.out.println("SELECT_IMG Path : " + selectedPath);
					Log.d("mobstar","img path=>"+selectedPath);
//									File sVideoPath=new File(selectedPath);
					if(selectedPath!=null){
						Intent intent = new Intent(mContext, UploadFileActivity.class);
						intent.putExtra("categoryId",categoryId);
						intent.putExtra("file1", selectedPath);
						intent.putExtra("type", "image");
						startActivity(intent);
						finish();
						overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
					}
					
				}
				

			}else if(requestCode==VIDEO_PICKER_SELECT){
				if(data!=null && data.getData()!=null){
					Uri selectedMediaUri = data.getData();
					String selectedPath = getPath(selectedMediaUri);
					System.out.println("SELECT_VIDEO Path : " + selectedPath);
//					Log.d("mobstar","video path=>"+selectedPath);
					//				File sImagePath=new File(selectedPath);

					if(selectedPath!=null){
						Intent intent = new Intent(mContext, UploadFileActivity.class);
						intent.putExtra("file1", selectedPath);
						intent.putExtra("type", "video");
						intent.putExtra("categoryId",categoryId);
						startActivity(intent);
						finish();
						overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
					}
					
				}
				
			}

		}
	};

	public String getPath(Uri uri) {
//		String[] projection = { MediaStore.Images.Media.DATA };
//		Cursor cursor = managedQuery(uri, projection, null, null, null);
//		int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
//		cursor.moveToFirst();
//		return cursor.getString(column_index);
		try {
			String[] projection = { MediaColumns.DATA };
		    @SuppressWarnings("deprecation")
		    Cursor cursor = managedQuery(uri, projection, null, null, null);
		    if (cursor != null) {
		        int column_index = cursor.getColumnIndexOrThrow(MediaColumns.DATA);
		        cursor.moveToFirst();
		        return cursor.getString(column_index);
		    } else
		        return null;
		} catch (Exception e) {
			e.printStackTrace();
			 return null;
		}
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		if(!FromProfile){
			Intent intent = new Intent(mContext,SelectProfileContentType.class);
			startActivity(intent);
			finish();
			overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
		}
	
	}

}
